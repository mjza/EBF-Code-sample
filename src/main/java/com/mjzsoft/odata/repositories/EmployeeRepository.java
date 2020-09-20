package com.mjzsoft.odata.repositories;

import java.util.List;
import java.util.Optional;

import com.mjzsoft.odata.models.Employee;

public interface EmployeeRepository extends BaseRepository<Employee, Integer> {

    List<Employee> findByLastname(String lastname);
    
    Optional<Employee> findById(int id);
    
    public default Optional<Employee> findById(String id){
    	return this.findById(Integer.parseInt(id));
    }
    
    @Override
    public default Class<Employee> getType() {
        return Employee.class;
    }
    
    @Override
    public default int sequence(){
    	return 2;
    }

}