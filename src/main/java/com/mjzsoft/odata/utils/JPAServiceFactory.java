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

import java.net.URI;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

import javax.persistence.EntityManagerFactory;
//import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.olingo.odata2.api.exception.ODataException;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataSingleProcessor;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAContext;
import org.apache.olingo.odata2.jpa.processor.api.ODataJPAServiceFactory;
import org.apache.olingo.odata2.jpa.processor.api.OnJPAWriteContent;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

public class JPAServiceFactory extends ODataJPAServiceFactory {
	private static final Logger logger = LoggerFactory.getLogger(JPAServiceFactory.class);
	public static final String DEFAULT_ENTITY_UNIT_NAME = "Model";
	public static final String ENTITY_MANAGER_FACTORY_ID = "entityManagerFactory";
	public final OnJPAWriteContent onDBWriteContent = new OnDBWriteContent();

	@Override
	public ODataJPAContext initializeODataJPAContext() throws ODataJPARuntimeException {
		logger.info("ODataJPAContext initiated");
		ODataJPAContext oDataJPAContext = getODataJPAContext();		
		// Set current locale if passed in the URL
		ODataContext oDataContext = oDataJPAContext.getODataContext();
		if (oDataContext.getParameter("Accept-Language") == null) {
			try {
				URI uri = oDataContext.getPathInfo().getRequestUri();
				List<NameValuePair> params = URLEncodedUtils.parse(uri, Charset.forName("UTF-8"));
				for (NameValuePair entity : params) {
					if ((entity.getName().equals("sap-language") || entity.getName().equals("language")
							|| entity.getName().equals("lang")) && entity.getValue().trim().length() > 0) {
						String[] localeName = entity.getValue().split("_");
						Locale locale = null;
						if (localeName.length > 1) {
							locale = new Locale.Builder().setLanguageTag(localeName[0]).setRegion(localeName[1])
									.build();
						} else {
							locale = new Locale.Builder().setLanguageTag(localeName[0]).build();
						}
						LocaleContextHolder.setLocale(locale);
						break;
					}
				}
			} catch (ODataException e) {
				e.printStackTrace();
			}
		}
		
		//HttpServletRequest request = (HttpServletRequest) oDataContext.getParameter(ODataContext.HTTP_SERVLET_REQUEST_OBJECT);
		
		EntityManagerFactory factory = (EntityManagerFactory) SpringContextsUtil.getBean(ENTITY_MANAGER_FACTORY_ID);
		oDataJPAContext.setEntityManagerFactory(factory);
		oDataJPAContext.setPersistenceUnitName(DEFAULT_ENTITY_UNIT_NAME);
		oDataJPAContext.setJPAEdmExtension(new JPAEdmExtension());
		ODataContextUtil.setODataContext(oDataJPAContext.getODataContext());
		
		//Register Call Back
		setOnWriteJPAContent(onDBWriteContent); 

		return oDataJPAContext;

	}

	@Override
	public ODataSingleProcessor createCustomODataProcessor(ODataJPAContext oDataJPAContext) {
		logger.info("Start of odata processing");
		return new ODataJPAProcessorUtil(oDataJPAContext);
	}

}
