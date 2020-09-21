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

package com.mjzsoft.odata.services;
/**
 * The is a service for Company entity model.
 */

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
	public Optional<Company> findById(String id) {
		return companyRepository.findById(id);
	}

	// This is an important function that tells the runner class the type of this
	// service.
	// In the other word, what entity this service belongs to.
	@Override
	public Class<Company> getType() {
		return Company.class;
	}
}