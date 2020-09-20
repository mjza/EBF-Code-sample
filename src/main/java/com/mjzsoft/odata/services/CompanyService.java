package com.mjzsoft.odata.services;

import java.util.Optional;

import com.mjzsoft.odata.models.Company;
import com.mjzsoft.odata.repositories.CompanyRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyService implements BaseService<Company> {
	
	@Autowired
    private CompanyRepository companyRepository;
	
	@Override
	public Optional<Company> findById(String id){
		return companyRepository.findById(id);
    }
	
	@Override
    public Class<Company> getType() {
        return Company.class;
    }
}