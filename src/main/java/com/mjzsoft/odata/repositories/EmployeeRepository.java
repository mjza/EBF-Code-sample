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

package com.mjzsoft.odata.repositories;

import java.util.List;
import java.util.Optional;

import com.mjzsoft.odata.models.Employee;

public interface EmployeeRepository extends BaseRepository<Employee, Integer> {

	List<Employee> findByLastname(String lastname);

	Optional<Employee> findById(int id);

	public default Optional<Employee> findById(String id) {
		return this.findById(Integer.parseInt(id));
	}

	@Override
	public default Class<Employee> getType() {
		return Employee.class;
	}

	@Override
	public default int sequence() {
		return 2;
	}

}