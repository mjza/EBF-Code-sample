package com.mjzsoft.odata.repositories;

import java.util.Optional;
import com.mjzsoft.odata.models.Company;

public interface CompanyRepository extends BaseRepository<Company, String> {

    Optional<Company> findById(String id);
    
    @Override
    public default Class<Company> getType() {
        return Company.class;
    }
    
    @Override
    public default int sequence(){
    	return 1;
    }
}