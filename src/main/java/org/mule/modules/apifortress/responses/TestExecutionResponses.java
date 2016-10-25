/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.responses;

import java.util.LinkedList;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Represents the result of an automatch execution
 * @author Simone Pezzano - simone@apifortress.com
 *
 */
@JsonAutoDetect
public class TestExecutionResponses extends LinkedList<TestExecutionResponse> {

    private static final long serialVersionUID = -4901528768696145288L;
    
    private boolean noRun;

    public boolean isNoRun() {
        return noRun;
    }

    public void setNoRun(boolean noRun) {
        this.noRun = noRun;
    }

    public TestExecutionResponses(){
        super();
    }
    
    public boolean equals(Object object){
    	if (!(object instanceof TestExecutionResponses)){
    		return false;
    	}
    	TestExecutionResponses o2 = (TestExecutionResponses) object;
    	if(size() != o2.size()){
    		return false;
    	}
    	for(int i=0;i<size();i++){
    		if(!this.get(i).equals(o2.get(i))){
    			return false;
    		}
    	}
    	return true;
    }
    
    public int hashCode(){
    	return Objects.hash(this.toArray());
    }
}
