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
/**
 * An annotation class that is used to add sap:LineItem annotation in the metadata 
 */

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

// Annotation Type Target
@Target({ FIELD })

// The annotation is available at runtime also
@Retention(RUNTIME)

// Used by SAPUI5 in smart table to show a property in the lists and tables 
public @interface SAPLineItem {

}
