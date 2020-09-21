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
 * The BaseService interface is a generic one that all the service classes must implement it.
 * This interface define some required functions that are needed in Runner class. 
 * The Runner class is responsible for feeding the tables.  
 * However, it has been designed in a way that does not need to change it by adding new models. 
 * For making the Runner class so generic, we needed to define some parent service interface to autowired all the service classes! 
 */

import java.util.Optional;

//The type parameter is the entity model class
public interface BaseService<T> {

	// A function that must be overwritten in child class for finding entities by
	// their id.
	// It is used in Runner class to find related object to foreign keys.
	// However, as you may noticed, the id here is of type string as the data in csv
	// files are string.
	// So it may happen that we need to convert the id and call another findById
	// function.
	public Optional<T> findById(String id);

	// A function that must be overwritten in child class to give the class type of
	// the child repository!
	public Class<T> getType();
}