/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.mule.modules.apifortress.config.Config;
import org.mule.modules.apifortress.exceptions.ApiFortressIOException;
import org.mule.modules.apifortress.exceptions.ApiFortressParseException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The API Fortress logic
 * @author Simone Pezzano - simone@apifortress.com
 */
@Component
public class ApiFortressClient {
    
    /**
     * Jackson object mapper to perform JSON/Object Object/JSON conversions
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * A pooling connection manager
     */
    private final PoolingHttpClientConnectionManager connectionManager;

    /**
     * an HttpClient
     */
    private CloseableHttpClient client;
    

    /**
     * True when no notifications are to be sent
     */
    private final boolean silent;
    
    /**
     * True when no events have to be stored 
     */
    private final boolean dryRun;
    
    /**
     * The threshold limiter
     */
    private final int threshold;
    
    /**
     * Counts how many tests the current instance processed
     */
    private int counter = 0;
    
    public static final String CT_APPLICATION_JSON = "application/json";
    public static final String CT_TEXT_XML = "text/xml";
    public static final String CT_TEXT_PLAIN = "text/plain";
    
    public static final String HEADER_CONTENT_TYPE = "content-type";
    
    public static final String MODE_RUN = "run";
    public static final String MODE_AUTOMATCH = "automatch";
    
    public static final String ATTR_PARAMS = "params";
    public static final String ATTR_CONTENT_TYPE = "Content-Type";
    public static final String ATTR_PAYLOAD = "payload";
    public static final String ATTR_PAYLOAD_RESPONSE = "payload_response";
    public static final String ATTR_HEADERS = "headers";
    
