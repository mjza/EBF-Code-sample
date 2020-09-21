/*
 * Licensed under the MIT License
 *
 * Copyright (c) 2020 Mahdi Jaberzadeh Ansari
 * 
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mjzsoft.odata.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.ws.rs.NotFoundException;

import org.apache.olingo.odata2.api.batch.BatchHandler;
import org.apache.olingo.odata2.api.batch.BatchRequestPart;
import org.apache.olingo.odata2.api.batch.BatchResponsePart;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.ep.EntityProvider;
import org.apache.olingo.odata2.api.ep.EntityProviderBatchProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderReadProperties;
import org.apache.olingo.odata2.api.ep.EntityProviderWriteProperties;
import org.apache.olingo.odata2.api.ep.entry.ODataEntry;
import org.apache.olingo.odata2.api.exception.MessageReference;
import org.apache.olingo.odata2.api.exception.ODataBadRequestException;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.exception.ODataInternalServerErrorException;
import org.apache.olingo.odata2.api.exception.ODataNotFoundException;
import org.apache.olingo.odata2.api.exception.ODataNotImplementedException;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.apache.olingo.odata2.api.uri.PathInfo;
import org.apache.olingo.odata2.api.uri.info.GetEntitySetUriInfo;
import org.apache.olingo.odata2.api.uri.info.PostUriInfo;
import org.apache.olingo.odata2.api.uri.info.PutMergePatchUriInfo;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPADefaultProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mjzsoft.odata.repositories.BaseRepository;
import org.apache.commons.io.IOUtils;

public class ODataJPAProcessorUtil extends ODataJPADefaultProcessor {

	private static final Logger logger = LoggerFactory.getLogger(ODataJPAProcessorUtil.class);
	private ByteArrayInputStream byteArrayInputStream;

	public ODataJPAProcessorUtil(ODataJPAContext oDataJPAContext) {
		super(oDataJPAContext);
	}

	// Store the stream data
	private void storeStream(final InputStream inputStream) {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			IOUtils.copy(inputStream, byteArrayOutputStream);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		byte[] bytes = byteArrayOutputStream.toByteArray();
		byteArrayInputStream = new ByteArrayInputStream(bytes);
	}

	// Retrieve the stored data
	private ByteArrayInputStream retreiveStream() {
		byteArrayInputStream.reset();
		return byteArrayInputStream;
	}

	// Override the original batch handler because of error in processing batch with
	// multiple change sets
	@Override
	public ODataResponse executeBatch(final BatchHandler handler, final String contentType, final InputStream content)
			throws ODataException {
		try {
			ODataResponse batchResponse;
			List<BatchResponsePart> batchResponseParts = new ArrayList<BatchResponsePart>();
			PathInfo pathInfo = getContext().getPathInfo();
			EntityProviderBatchProperties batchProperties = EntityProviderBatchProperties.init().pathInfo(pathInfo)
					.build();
			List<BatchRequestPart> batchParts = EntityProvider.parseBatchRequest(contentType, content, batchProperties);

			for (BatchRequestPart batchPart : batchParts) {
				batchResponseParts.add(handler.handleBatchPart(batchPart));
			}
			batchResponse = EntityProvider.writeBatchResponse(batchResponseParts);
			return batchResponse;
		} finally {
			close(true);
		}
	}

	// Override the original batch handler because of error in processing batch with
	// multiple change sets
	@Override
	public BatchResponsePart executeChangeSet(final BatchHandler handler, final List<ODataRequest> requests)
			throws ODataException {
		List<ODataResponse> responses = new ArrayList<ODataResponse>();
		try {
			for (ODataRequest request : requests) {
				ODataResponse response = handler.handleRequest(request);
				if (response.getStatus().getStatusCode() >= HttpStatusCodes.BAD_REQUEST.getStatusCode()) {
					// Roll Back
					oDataJPAContext.getODataJPATransaction().rollback();
					List<ODataResponse> errorResponses = new ArrayList<ODataResponse>(1);
					errorResponses.add(response);
					return BatchResponsePart.responses(errorResponses).changeSet(false).build();
				}
				responses.add(response);
			}
			return BatchResponsePart.responses(responses).changeSet(true).build();
		} catch (Exception e) {
			throw new ODataException("Error on processing request content:" + e.getMessage(), e);
		} finally {
			close(true);
		}
	}

	// Override the create entity function for updating foreign keys!
	@Override
	public ODataResponse createEntity(final PostUriInfo uriParserResultView, final InputStream content,
			final String requestContentType, final String contentType) throws ODataException {
		// Record data for next read
		this.storeStream(content);
		try {
			if (uriParserResultView.getTargetEntitySet().getEntityType().hasStream()) {
				throw new ODataNotImplementedException();
			}
			// find related repository
			List<Object> createdJpaEntity = jpaProcessor.process((GetEntitySetUriInfo) uriParserResultView);
			BaseRepository<?, ?> repository = SpringContextsUtil.getRepository(createdJpaEntity.get(0));
			// fetch body from OData request
			EntityProviderReadProperties properties = EntityProviderReadProperties.init().mergeSemantic(false).build();
			// make an entry from data
			ODataEntry entry = EntityProvider.readEntry(requestContentType, uriParserResultView.getTargetEntitySet(),
					this.retreiveStream(), properties);
			Map<String, Object> data = entry.getProperties();
			this.createItem(repository, data);
			// serialize the entry, Location header is set by OData Library
			return EntityProvider.writeEntry(contentType, uriParserResultView.getStartEntitySet(),
					entry.getProperties(),
					EntityProviderWriteProperties.serviceRoot(getContext().getPathInfo().getServiceRoot()).build());
		} catch (Exception e) {
			logger.error("Couldn't insert by costumized function. Try to insert by default function");
			return super.createEntity(uriParserResultView, this.retreiveStream(), requestContentType, contentType);
		}
	}

	// Override the update entity function for updating foreign keys!
	@Override
	public ODataResponse updateEntity(final PutMergePatchUriInfo uriParserResultView, final InputStream content,
			final String requestContentType, final boolean merge, final String contentType) throws ODataException {
		// Record data for next read
		this.storeStream(content);
		try {
			if (uriParserResultView.getTargetEntitySet().getEntityType().hasStream()) {
				throw new ODataNotImplementedException();
			}
			// find related repository
			List<Object> createdJpaEntity = jpaProcessor.process((GetEntitySetUriInfo) uriParserResultView);
			BaseRepository<?, ?> repository = SpringContextsUtil.getRepository(createdJpaEntity.get(0));
			// Fetch body from OData request
			EntityProviderReadProperties properties = EntityProviderReadProperties.init().mergeSemantic(false).build();
			// make an entry from data
			ODataEntry entry = EntityProvider.readEntry(requestContentType, uriParserResultView.getTargetEntitySet(),
					this.retreiveStream(), properties);
			Map<String, Object> data = entry.getProperties();
			// Get ID to be processed from key predicates
			String id = uriParserResultView.getKeyPredicates().get(0).getLiteral();
			try {
				this.updateItem(repository, data, id);
			} catch (NotFoundException e) {
				// No entry exists for the given Id
				return ODataResponse.status(HttpStatusCodes.NOT_FOUND).build();
			}
			return ODataResponse.status(HttpStatusCodes.NO_CONTENT).build();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Couldn't update by costumized function. Try to update by default function");
			return super.updateEntity(uriParserResultView, this.retreiveStream(), requestContentType, merge,
					contentType);
		}
	}

	// try to create an item in the database
	private void createItem(@SuppressWarnings("rawtypes") BaseRepository repository, Map<String, Object> data)
			throws ODataInternalServerErrorException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, ODataBadRequestException,
			NoSuchFieldException, ClassNotFoundException {
		Class<?> type = repository.getType();
		if (type == null) {
			MessageReference msgRef = ODataNotImplementedException.COMMON.create();
			msgRef.addContent("The type is not set in the repository class.");
			throw new ODataInternalServerErrorException(msgRef);
		}
		// Create an object
		Constructor<?> constructor = type.getConstructor();
		Object object = constructor.newInstance();
		// update the object
		SpringContextsUtil.updateObject(oDataJPAContext, type, object, data);
		// Store the data and update the id
		@SuppressWarnings("unchecked")
		Object persisted = repository.save(object);
		// Update the data map after persisting
		SpringContextsUtil.updateDataMap(oDataJPAContext, type, persisted, data);
	}

	// try to update an existing item in database
	private void updateItem(@SuppressWarnings("rawtypes") BaseRepository repository, Map<String, Object> data,
			String id) throws ODataInternalServerErrorException, ODataBadRequestException, NoSuchFieldException,
			SecurityException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, InstantiationException {
		// Get the course object for the courseID passed as key predicate
		Optional<?> item = repository.findById(id);
		if (item.isPresent()) {
			// Check the type!
			Class<?> type = repository.getType();
			if (type == null) {
				MessageReference msgRef = ODataNotImplementedException.COMMON.create();
				msgRef.addContent("The type is not set in the repository class.");
				throw new ODataInternalServerErrorException(msgRef);
			}
			// Fetch the working object
			Object object = item.get();
			// update the object
			SpringContextsUtil.updateObject(oDataJPAContext, type, object, data);
			// Store the data and update the id
			@SuppressWarnings("unchecked")
			Object persisted = repository.save(object);
			// Update the data map after persisting
			SpringContextsUtil.updateDataMap(oDataJPAContext, type, persisted, data);
		} else {
			// No entry exists for the given Id
			MessageReference msgRef = ODataNotFoundException.ENTITY.create();
			msgRef.addContent("There is no object for passed id: {" + id + "}");
			throw new ODataBadRequestException(msgRef);
		}
	}
}
