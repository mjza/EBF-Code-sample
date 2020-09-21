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

package com.mjzsoft.odata.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

// Annotation Type Target
@Target({ FIELD })

// The annotation is available at runtime also
@Retention(RUNTIME)

// The annotation interface that will be used to extend the odata metadata later in run time
public @interface Sap {
	
	// if false the sorting on the filed will be ignored
	boolean sortable() default false;
	
	// if false the filtering on the filed will be ignored
	boolean filterable() default false;
	
	// if false the field cannot be passed in the creation time (Post request)!
	boolean creatable() default false;

	// if false the field cannot be updated by PUT requests!
	boolean updatable() default false;
	
	// if false the field cannot be cleaned or null pass for in the update time!
	boolean deletable() default false;
}