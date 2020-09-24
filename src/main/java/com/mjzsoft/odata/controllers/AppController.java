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

package com.mjzsoft.odata.controllers;
/**
 * A simple controller class that redirect / requests to /index.html    
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AppController {
	
	@GetMapping("/")
    public String index() {		
        return "redirect:index.html";
    }
}
