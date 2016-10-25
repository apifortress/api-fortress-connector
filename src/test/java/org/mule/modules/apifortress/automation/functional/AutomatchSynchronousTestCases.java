/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.functional;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mule.modules.apifortress.ApiFortressConnector;
import org.mule.modules.apifortress.responses.TestExecutionResponses;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;
import org.mule.tools.devkit.ctf.junit.MinMuleRuntime;
import static org.hamcrest.CoreMatchers.instanceOf;

public class AutomatchSynchronousTestCases extends AbstractTestCase<ApiFortressConnector> {

    
    public AutomatchSynchronousTestCases(){
        super(ApiFortressConnector.class);
    }
    @Test
    @MinMuleRuntime(minversion="3.8.0")
    public void basicSuccess() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
     
        Object returnedEvent = connector.automatchSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                                                                TestDataBuilder.getValidHookEndpoint(),
                                                                TestDataBuilder.getValidAutomatchPath(),
                                                                TestDataBuilder.validHeaders,
                                                                TestDataBuilder.emptyMap);
        
        Assert.assertThat(returnedEvent, instanceOf(TestExecutionResponses.class));
        TestExecutionResponses response = (TestExecutionResponses)returnedEvent;
        Assert.assertEquals(response.size(), 1);
        Assert.assertEquals(response.get(0).getFailuresCount(), 0);
    }
    
    @Test(expected=IOException.class)
    @MinMuleRuntime(minversion="3.8.0")
    public void wrongProject() throws Exception {
        
        getConnector().automatchSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                TestDataBuilder.getValidHookEndpoint()+"25a",
                TestDataBuilder.getValidAutomatchPath(),
                TestDataBuilder.validHeaders,
                TestDataBuilder.emptyMap);
    }
    
    @Test
    @MinMuleRuntime(minversion="3.8.0")
    public void noMatch() throws Exception {
        TestExecutionResponses responses = getConnector().automatchSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                TestDataBuilder.getValidHookEndpoint(),
                TestDataBuilder.getValidAutomatchPath()+"/2",
                TestDataBuilder.validHeaders,
                TestDataBuilder.emptyMap);
        Assert.assertEquals(responses.size(),0);
    }
    
    @Test
    @MinMuleRuntime(minversion="3.8.0")
    public void brokenPayload() throws Exception {
        TestExecutionResponses responses = getConnector().automatchSynchronous(TestDataBuilder.loadValidSuccessInputAsString()+"}{",
                TestDataBuilder.getValidHookEndpoint(),
                TestDataBuilder.getValidAutomatchPath(),
                TestDataBuilder.validHeaders,
                TestDataBuilder.emptyMap);
        Assert.assertEquals(responses.get(0).getFailuresCount(),0);
    }

}
