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

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.olingo.odata2.api.processor.ODataContext;

public class ODataContextUtil {

	private static ThreadLocal<ODataContext> oDataContext = new ThreadLocal<ODataContext>();

	public static void setODataContext(ODataContext c) {
		oDataContext.set(c);
	}

	public static ODataContext getODataContext() {
		return oDataContext.get();
	}

	public static ResourceBundle getResourceBundle(String name, Locale locale) {
		ResourceBundle i18n = null;
		if (oDataContext.get() != null) {
			i18n = ResourceBundle.getBundle(name, locale);
		}
		return i18n;
	}
}
