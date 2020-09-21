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

package com.mjzsoft.odata.models;

import javax.persistence.*;

import com.mjzsoft.odata.annotations.SAPLineItem;
import com.mjzsoft.odata.annotations.Sap;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import org.apache.commons.math3.util.Precision;
import org.hibernate.annotations.ColumnDefault;

/**
 * The persistent class for the companies database table.
 * 
 */
@Entity
@Table(name = "companies")
@NamedQuery(name = "Company.findAll", query = "SELECT c FROM Company c")
public class Company implements Serializable {

	/**
	 * Default serialize version
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Sap(filterable = true, sortable = true, creatable = true, updatable = false)
	@SAPLineItem
	@Column(name = "id", unique = true, nullable = false, length = 255)
	private String id;

	@Sap(filterable = true, sortable = true, creatable = true, updatable = true)
	@SAPLineItem
	@Column(name = "name", nullable = false, length = 255)
	private String name;

	// bi-directional many-to-one association to Employee
	@OneToMany(mappedBy = "company")
	private Set<Employee> employees;

	@Sap(filterable = true, sortable = true, creatable = false, updatable = false, deletable = false)
	@SAPLineItem
	@Column(name = "average_salary", nullable = false, updatable = false, insertable = false)
	@ColumnDefault("0.0")
	private double averageSalary;

	public Company() {
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Employee> getEmployees() {
		return this.employees;
	}

	public void setEmployees(Set<Employee> employees) {
		this.employees = employees;
	}

	public Employee addEmployee(Employee employee) {
		getEmployees().add(employee);
		employee.setCompany(this);

		return employee;
	}

	public Employee removeEmployee(Employee employee) {
		getEmployees().remove(employee);
		employee.setCompany(null);

		return employee;
	}

	public double getAverageSalary() {
		int size = getEmployees() != null ? getEmployees().size() : 0;
		if (size > 0) {
			double sumSalary = 0;
			Iterator<Employee> iterator = getEmployees().iterator();
			while (iterator.hasNext()) {
				Employee employee = iterator.next();
				sumSalary += employee.getSalary();
			}
			return Precision.round((sumSalary / size), 2);
		}
		return this.averageSalary;
	}

	public void setAverageSalary(double averageSalary) {
		this.averageSalary = 0.0;
	}

}