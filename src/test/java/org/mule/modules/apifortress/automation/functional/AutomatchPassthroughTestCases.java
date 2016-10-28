/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.functional;

import java.net.MalformedURLException;

import org.junit.Assert;
import org.junit.Test;
import org.mule.modules.apifortress.ApiFortressConnector;
import org.mule.modules.apifortress.exceptions.ApiFortressBadHookException;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

public class AutomatchPassthroughTestCases extends AbstractTestCase<ApiFortressConnector> {

    public AutomatchPassthroughTestCases(){
        super(ApiFortressConnector.class);
    }
    
    @Test
    public void basicSuccess() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
        String data = TestDataBuilder.loadValidSuccessInputAsString();
     
        Object returnedEvent = connector.automatchPassthrough(data,
                                                                TestDataBuilder.VALID_HOOK_ENDPOINT,
                                                                TestDataBuilder.VALID_AUTOMATCH_PATH,
                                                                TestDataBuilder.VALID_HEADERS,TestDataBuilder.EMPTY_MAP);
        
        Assert.assertEquals(returnedEvent,data);
    }
    
    @Test(expected=ApiFortressBadHookException.class)
    public void verifyBadHookException() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
        String data = TestDataBuilder.loadValidSuccessInputAsString();
     
        connector.automatchPassthrough(data,
                                        "httpz",
                                        TestDataBuilder.VALID_AUTOMATCH_PATH,
                                        TestDataBuilder.VALID_HEADERS,TestDataBuilder.EMPTY_MAP);
        
    }
}
