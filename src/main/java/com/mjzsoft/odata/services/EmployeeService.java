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