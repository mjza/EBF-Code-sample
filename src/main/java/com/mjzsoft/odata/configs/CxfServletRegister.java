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

package com.mjzsoft.odata.configs;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.MultipartConfigElement;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CxfServletRegister {
	@Bean
	public ServletRegistrationBean<CXFNonSpringJaxrsServlet> getODataServletRegistrationBean() {
		ServletRegistrationBean<CXFNonSpringJaxrsServlet> odataServletRegistrationBean = new ServletRegistrationBean<CXFNonSpringJaxrsServlet>(
				new CXFNonSpringJaxrsServlet(), "/odata.svc/*");
		Map<String, String> initParameters = new HashMap<String, String>();
		initParameters.put("javax.ws.rs.Application", "org.apache.olingo.odata2.core.rest.app.ODataApplication");
		initParameters.put("org.apache.olingo.odata2.service.factory", "com.mjzsoft.odata.utils.JPAServiceFactory");
		odataServletRegistrationBean.setMultipartConfig(new MultipartConfigElement(""));
		odataServletRegistrationBean.setInitParameters(initParameters);
		return odataServletRegistrationBean;
	}
}
