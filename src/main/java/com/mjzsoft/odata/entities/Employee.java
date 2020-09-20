package com.mjzsoft.odata.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.mjzsoft.odata.annotations.SAPLineItem;
import com.mjzsoft.odata.annotations.Sap;

@Entity
@Table(name="employees")
public class Employee {

	@Id
	private int id;
	@SAPLineItem
    private String firstName;
	@Sap(filterable=true, sortable=true)
	@SAPLineItem
    private String lastName;

    public Employee() {}

    public Employee(int id, String firstName, String lastName) {
    	this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format(
                "Employee[id=%d, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
}
