/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.responses;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonAutoDetect
@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * Represents the results of a single test execution
 * @author Simone Pezzano - simone@apifortress.com
 *
 */
public class TestExecutionResponse{
    
    /**
     * List of critical failures
     */
    private List<String> criticalFailures;
    
    /**
     * Number of failed assertions
     */
    private int failuresCount;
    
    /**
     * Number of warnings
     */
    private int warningsCount;
    
    /**
     * Event date, in milliseconds
     */
    private long date;
    
    /**
     * Which data center performed the operation
     */
    private String location;
    
    /**
     * Test details
     */
    private TestDetails test;
    
    /**
     * project id in API Fortress
     */
    private int projectId;
    
    /**
     * companyId in API Fortress
     */
    private int companyId;
    
    /**
     * true when the threshold limiter avoided the test execution
     */
    private boolean noRun;
    

    public boolean isNoRun() {
        return noRun;
    }


    public void setNoRun(boolean noRun) {
        this.noRun = noRun;
    }


    public String getLocation() {
        return location;
    }


    public void setLocation(String location) {
        this.location = location;
    }


    public long getDate() {
        return date;
    }


    public void setDate(long date) {
        this.date = date;
    }


    public int getWarningsCount() {
        return warningsCount;
    }


    public void setWarningsCount(int warningsCount) {
        this.warningsCount = warningsCount;
    }


    public TestExecutionResponse(){
        super();
    }


    public List<String> getCriticalFailures() {
        return criticalFailures;
    }


    public void setCriticalFailures(List<String> criticalFailures) {
        this.criticalFailures = criticalFailures;
    }


    public int getFailuresCount() {
        return failuresCount;
    }


    public void setFailuresCount(int failuresCount) {
        this.failuresCount = failuresCount;
    }
    
    
    
    public TestDetails getTest() {
        return test;
    }


    public void setTest(TestDetails test) {
        this.test = test;
    }


    public int getProjectId() {
        return projectId;
    }


    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }


    public int getCompanyId() {
        return companyId;
    }


    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }
    
    
    public boolean equals(Object object){
    	if(!(object instanceof TestExecutionResponse)){
    		return false;
    	}
    	TestExecutionResponse o2 = (TestExecutionResponse) object;
    	return companyId==o2.getCompanyId() && projectId==o2.getProjectId() &&
    			failuresCount == o2.getFailuresCount() && warningsCount == o2.getWarningsCount() &&
    			location.equals(o2.getLocation()) && test.equals(o2.getTest());
    }
    
    public int hashCode(){
    	return Objects.hash(companyId,projectId,failuresCount,warningsCount,location,test);
    }
    
    
}
