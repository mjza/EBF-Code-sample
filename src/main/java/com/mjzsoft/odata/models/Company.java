package com.mjzsoft.odata.models;

import javax.persistence.*;

import com.mjzsoft.odata.annotations.Sap;

import java.io.Serializable;
import java.util.Set;

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
	@Sap(filterable=true, sortable=true)
	@Column(name = "id", unique = true, nullable = false, length = 255)
	private String id;
	
	@Sap(filterable=true, sortable=true, creatable=true, updatable=true)
	@Column(name = "name", nullable = false, length = 255)
	private String name;

	// bi-directional many-to-one association to Employee	
	@OneToMany(mappedBy = "company")
	private Set<Employee> employees;

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

}