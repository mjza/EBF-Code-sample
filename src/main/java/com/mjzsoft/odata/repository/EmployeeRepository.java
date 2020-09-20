package com.mjzsoft.odata.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.mjzsoft.odata.entities.Employee;

public interface EmployeeRepository extends CrudRepository<Employee, Integer> {

    List<Employee> findByLastName(String lastName);

}