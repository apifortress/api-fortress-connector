/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.status.StatusLogger;
import org.glassfish.grizzly.utils.BufferInputStream;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.display.Summary;
import org.mule.api.annotations.licensing.RequiresEnterpriseLicense;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.annotations.param.RefOnly;
import org.mule.modules.apifortress.config.ConnectorConfig;
import org.mule.modules.apifortress.responses.ApiFortressResponse;
import org.mule.modules.apifortress.responses.ApiFortressResponses;
import org.mule.util.IOUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * The API Fortress connector allows you to send API payloads to API Fortress for testing
 * @author Simone Pezzano - simone@apifortress.com
 *
 */
@Connector(name = "api-fortress", friendlyName = "API Fortress")
@RequiresEnterpriseLicense(allowEval = true)
public class ApiFortressConnector {

    private static final StatusLogger logger = StatusLogger.getLogger();
    
    /**
     * The global configuration
     */
    @Config
    ConnectorConfig config;
    
    /**
     * The API Fortress logic. As an instance of the connector is deployed for each configuration
     * it is safe to store a reference of this
     */
    private ApiFortress apiFortress;
    
    /**
     * a Jackson object mapper to turn JSON into objects and vice versa
     */
    private static ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Creates an API Fortress logic instance. Since it needs the config element, this cannot be done
     * at constructor time as the config object is injected
     */
    public synchronized void createApiFortress(){
        if(apiFortress == null) {
            apiFortress = new ApiFortress(config);
        }
    }
    /**
     * Runs a single test and returns the result of the testing
     * @param payload the payload, either in the form of string or object
     * @param hook the API Hook url
     * @param testId the test id
     * @param headers a map of the headers
     * @param variables extra variables to be injected in the scope of the test
     * @return an ApiFortressResponse instance 
     * @throws IOException a MalformedUrlException when the provided URL is wrong, an IOException when communication with API Fortress fails.
     * 
     */
    @Processor
    @Summary("Runs one test against the provided data and will return the result of the evaluation")
    public ApiFortressResponse singleTestSynchronous(
            @Default("#[payload]") Object payload,
            @Placement(group = "Settings") @FriendlyName("API Hook") @Summary("The API hook URL. Create one using the API Fortress dashboard") String hook,
            @Placement(group = "Settings") @FriendlyName("Test ID") @Summary("The test ID. You can retrieve it in the API Fortress interstitial page for the test") String testId,
            @Placement(group = "Settings") @Default("#[message.inboundProperties]") @FriendlyName("Headers collection") @Summary("The response headers") @RefOnly Map<String,Object>headers,
            @Placement(group = "Settings") @FriendlyName("Extra variables") @Summary("Extra variables to be injected in the scope of the test") @Optional @RefOnly Map<String,Object>variables) throws IOException {
        if(apiFortress == null) {
            createApiFortress();
        }
        final Object digestedPayload = digestPayload(payload);
        final URL hookUrl = new URL(hook);
        return evaluateResponse(apiFortress.runTest(digestedPayload,sanitizeMap(headers),sanitizeMap(variables),hookUrl, testId, true));
    }
    
    /**
     * Runs a single test but returns the received event, and therefore operating as a pass through in
     * the flow logic
     * @param payload the payload, either in the form of string or object
     * @param hook the API Hook url
     * @param testId the test id
     * @param headers a map of the headers
     * @param variables extra variables to be injected in the scope of the test
     * @return the received payload
     * @throws MalformedURLException when the provided URL is not valid
     */
    @Processor
    @Summary("Runs one test against the provided data and will pass through the original payload")
    public Object singleTestPassthrough(
    		@Default("#[payload]") Object payload,
            @Placement(group = "Settings") @FriendlyName("API Hook") @Summary("The API hook URL. Create one using the API Fortress dashboard") String hook,
            @Placement(group = "Settings") @FriendlyName("Test ID") @Summary("The test ID. You can retrieve it in the API Fortress interstitial page for the test") String testId,
            @Placement(group = "Settings") @Default("#[message.inboundProperties]") @FriendlyName("Headers collection") @Summary("The response headers") @RefOnly Map<String,Object>headers,
            @Placement(group = "Settings") @FriendlyName("Extra variables") @Summary("Extra variables to be injected in the scope of the test") @Optional @RefOnly Map<String,Object>variables) throws MalformedURLException
             {
        if(apiFortress == null) {
            createApiFortress();
        }
        final URL hookUrl = new URL(hook);
        final Object digestedPayload = digestPayload(payload);
        try{
            apiFortress.runTest(digestedPayload,sanitizeMap(headers),sanitizeMap(variables),hookUrl, testId, false);
            
        }catch(Exception e){
            logger.error("Something wrong happened while trying to run the test",e);
        }
        return digestedPayload;
    }
    
    
    /**
     * Runs an automatch testing suite and returns the results
     * @param payload the payload, either in the form of string or object
     * @param hook an API Hook url
     * @param automatch the relative path of the tested endpoint, used by automatch to determine which tests to run
     * @param headers a map of the headers
     * @param variables extra variables to be injected in the scope of the test
     * @return an ApiFortressResponses object
     * @throws IOException a MalformedUrlException when the provided URL is wrong, an IOException when communication with API Fortress fails.
     */
    @Processor
    @Summary("Forwards the payload to the automatch processor which will run tests based on the apif.url inbound attribute and return the evaluation results")
    public ApiFortressResponses automatchSynchronous(
    		@Default("#[payload]") Object payload,
            @Placement(group = "Settings") @FriendlyName("API Hook") @Summary("The API hook URL. Create one using the API Fortress dashboard") String hook,
            @Placement(group = "Settings") @FriendlyName("Automatch path") @Summary("The Automatch path API Fortress uses to determine which tests to run") String automatch,
            @Placement(group = "Settings") @Default("#[message.inboundProperties]") @FriendlyName("Headers collection") @Summary("The response headers") @RefOnly Map<String,Object>headers,
            @Placement(group = "Settings") @FriendlyName("Extra variables") @Summary("Extra variables to be injected in the scope of the test") @Optional @RefOnly Map<String,Object>variables) throws IOException
            {
        if(apiFortress == null) {
            createApiFortress();
        }
        final URL hookUrl = new URL(hook);
        final Object digestedPayload = digestPayload(payload);
        return evaluateResponses(apiFortress.runAutomatch(digestedPayload,sanitizeMap(headers), sanitizeMap(variables), hookUrl,true,automatch));
    }
    
