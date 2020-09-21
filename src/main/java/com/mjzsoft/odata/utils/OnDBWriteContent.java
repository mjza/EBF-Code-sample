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

// http://olingo.apache.org/doc/odata2/tutorials/HandlingClobAndBlob.html
package com.mjzsoft.odata.utils;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialException;

import org.apache.olingo.odata2.jpa.processor.api.OnJPAWriteContent;
import org.apache.olingo.odata2.jpa.processor.api.exception.ODataJPARuntimeException;

public class OnDBWriteContent implements OnJPAWriteContent {

	@Override
	public Blob getJPABlob(byte[] binaryData) throws ODataJPARuntimeException {
		try {
			return new SerialBlob(binaryData);
		} catch (SerialException e) {
			ODataJPARuntimeException.throwException(ODataJPARuntimeException.INNER_EXCEPTION, e);
		} catch (SQLException e) {
			ODataJPARuntimeException.throwException(ODataJPARuntimeException.INNER_EXCEPTION, e);
		}
		return null;
	}

	@Override
	public Clob getJPAClob(char[] characterData) throws ODataJPARuntimeException {
		try {
			return new SerialClob(characterData);
		} catch (SQLException e) {
			ODataJPARuntimeException.throwException(ODataJPARuntimeException.INNER_EXCEPTION, e);
		}
		return null;
	}
}
