/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.unit;

import java.io.IOException;
import java.util.HashMap;

import org.glassfish.grizzly.memory.ByteBufferManager;
import org.glassfish.grizzly.memory.ByteBufferWrapper;
import org.glassfish.grizzly.utils.BufferInputStream;
import org.junit.Assert;
import org.junit.Test;
import org.mule.modules.apifortress.ApiFortressConnector;
import org.mule.modules.apifortress.automation.functional.TestDataBuilder;
import org.mule.modules.apifortress.responses.ApiFortressResponse;
import org.mule.modules.apifortress.responses.ApiFortressResponses;

public class ApiFortressConnectorTest {

    
    @Test
    public void verifySanitizeMap() {
        Assert.assertEquals(ApiFortressConnector.sanitizeMap(TestDataBuilder.validHeaders),TestDataBuilder.validHeaders);
        Assert.assertEquals(ApiFortressConnector.sanitizeMap(null),new HashMap<String,Object>());
    }
    
    @Test
    public void verifyDigestPayload(){
        ByteBufferWrapper buffer = new ByteBufferManager().allocate(24);
        buffer.put("yay".getBytes());
        BufferInputStream stream = new BufferInputStream(buffer);
        Assert.assertEquals(ApiFortressConnector.digestPayload(stream).getClass().getSimpleName(),"String");
        Assert.assertEquals(ApiFortressConnector.digestPayload(new HashMap<String,Object>()).getClass().getSimpleName(),"HashMap");
    }
    
    @Test
    public void verifyEvaluateResponse() throws IOException{
        ApiFortressResponse response = ApiFortressConnector.evaluateResponse("{\"failuresCount\":1}");
        Assert.assertEquals(response.getFailuresCount(), 1);
        
        response = ApiFortressConnector.evaluateResponse(null);
        Assert.assertEquals(response.isNoRun(), true);

    }
    @Test
    public void verifyEvaluateResponses() throws IOException{
        ApiFortressResponses responses = ApiFortressConnector.evaluateResponses("[{\"failuresCount\":1}]");
        Assert.assertEquals(responses.size(),1);
        Assert.assertEquals(responses.get(0).getFailuresCount(),1);
        
        responses = ApiFortressConnector.evaluateResponses(null);
        Assert.assertEquals(responses.isNoRun(), true);

    }
    
}