    /**
     * Runs an automatch testing suite but returns the received event, and therefore operating as a passthrough
     * in the flow logic
     * @param payload the payload, either in the form of string or object
     * @param hook an API Hook url
     * @param automatch the relative path of the tested endpoint, used by automatch to determine which tests to run
     * @param headers a map of the headers
     * @param variables extra variables to be injected in the scope of the test
     * @return the received payload
     * @throws MalformedURLException when the provided URL is not valid
     * @throws Exception a MalformedUrlException when the provided URL is not valid. A generic Exception for any other unknown error
     */
    @Processor
    @Summary("Forwards the payload to the automatch process which will run tests based on the apif.url inbound property and return the original payload")
    public Object automatchPassthrough(
    		@Default("#[payload]") Object payload,
            @Placement(group = "Settings") @FriendlyName("API Hook") @Summary("The API hook URL. Create one using the API Fortress dashboard") String hook,
            @Placement(group = "Settings") @FriendlyName("Automatch path") @Summary("The Automatch path API Fortress uses to determine which tests to run") String automatch,
            @Placement(group = "Settings") @Default("#[message.inboundProperties]") @FriendlyName("Headers collection") @Summary("The response headers") @RefOnly Map<String,Object>headers,
            @Placement(group = "Settings") @FriendlyName("Extra variables") @Summary("Extra variables to be injected in the scope of the test") @Optional @RefOnly Map<String,Object>variables) throws MalformedURLException{
        if(apiFortress == null) {
            createApiFortress();
        }
        final URL hookUrl = new URL(hook);
        final Object digestedPayload = digestPayload(payload);
        try{
            apiFortress.runAutomatch(digestedPayload,sanitizeMap(headers),sanitizeMap(variables),hookUrl,true,automatch);
            
        }catch(Exception e){
            logger.error("Something wrong happened while trying to run the test",e);
        }
        return digestedPayload;
    }

    /**
     * 
     * @return the ConnectorConfig instance
     */
    public ConnectorConfig getConfig() {
        return config;
    }

    /**
     * Sets the connector config instance
     * @param config the connector config instance
     */
    public void setConfig(ConnectorConfig config) {
        this.config = config;
    }
    
    /**
     * Evaluates the response from API Fortress for a single test run
     * @param data the response from API Fortress
     * @return an ApiFortressResponse object
     * @throws JsonParseException when the provided data cannot be parsed as JSON
     * @throws JsonMappingException when mapping the JSON data to the ApiFortressResponse class fails
     */
    public static ApiFortressResponse evaluateResponse(String data) throws IOException{
        if(data == null){
            final ApiFortressResponse response = new ApiFortressResponse();
            response.setNoRun(true);
            return response;
        }
        return objectMapper.readValue(data,ApiFortressResponse.class);
    }
    
    /**
     * Evaluates the response from API Fortress for an automatch run
     * @param data the response from API Fortress
     * @return an ApiFortressResponses object
     * @throws JsonParseException when the provided data cannot be parsed as JSON
     * @throws JsonMappingException when mapping the JSON data to the ApiFortressResponses class fails
     */
    public static ApiFortressResponses evaluateResponses(String data) throws IOException{
        if(data == null){
            final ApiFortressResponses response = new ApiFortressResponses();
            response.setNoRun(true);
            return response;
        }
        return objectMapper.readValue(data,ApiFortressResponses.class);
    }
    
    /**
     * As string payloads are passed as buffers, it is convenient to convert them to actual strings before
     * proceeding any further
     * @param payload a payload
     * @return a digested payload
     */
    public static Object digestPayload(Object payload){
        if (payload instanceof BufferInputStream) {
            return IOUtils.toString((InputStream) payload);
        }
        return payload;
    }
    
    /**
     * If the provided map is null, then an empty map is returned
     * @param map a map
     * @return the sanitized map
     */
    public static Map<String,Object> sanitizeMap(Map<String,Object> map){
        if(map == null){
            logger.warn("Collection is null");
            return new HashMap<String,Object>();
        }
        return map;
    }

}