/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.functional;

import org.junit.Assert;
import org.junit.Test;
import org.mule.modules.apifortress.ApiFortressConnector;
import org.mule.modules.apifortress.exceptions.ApiFortressIOException;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

public class SingleTestPassthroughTestCases  extends AbstractTestCase<ApiFortressConnector> {

    public SingleTestPassthroughTestCases() {
        super(ApiFortressConnector.class);
    }

    @Test
    public void basicSuccess() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
        String payload = TestDataBuilder.loadValidSuccessInputAsString();
        Object returnedEvent = connector.singleTestPassthrough(payload,
                                                                TestDataBuilder.VALID_HOOK_ENDPOINT,
                                                                TestDataBuilder.VALID_TEST_ID,
                                                                TestDataBuilder.VALID_HEADERS,
                                                                TestDataBuilder.EMPTY_MAP);
        
        Assert.assertEquals(payload,returnedEvent);
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
}
