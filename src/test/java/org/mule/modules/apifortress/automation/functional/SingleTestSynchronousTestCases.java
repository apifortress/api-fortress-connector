/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.functional;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.mule.modules.apifortress.ApiFortressConnector;
import org.mule.modules.apifortress.responses.TestExecutionResponse;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;
import org.mule.tools.devkit.ctf.junit.MinMuleRuntime;
import static org.hamcrest.CoreMatchers.instanceOf;

public class SingleTestSynchronousTestCases
        extends AbstractTestCase<ApiFortressConnector> {

    public SingleTestSynchronousTestCases() {
        super(ApiFortressConnector.class);
    }

    @Test
    public void basicSuccess() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
     
        Object returnedEvent = connector.singleTestSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                                                                TestDataBuilder.getValidHookEndpoint(),
                                                                TestDataBuilder.getValidTestId(),
                                                                TestDataBuilder.validHeaders,
                                                                TestDataBuilder.emptyMap);
        
        Assert.assertThat(returnedEvent, instanceOf(TestExecutionResponse.class));
        TestExecutionResponse response = (TestExecutionResponse)returnedEvent;
        Assert.assertEquals(response.getFailuresCount(), 0);
        Assert.assertEquals(response.getLocation(),"Ashburn, Virginia");
    }
    
    @Test(expected=IOException.class)
    @MinMuleRuntime(minversion="3.8.0")
    public void wrongProject() throws Exception {
        
        getConnector().singleTestSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                TestDataBuilder.getValidHookEndpoint()+"25a",
                TestDataBuilder.getValidTestId(),
                TestDataBuilder.validHeaders,
                TestDataBuilder.emptyMap);
    }
    
    @Test(expected=IOException.class)
    @MinMuleRuntime(minversion="3.8.0")
    public void wrongTest() throws Exception {
        getConnector().singleTestSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                TestDataBuilder.getValidHookEndpoint(),
                TestDataBuilder.getValidTestId()+"25a",
                TestDataBuilder.validHeaders,
                TestDataBuilder.emptyMap);
    }
    
    @Test
    @MinMuleRuntime(minversion="3.8.0")
    public void brokenPayload() throws Exception {
        TestExecutionResponse response = getConnector().singleTestSynchronous(TestDataBuilder.loadValidSuccessInputAsString()+"}",
                TestDataBuilder.getValidHookEndpoint(),
                TestDataBuilder.getValidTestId(),
                TestDataBuilder.validHeaders,
                TestDataBuilder.emptyMap);
        Assert.assertEquals(response.getFailuresCount(),1);
    }
    

    @Test
    public void noHeaders() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
     
        Object returnedEvent = connector.singleTestSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                                                                TestDataBuilder.getValidHookEndpoint(),
                                                                TestDataBuilder.getValidTestId(),
                                                                new HashMap<String,Object>(),
                                                                TestDataBuilder.emptyMap);
        
        Assert.assertThat(returnedEvent, instanceOf(TestExecutionResponse.class));
        TestExecutionResponse response = (TestExecutionResponse)returnedEvent;
        Assert.assertEquals(response.getFailuresCount(), 2);
        Assert.assertEquals(response.getLocation(),"Ashburn, Virginia");
    }
    
    @Test
    public void nullParams() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
     
        Object returnedEvent = connector.singleTestSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                                                                TestDataBuilder.getValidHookEndpoint(),
                                                                TestDataBuilder.getValidTestId(),
                                                                TestDataBuilder.validHeaders,
                                                                null);
        
        Assert.assertThat(returnedEvent, instanceOf(TestExecutionResponse.class));
        TestExecutionResponse response = (TestExecutionResponse)returnedEvent;
        Assert.assertEquals(response.getFailuresCount(), 0);
        Assert.assertEquals(response.getLocation(),"Ashburn, Virginia");
    }
    
    @Test
    public void nullHeaders() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
     
        Object returnedEvent = connector.singleTestSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                                                                TestDataBuilder.getValidHookEndpoint(),
                                                                TestDataBuilder.getValidTestId(),
                                                                null,
                                                                null);
        
        Assert.assertThat(returnedEvent, instanceOf(TestExecutionResponse.class));
        TestExecutionResponse response = (TestExecutionResponse)returnedEvent;
        Assert.assertEquals(response.getFailuresCount(), 2);
        Assert.assertEquals(response.getLocation(),"Ashburn, Virginia");
    }

}