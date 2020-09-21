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
 * The is a repository for Company entity model.
 * As the Company does not related to any other model it will place 
 * at the first priority row, then we didn't overwrite the `sequence` function here. 
 */

import java.util.Optional;
import com.mjzsoft.odata.models.Company;

// Please notice that Company has a string type id, then the second type parameter that has been passed is string.  
public interface CompanyRepository extends BaseRepository<Company, String> {

	Optional<Company> findById(String id);

	// This is an important function that tells the runner class the type of this
	// repository.
	// In the other word, what entity this repository belongs to.
	@Override
	public default Class<Company> getType() {
		return Company.class;
	}
}