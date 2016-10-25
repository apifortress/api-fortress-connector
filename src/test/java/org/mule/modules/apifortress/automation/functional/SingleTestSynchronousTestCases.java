/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.functional;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.mule.modules.apifortress.ApiFortressConnector;
import org.mule.modules.apifortress.exceptions.ApiFortressIOException;
import org.mule.modules.apifortress.responses.TestExecutionResponse;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

public class SingleTestSynchronousTestCases
        extends AbstractTestCase<ApiFortressConnector> {

    public SingleTestSynchronousTestCases() {
        super(ApiFortressConnector.class);
    }

    @Test
    public void basicSuccess() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
     
        Object returnedEvent = connector.singleTestSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                                                                TestDataBuilder.VALID_HOOK_ENDPOINT,
                                                                TestDataBuilder.VALID_TEST_ID,
                                                                TestDataBuilder.VALID_HEADERS,
                                                                TestDataBuilder.EMPTY_MAP);
        
        Assert.assertThat(returnedEvent, instanceOf(TestExecutionResponse.class));
        TestExecutionResponse response = (TestExecutionResponse)returnedEvent;
        Assert.assertEquals(response.getFailuresCount(), 0);
        Assert.assertEquals(response.getLocation(),"Ashburn, Virginia");
    }
    
    @Test(expected=ApiFortressIOException.class)
    public void wrongProject() throws Exception {
        
        getConnector().singleTestSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                TestDataBuilder.VALID_HOOK_ENDPOINT+"25a",
                TestDataBuilder.VALID_TEST_ID,
                TestDataBuilder.VALID_HEADERS,
                TestDataBuilder.EMPTY_MAP);
    }
    
    @Test(expected=ApiFortressIOException.class)
    public void wrongTest() throws Exception {
        getConnector().singleTestSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                TestDataBuilder.VALID_HOOK_ENDPOINT,
                TestDataBuilder.VALID_TEST_ID+"25a",
                TestDataBuilder.VALID_HEADERS,
                TestDataBuilder.EMPTY_MAP);
    }
    
    /**
     * NOTE: this test is invalid due to an issue in the API Fortress cloud engine
     * 
     */
    /*@Test
    public void brokenPayload() throws Exception {
        TestExecutionResponse response = getConnector().singleTestSynchronous(TestDataBuilder.loadValidSuccessInputAsString()+"}",
                TestDataBuilder.VALID_HOOK_ENDPOINT,
                TestDataBuilder.VALID_TEST_ID,
                TestDataBuilder.VALID_HEADERS,
                TestDataBuilder.EMPTY_MAP);
        Assert.assertEquals(response.getFailuresCount(),1);
    }*/
    

    @Test
    public void noHeaders() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
     
        Object returnedEvent = connector.singleTestSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                                                                TestDataBuilder.VALID_HOOK_ENDPOINT,
                                                                TestDataBuilder.VALID_TEST_ID,
                                                                new HashMap<String,Object>(),
                                                                TestDataBuilder.EMPTY_MAP);
        
        Assert.assertThat(returnedEvent, instanceOf(TestExecutionResponse.class));
        TestExecutionResponse response = (TestExecutionResponse)returnedEvent;
        Assert.assertEquals(response.getFailuresCount(), 2);
        Assert.assertEquals(response.getLocation(),"Ashburn, Virginia");
    }
    
    @Test
    public void nullParams() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
     
        Object returnedEvent = connector.singleTestSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                                                                TestDataBuilder.VALID_HOOK_ENDPOINT,
                                                                TestDataBuilder.VALID_TEST_ID,
                                                                TestDataBuilder.VALID_HEADERS,
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
                                                                TestDataBuilder.VALID_HOOK_ENDPOINT,
                                                                TestDataBuilder.VALID_TEST_ID,
                                                                null,
                                                                null);
        
        Assert.assertThat(returnedEvent, instanceOf(TestExecutionResponse.class));
        TestExecutionResponse response = (TestExecutionResponse)returnedEvent;
        Assert.assertEquals(response.getFailuresCount(), 2);
        Assert.assertEquals(response.getLocation(),"Ashburn, Virginia");
    }

}