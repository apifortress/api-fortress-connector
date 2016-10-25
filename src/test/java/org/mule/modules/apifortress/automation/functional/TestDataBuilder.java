/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.functional;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.mockito.Mockito;
import org.mule.api.MuleMessage;

public class TestDataBuilder {

	
	public static final String VALID_HOOK_ENDPOINT="https://mastiff.apifortress.com/app/api/rest/v3/9e05babb-e332-4715-bba5-a1a487a4b05c324";
	public static final String VALID_TEST_ID="57cd6a4612b87d19658a5b75";
    public static final Map<String,Object> VALID_HEADERS = new HashMap<String,Object>();
    static {
        VALID_HEADERS.put("content-type", "application/json");
        VALID_HEADERS.put("x-mule", "true");
    }
    
    public static final Map<String,Object> EMPTY_MAP = new HashMap<String,Object>();
    
    public static final String VALID_AUTOMATCH_PATH = "/test/1";
    
    
    public static String loadValidSuccessInputAsString() throws IOException{
       return IOUtils.toString(TestDataBuilder.class.getResourceAsStream("/success.json"));
    }
    
    public static void injectInboundProperties(MuleMessage msg,String contentType){
        Mockito.when(msg.getInboundProperty("Content-Type")).thenReturn(contentType);
        Mockito.when(msg.getInboundProperty("x-mule")).thenReturn("true");
    }
    public static void injectOutboundProperties(MuleMessage msg,String contentType){
        Mockito.when(msg.getOutboundProperty("x-mule")).thenReturn("true");
    }
}
