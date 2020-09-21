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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.hibernate.resource.jdbc.spi.StatementInspector;

public class SqlStatementInspector implements StatementInspector {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(SqlStatementInspector.class);

	@Override
	public String inspect(String sql) {
		if (!sql.contains("escape \'\\'")) {
			return sql;
		}
		// OData JPA query correction -> current version (2.0.11) contains
		// the invalid 'escape "\"' statement that delivers no results
		logger.info("Replacing invalid statement: escape \'\\\'");
		return sql.replace("escape \'\\'", "escape \'\\\\'");
	}
}