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

package com.mjzsoft.odata;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.mjzsoft.odata.repositories.BaseRepository;
import com.mjzsoft.odata.services.BaseService;
import com.mjzsoft.odata.utils.SpringContextsUtil;

@Component
public class Runner implements CommandLineRunner {

	public static final String COMMA_DELIMITER = ",";
	private static final Logger logger = LoggerFactory.getLogger(Runner.class);

	private List<BaseService<?>> services;
	private List<BaseRepository<?, ?>> repositories;

	@Autowired
	public Runner(List<BaseService<?>> services, List<BaseRepository<?, ?>> repositories) {

		this.services = services;

		Collections.sort(repositories, (r1, r2) -> {
			return r1.sequence() - r2.sequence();
		});
		this.repositories = repositories;
	}

	@Override
	public void run(String... args) throws Exception {
		for (@SuppressWarnings("rawtypes")
		BaseRepository repo : repositories) {
			this.fillRepository(repo);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void fillRepository(BaseRepository repository) {
		if (repository.count() == 0) {
			Class type = repository.getType();
			if (type == null)
				return;
			String entityName = type.getSimpleName();
			logger.info(entityName + " table in Database is still empty. Adding some sample records from csv file.");
			try {
				InputStream inputStream = getClass().getClassLoader()
						.getResourceAsStream("mockdata/" + entityName + ".csv");
				if (inputStream == null) {
					logger.warn("Couldn't find `" + entityName + ".csv` file in the `/resources/mockdata/` folder!");
					throw new IllegalArgumentException("file not found! " + entityName + ".csv");
				}
				InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
				BufferedReader bufferedReader = new BufferedReader(streamReader);
				CSVReader csvReader = new CSVReader(bufferedReader);
				String[] line;
				boolean firstLine = true;
				List<String> columns = null;
				while ((line = csvReader.readNext()) != null) {
					String[] values = line;
					if (firstLine) {
						firstLine = false;
						columns = Arrays.asList(values);
						continue;
					}

					try {
						Constructor<?> constructor = type.getConstructor();
						Object object = constructor.newInstance();
						for (int i = 0; i < values.length && i < columns.size(); i++) {
							String column = columns.get(i);
							Object value = values[i];
							SpringContextsUtil.updateColumn(type, object, column, value, services);
						}
						repository.save(object);
					} catch (Exception e) {
						e.printStackTrace();
						logger.error("Line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + ": "
								+ e.toString());
						break;
					}
				}
				csvReader.close();
				bufferedReader.close();
			} catch (Exception e) {
				logger.error("Line " + Thread.currentThread().getStackTrace()[1].getLineNumber() + ": " + e.toString());
			}
		}
	}
}