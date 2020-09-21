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

/**
 * The repository for Employee entity model.
 * As the Employee is related to Company model (has a foreign key of companies table) 
 * it will place at the second priority row, then we did overwrite the `sequence` function here. 
 */

import java.util.List;
import java.util.Optional;

import com.mjzsoft.odata.models.Employee;

//Please notice that Employee has a integer type id, then the second type parameter that has been passed is Integer.
public interface EmployeeRepository extends BaseRepository<Employee, Integer> {

	List<Employee> findByLastname(String lastname);

	Optional<Employee> findById(int id);

	// As the id of Employee is integer we parse and convert the string id to
	// integer.
	// This function is also mainly used in Runner class to get rid of conversion
	// complexity in Runner class.
	public default Optional<Employee> findById(String id) {
		return this.findById(Integer.parseInt(id));
	}

	// This is an important function that tells the runner class the type of this
	// repository.
	// In the other word, what entity this repository belongs to.
	@Override
	public default Class<Employee> getType() {
		return Employee.class;
	}

	// Tells the row number of the repository!
	@Override
	public default int sequence() {
		return 2;
	}

}