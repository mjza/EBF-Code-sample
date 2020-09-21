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

package com.mjzsoft.odata.services;

import java.util.Optional;

import com.mjzsoft.odata.models.Employee;
import com.mjzsoft.odata.repositories.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmployeeService implements BaseService<Employee> {
	
	@Autowired
    private EmployeeRepository employeeRepository;
	
	@Override
	public Optional<Employee> findById(String id){
		return employeeRepository.findById(Integer.parseInt(id));
    }
	
	@Override
    public Class<Employee> getType() {
        return Employee.class;
    }
}