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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.mjzsoft.odata.annotations.SAPLineItem;
import com.mjzsoft.odata.annotations.Sap;

@Entity
@Table(name="employees")
@NamedQuery(name="Employee.findAll", query="SELECT e FROM Employee e")
public class Employee implements Serializable {

	/**
	 * Default serialize version
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Sap(filterable=true, sortable=true, creatable=false, updatable=false)
	@Column(name = "id", unique=true, nullable=false)
	private int id;
	
	@Sap(filterable=true, sortable=true, creatable=true, updatable=true)
	@SAPLineItem
	@Column(name = "firstname", nullable=false, length=255)
	private String firstname;
	
	@Sap(filterable=true, sortable=true, creatable=true, updatable=true)
	@SAPLineItem
	@Column(name = "lastname", nullable=false, length=255)
    private String lastname;
	
	@Sap(filterable=true, sortable=true, creatable=true, updatable=true)
	@SAPLineItem
	@Column(name = "email", nullable=true, length=255)
    private String email;
	
	@Sap(filterable=true, sortable=false, creatable=true, updatable=true)
	@SAPLineItem
	@Column(name = "address", nullable=true, length=255)
    private String address;
	
	@Sap(filterable=true, sortable=true, creatable=true, updatable=true)
	@SAPLineItem
	@Column(name = "salary", nullable=false)
    private double salary;	
	
	//bi-directional many-to-one association to Qtype
	@Sap(filterable=true, sortable=true, creatable=true, updatable=true)
	@ManyToOne
	@JoinColumn(name="company_id", referencedColumnName = "id", nullable=false)
	private Company company;
	

    public Employee() {
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}
	
	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
}
