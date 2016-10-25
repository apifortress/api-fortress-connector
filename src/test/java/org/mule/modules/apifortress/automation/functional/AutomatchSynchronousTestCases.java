/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.functional;

import static org.hamcrest.CoreMatchers.instanceOf;

import org.junit.Assert;
import org.junit.Test;
import org.mule.modules.apifortress.ApiFortressConnector;
import org.mule.modules.apifortress.exceptions.ApiFortressIOException;
import org.mule.modules.apifortress.responses.TestExecutionResponses;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

public class AutomatchSynchronousTestCases extends AbstractTestCase<ApiFortressConnector> {

    
    public AutomatchSynchronousTestCases(){
        super(ApiFortressConnector.class);
    }
    @Test
    public void basicSuccess() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
     
        Object returnedEvent = connector.automatchSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                                                                TestDataBuilder.VALID_HOOK_ENDPOINT,
                                                                TestDataBuilder.VALID_AUTOMATCH_PATH,
                                                                TestDataBuilder.VALID_HEADERS,
                                                                TestDataBuilder.EMPTY_MAP);
        
        Assert.assertThat(returnedEvent, instanceOf(TestExecutionResponses.class));
        TestExecutionResponses response = (TestExecutionResponses)returnedEvent;
        Assert.assertEquals(response.size(), 1);
        Assert.assertEquals(response.get(0).getFailuresCount(), 0);
    }
    
    @Test(expected=ApiFortressIOException.class)
    public void wrongProject() throws Exception {
        
        getConnector().automatchSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                TestDataBuilder.VALID_HOOK_ENDPOINT+"25a",
                TestDataBuilder.VALID_AUTOMATCH_PATH,
                TestDataBuilder.VALID_HEADERS,
                TestDataBuilder.EMPTY_MAP);
    }
    
    @Test
    public void noMatch() throws Exception {
        TestExecutionResponses responses = getConnector().automatchSynchronous(TestDataBuilder.loadValidSuccessInputAsString(),
                TestDataBuilder.VALID_HOOK_ENDPOINT,
                TestDataBuilder.VALID_AUTOMATCH_PATH+"/2",
                TestDataBuilder.VALID_HEADERS,
                TestDataBuilder.EMPTY_MAP);
        Assert.assertEquals(responses.size(),0);
    }
    
    @Test
    public void brokenPayload() throws Exception {
        TestExecutionResponses responses = getConnector().automatchSynchronous(TestDataBuilder.loadValidSuccessInputAsString()+"}{",
                TestDataBuilder.VALID_HOOK_ENDPOINT,
                TestDataBuilder.VALID_AUTOMATCH_PATH,
                TestDataBuilder.VALID_HEADERS,
                TestDataBuilder.EMPTY_MAP);
        Assert.assertEquals(responses.get(0).getFailuresCount(),0);
    }

}
