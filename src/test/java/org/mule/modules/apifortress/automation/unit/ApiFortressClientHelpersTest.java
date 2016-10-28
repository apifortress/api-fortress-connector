/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.BufferedInputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mule.modules.apifortress.ApiFortressClient;
import org.mule.modules.apifortress.automation.functional.TestDataBuilder;
import org.mule.modules.apifortress.exceptions.ApiFortressParseException;


public class ApiFortressClientHelpersTest{
	
	@Test
	public void verifyComposeUrl() {
		assertEquals(ApiFortressClient.composeUrl("http://www.foobar.com", "123", "run", true, false, true),"http://www.foobar.com/tests/123/run?sync=true&silent=false&dryrun=true&nosets=true");
		assertEquals(ApiFortressClient.composeUrl("http://www.foobar.com", "123", "automatch", true, false, true),"http://www.foobar.com/tests/automatch?sync=true&silent=false&dryrun=true&nosets=true");
	}
	
	@Test
	public void verifyGetContentType(){
	    Map<String,Object> withHeaders = new HashMap<String,Object>();
	    withHeaders.put("content-type", "application/json");
	    
	    assertEquals(ApiFortressClient.getContentType(withHeaders),"application/json");
	    
	    withHeaders.put("content-type", "text/xml");
        assertEquals(ApiFortressClient.getContentType(withHeaders),"text/xml");
        
        Map<String,Object> empty = new HashMap<String,Object>();
	    assertEquals(ApiFortressClient.getContentType(empty),"text/plain");
	    
	}
	
	@Test
	public void verifyExtractHeaders(){
	    Map<String,Object> withHeaders = new HashMap<String,Object>();
        withHeaders.put("content-type", "application/json");
        withHeaders.put("x-test", "true");
	    Map<String,Object> headers = ApiFortressClient.extractHeaders(withHeaders);
	    assertEquals(headers.size(),2);
	    assertTrue(headers.containsKey("content-type"));
	}
	
	@SuppressWarnings("unchecked")
    @Test
	public void verifyBuildBodyMap() throws Exception{
	    Map<String,Object> body = ApiFortressClient.buildBodyMap("data", TestDataBuilder.VALID_HEADERS, TestDataBuilder.EMPTY_MAP);
	    assertEquals(body.get("payload"), "data");
	    assertEquals(((Map<String, Object>) body.get("params")).size(), 1);
	    assertNotNull(((Map<String,Object>)body.get("params")).get("payload_response"));
	    assertEquals(((Map<String,Object>)((Map<String,Object>)((Map<String,Object>)body.get("params")).get("payload_response")).get("headers")).size(),2);
	}
	
    @Test(expected=ApiFortressParseException.class)
	public void verifyBuildBodyMapFailure() throws Exception{
	    ApiFortressClient.buildBodyMap(new BufferedInputStream(null), TestDataBuilder.VALID_HEADERS, TestDataBuilder.EMPTY_MAP);
	}
	
	@Test
	public void verifyAddUrlToBodyMap() throws Exception{
	   Map<String,Object> body = ApiFortressClient.buildBodyMap("data", TestDataBuilder.VALID_HEADERS, TestDataBuilder.EMPTY_MAP);
	   ApiFortressClient.addUrlToBodyMap(body, "/test/1");
	   assertEquals(body.get("url"), "/test/1");
	}
	
	@Test(expected=IllegalArgumentException.class)
    public void verifyAddUrlToBodyMapNull() throws Exception{
       Map<String,Object> body = ApiFortressClient.buildBodyMap("data", TestDataBuilder.VALID_HEADERS, TestDataBuilder.EMPTY_MAP);
       ApiFortressClient.addUrlToBodyMap(body,null);
    }
}
