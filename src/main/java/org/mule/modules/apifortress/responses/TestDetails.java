/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
/**
 * 
 * Represents a test, made up of an id and a name
 * @author Simone Pezzano - simone@apifortress.com
 *
 */
@JsonAutoDetect
class TestDetails{
    String id;
    String name;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean equals(Object object){
    	if(!(object instanceof TestDetails)){
    		return false;
    	}
    	TestDetails o2 = (TestDetails) object;
    	return id.equals(o2.getId());
    }
    
}