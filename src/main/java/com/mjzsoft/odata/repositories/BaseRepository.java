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

package com.mjzsoft.odata.repositories;
/**
 * The BaseRepository interface is a generic one that all the repository classes must implement it.
 * This interface define some required functions that are needed in Runner class. 
 * The Runner class is responsible for feeding the tables.  
 * However, it has been designed in a way that does not to change it by adding new models. 
 * For making the Runner class we needed to define some parent repository interface to autowired all the models! 
 */

import java.io.Serializable;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

// The first type parameter is the entity model class
// The second type parameter is the type of the ID column 
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
	
	// A function that must be overwritten in child class for finding entities by their id
	// is used in Runner class to find related object to foreign keys. 
	public Optional<T> findById(String id);
	
	// A function that must be overwritten in child class to give the class type of the child repository!
	public Class<T> getType();

	// A function that can be overwritten in child class that tells the repository priority.
	// The sequences are representing the foreign keys dependencies between models.
	// As much as the sequence value is lower, the equivalent model must be fed first. 
	// Therefore, if an entity model is related to a primitive entity model, it must returns 2!
	// So we can assume the models in some priority rows! Each row can contain several models!
	public default int sequence() {
		return 1;
	}
}