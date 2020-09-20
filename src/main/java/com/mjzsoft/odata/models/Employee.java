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
	
	public Company getCompany() {
		return this.company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
}
