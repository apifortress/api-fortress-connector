/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.functional;

import org.junit.Assert;
import org.junit.Test;
import org.mule.modules.apifortress.ApiFortressConnector;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

public class AutomatchPasshtroughTestCases extends AbstractTestCase<ApiFortressConnector> {

    public AutomatchPasshtroughTestCases(){
        super(ApiFortressConnector.class);
    }
    
    @Test
    public void basicSuccess() throws Exception {
        
        
        ApiFortressConnector connector = getConnector();
        String data = TestDataBuilder.loadValidSuccessInputAsString();
     
        Object returnedEvent = connector.automatchPassthrough(data,
                                                                TestDataBuilder.getValidHookEndpoint(),
                                                                TestDataBuilder.getValidAutomatchPath(),
                                                                TestDataBuilder.validHeaders,TestDataBuilder.emptyMap);
        
        Assert.assertEquals(returnedEvent,data);
    }
}
