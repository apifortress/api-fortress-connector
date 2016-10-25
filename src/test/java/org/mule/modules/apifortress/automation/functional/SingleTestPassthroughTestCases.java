/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.functional;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.mule.modules.apifortress.ApiFortressConnector;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;
import org.mule.tools.devkit.ctf.junit.MinMuleRuntime;

public class SingleTestPassthroughTestCases  extends AbstractTestCase<ApiFortressConnector> {

    public SingleTestPassthroughTestCases() {
        super(ApiFortressConnector.class);
    }

    @Test
    @MinMuleRuntime(minversion="3.8.0")
    public void basicSuccess() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
        String payload = TestDataBuilder.loadValidSuccessInputAsString();
        Object returnedEvent = connector.singleTestPassthrough(payload,
                                                                TestDataBuilder.getValidHookEndpoint(),
                                                                TestDataBuilder.getValidTestId(),
                                                                TestDataBuilder.validHeaders,
                                                                TestDataBuilder.emptyMap);
        
        Assert.assertEquals(payload,returnedEvent);
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
}