    /**
     * Constructor
     * @param config the connector config. Won't be referenced
     */
    public ApiFortressClient(Config config) {
        super();
        
        connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(config.getTotalConnections());

        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(config.getConnectTimeout() * 1000)
                .setSocketTimeout(config.getSocketTimeout() * 1000).build();
        client = HttpClients.custom().setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig).build();
        silent = config.isSilent();
        dryRun = config.isDryRun();
        threshold = config.getThreshold();
        
    }

    /**
     * Runs a single test
     * @param payload the payload
     * @param headers the response headers
     * @param params variables to be injected in the scope of the test
     * @param hook the API Hook url
     * @param testId the test id
     * @param synchronous true if the test has to return the result
     * @return The data received from API Fortress, or null if the threshold limiter denied the test execution
     * @throws ApiFortressIOException when the communication with an API Fortress service instance fails 
     * @throws ApiFortressParseException when the connector did not succeed in converting a payload object into JSON
     */
    public String runTest(Object payload, Map<String,Object> headers,Map<String,Object> params,
                                            URL hook, String testId,boolean synchronous) throws ApiFortressIOException, ApiFortressParseException {
        counter++;
        /*
         * Limiter. Only multiple of 'threshold' are processed
         */
        if(counter%threshold!=0){
            return null;
        }
        
        final String url = composeUrl(hook.toString(), testId, MODE_RUN, synchronous, silent, dryRun);
        try{
			final String body = new JSONObject(buildBodyMap(payload,headers,params)).toString();
			return run(url,body);
        }catch(IOException ex){
        	throw new ApiFortressIOException("Could not communicate with API Fortress",ex);
        }
    }
    
    private String run(String url,String body) throws IOException{
    	final HttpPost post = new HttpPost(url);
		final StringEntity entity = new StringEntity(body);
		entity.setContentType(CT_APPLICATION_JSON);
		post.setEntity(entity);
		final HttpResponse response = client.execute(post);
		final int statusCode = response.getStatusLine().getStatusCode();
		
		final HttpEntity responseEntity = response.getEntity();
		final InputStream is = responseEntity.getContent();
		final String dataBack = IOUtils.toString(is);
		is.close();
		EntityUtils.consume(responseEntity);
		
		/**
		 * failure when test execution has been rejected for some reason
		 */
		if (statusCode != 200){
		    throw new ApiFortressIOException("Test execution unsuccessful. Status code="+statusCode);
		}
		
		return dataBack;
    }

    /**
     * Builds the map representing the data to be sent to API Fortress
     * @param payload the payload
     * @param headers the response headers
     * @param params variables to be injected in the scope of the test
     * @return a map representing the message to be sent to API Fortress
     * @throws ApiFortressParseException when the payload object cannot be converted into JSON 
     */
    public static Map<String,Object> buildBodyMap(Object payload,Map<String,Object> headers,Map<String,Object> params) throws ApiFortressParseException {
        final HashMap<String, Object> map = new HashMap<>();
        
        /*
         * if the payload is a string, we can proceed straight forward
         */
        
        if(payload instanceof String){
            map.put(ATTR_PAYLOAD, payload);
        }
        /*
         * If the payload is an object, then we convert it to a string
         */
        else { 
        	try{
            	map.put(ATTR_PAYLOAD,objectMapper.writeValueAsString(payload));
	        }catch(JsonProcessingException ex){
	        	throw new ApiFortressParseException("Could not transform payload object into JSON",ex);
	        }
        }
        /*
         * Content-Type is very important to API Fortress to understand what it has
         * to do
         */
        map.put(ATTR_CONTENT_TYPE, getContentType(headers));
        
        /*
         * Params are extra values you might want in the API Fortress variable
         * scope
         */
        map.put(ATTR_PARAMS, params);

        /*
         * payload_response is the context of the response itself
         */
        final HashMap<String, Object> response = new HashMap<>();
        params.put(ATTR_PAYLOAD_RESPONSE, response);
        
        response.put(ATTR_HEADERS,extractHeaders(headers));
        return map;
    }
    
    /**
     * Given a set of headers, it filters out all the content that shouldn't be sent to API Fortress
     * and returns a clean map of headers
     * @param originalHeaders the provided headers headers
     * @return a map representing the cleaned headers
     */
    public static Map<String,Object> extractHeaders(Map<String,Object> originalHeaders){
        final HashMap<String, Object> headers = new HashMap<>();

        final Iterator<Entry<String,Object>> pxIterator = originalHeaders.entrySet().iterator();
        while(pxIterator.hasNext()){
            final Entry<String,Object> entry = pxIterator.next();
            /*
             * http. , mule.  and apif. are reserved prefixes and will not contain headers.
             */
            String key = entry.getKey();
            final boolean validHeader = !key.startsWith("http.") && !key.startsWith("mule.") && !key.startsWith("apif.");
            
            // If a header prefix has been configured, then only the items with that header prefix will be collected
            if(validHeader){
                headers.put(key,entry.getValue());
            }
        }
        return headers;
    }
    

    /**
     * Adds a URL element in the body to be sent to API Fortress
     * @param map the request to API Fortress, in the form of a map
     * @param automatch the automatch string
     * @return the modified message
     */
    public static Map<String, Object> addUrlToBodyMap(Map<String, Object> map, String automatch) {
        if (StringUtils.isNotEmpty(automatch)) {
            map.put("url", automatch);
        } else { 
            throw new IllegalArgumentException("the apif.url outbound property is required");
        }
        return map;
    }

    /**
     * Retrieves and sanitizes the content type from the headers
     * @param headers the headers map
     * @return the retrieved content-type
     */
    public static String getContentType(Map<String,Object> headers) {
        String contentType = (String) headers.get(HEADER_CONTENT_TYPE);
        if(contentType == null){
            contentType = CT_TEXT_PLAIN;
        } else
        if(contentType.contains(CT_APPLICATION_JSON)){
            contentType = CT_APPLICATION_JSON;
        } else
        if(contentType.contains(CT_TEXT_XML)){
            contentType = CT_TEXT_XML;
        }
        return contentType;
    }
    
    

    /**
     * Runs an automatch test
     * @param payload the payload
     * @param headers the response headers
     * @param params variables to be injected in the scope of the test
     * @param hook the API Hook URL
     * @param synchronous true if the tests need to return the results
     * @return the results from API Fortress or null if the threshold limiter avoided the test execution
     * @throws ApiFortressIOException when the communication with an API Fortress service instance fails 
     * @throws ApiFortressParseException when the connector did not succeed in converting a payload object into JSON
     */
    public String runAutomatch(Object payload,Map<String,Object> headers,Map<String,Object> params, URL hook, boolean synchronous, String automatch) throws ApiFortressIOException, ApiFortressParseException {
        
        counter++;
        /*
         * Limiter. Only multiple of 'threshold' are processed
         */
        if(counter%threshold!=0){
            return null;
        }
        final String url = composeUrl(hook.toString(), null, MODE_AUTOMATCH, synchronous, silent, dryRun);
        try {
	        String body = new JSONObject(addUrlToBodyMap(buildBodyMap(payload,headers,params),automatch)).toString();
	        return run(url, body);
        }catch(IOException ex){
        	throw new ApiFortressIOException("Could not communicate with API Fortress",ex);
        }
    }
    /**
     * Composes the API Fortress URL, based on the settings
     * @param hook the hook URL
     * @param testId the test id
     * @param mode the mode, either run or automatch
     * @param synchronous true if the test needs to return the evaluation result
     * @param silent true if notifications need to be sent
     * @param dryRun true if no event should be saved in API Fortress
     * @return the composed URL
     */
    public static String composeUrl(String hook, String testId, String mode,
            boolean synchronous, boolean silent, boolean dryRun) {
        String url = hook + "/tests/";
        if (mode.equals(MODE_RUN)){
            url += testId + "/";
        }
        url += mode;
        url += ("?sync=" + synchronous + "&silent=" + silent + "&dryrun="
                + dryRun + "&nosets=true");
        return url;
    }

}
