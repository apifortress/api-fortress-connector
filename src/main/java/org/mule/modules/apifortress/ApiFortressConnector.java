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
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.display.Summary;
import org.mule.api.annotations.licensing.RequiresEnterpriseLicense;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.annotations.param.RefOnly;
import org.mule.modules.apifortress.config.Config;
import org.mule.modules.apifortress.exceptions.ApiFortressBadHookException;
import org.mule.modules.apifortress.exceptions.ApiFortressIOException;
import org.mule.modules.apifortress.exceptions.ApiFortressParseException;
import org.mule.modules.apifortress.responses.TestExecutionResponse;
import org.mule.modules.apifortress.responses.TestExecutionResponses;
import org.mule.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * The API Fortress connector allows you to send API payloads to API Fortress for testing
 * @author Simone Pezzano - simone@apifortress.com
 *
 */
@Connector(name = "api-fortress", friendlyName = "API Fortress", minMuleVersion="3.8.0")
@RequiresEnterpriseLicense(allowEval = true)
public class ApiFortressConnector {

	private static final Logger logger = LoggerFactory.getLogger(ApiFortressConnector.class);
    
    /**
     * The global configuration
     */
    @org.mule.api.annotations.Config
    private Config config;
    
    /**
     * The API Fortress logic. As an instance of the connector is deployed for each configuration
     * it is safe to store a reference of this
     */
    private ApiFortressClient client;
    
    /**
     * a Jackson object mapper to turn JSON into objects and vice versa
     */
    private static ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Creates an API Fortress logic instance. Since it needs the config element, this cannot be done
     * at constructor time as the config object is injected
     */
    public synchronized void createApiFortress(){
        if(client == null) {
            client = new ApiFortressClient(config);
        }
    }
    /**
     * Runs a single test in a synchronous flavor. The connector will wait for the result of the test and set it as payload 
     * @param payload The payload, either in the form of string or object. The valid string formats are JSON, XML or plain text. The object can be anything
     * that can be converted into a JSON
     * @param hook The API Hook URL. You can retrieve this in your API Fortress company account
     * @param testId The test id. It can be found in the test interstitial page, on the API Fortress dashboard
     * @param headers The response headers of the payload it's being tested. 'content-type' is the only mandatory header 
     * @param variables Extra variables to be injected in the scope of the test. Ie. server name, flow name, geographic location, local time
     * @return A TestExecutionResponse object, containing the test result information, such as failures count, warnings count and critical failures
     * @throws ApiFortressParseException When the connector couldn't either convert the payload to JSON or couldn't parse the API Fortress response
     * @throws ApiFortressIOException When the communication with the API Fortress service fails
     * @throws ApiFortressBadHookException When the provided API Hook URL is invalid
     * 
     */
    @Processor
    @Summary("Runs one test against the provided data and will return the result of the evaluation")
    public TestExecutionResponse singleTestSynchronous(
    		@RefOnly @Default("#[payload]") Object payload,
            @Placement(group = "Settings") @FriendlyName("API Hook") @Summary("The API hook URL. Create one using the API Fortress dashboard") String hook,
            @Placement(group = "Settings") @FriendlyName("Test ID") @Summary("The test ID. You can retrieve it in the API Fortress interstitial page for the test") String testId,
            @Placement(group = "Settings") @FriendlyName("Headers collection Reference") @Summary("The response headers") @RefOnly @Default("#[message.inboundProperties]") Map<String,Object>headers,
            @Placement(group = "Settings") @FriendlyName("Extra variables Reference") @Summary("Extra variables to be injected in the scope of the test") @RefOnly @Optional Map<String,Object>variables) throws ApiFortressIOException, ApiFortressParseException, ApiFortressBadHookException {
        if(client == null) {
            createApiFortress();
        }
        final Object digestedPayload = digestPayload(payload);
        final URL hookUrl = toURL(hook);
        return evaluateResponse(client.runTest(digestedPayload,sanitizeMap(headers),sanitizeMap(variables),hookUrl, testId, true));
    }
    
    /**
     * Runs a single test in an asynchronous flavor. The input payload will be put back in the flow untouched once the operation is done,
     * so that further actions can be done on it by the rest of the flow.
     * @param payload The payload, either in the form of string or object. The valid string formats are JSON, XML or plain text. The object can be anything
     * that can be converted into a JSON
     * @param hook The API Hook URL. You can retrieve this in your API Fortress company account
     * @param testId The test id. It can be found in the test interstitial page, on the API Fortress dashboard
     * @param headers The response headers of the payload it's being tested. 'content-type' is the only mandatory header 
     * @param variables Extra variables to be injected in the scope of the test. Ie. server name, flow name, geographic location, local time
     * @param failOnError When set to true, the operation will fail when an I/O exception is raised. Set it to 'false' if the connector is placed in a critical flow 
     * @return The original payload passed to this operation
     * @throws ApiFortressParseException When the connector couldn't either convert the payload to JSON or couldn't parse the API Fortress response
     * @throws ApiFortressIOException When the communication with the API Fortress service fails
     * @throws ApiFortressBadHookException When the provided API Hook URL is invalid
     */
    @Processor
    @Summary("Runs one test against the provided data and will pass through the original payload")
    public Object singleTestPassthrough(
    		@RefOnly @Default("#[payload]") Object payload,
            @Placement(group = "Settings") @FriendlyName("API Hook") @Summary("The API hook URL. Create one using the API Fortress dashboard") String hook,
            @Placement(group = "Settings") @FriendlyName("Test ID") @Summary("The test ID. You can retrieve it in the API Fortress interstitial page for the test") String testId,
            @Placement(group = "Settings") @FriendlyName("Headers collection Reference") @Summary("The response headers") @RefOnly @Default("#[message.inboundProperties]") Map<String,Object>headers,
            @Placement(group = "Settings") @FriendlyName("Extra variables Reference") @Summary("Extra variables to be injected in the scope of the test") @RefOnly @Optional Map<String,Object>variables,
            @Placement(group = "Settings") @FriendlyName("Fail on Error") @Summary("Uncheck if the flow should continue even if an exception raises") @Default("true") boolean failOnError) throws ApiFortressIOException,ApiFortressParseException,ApiFortressBadHookException
             {
        if(client == null) {
            createApiFortress();
        }
        final URL hookUrl = toURL(hook);
        final Object digestedPayload = digestPayload(payload);
        try{
            client.runTest(digestedPayload,sanitizeMap(headers),sanitizeMap(variables),hookUrl, testId, false);
            
        }catch(ApiFortressIOException|ApiFortressParseException exception){
        	if(failOnError)
        		throw exception;
        	else
        		logger.error("An exception was thrown when the request was being sent, therefore no test has been executed. However, since the failOnError flag is set as true, the flow will continue unaffacted",exception);
        }
        return digestedPayload;
    }
    
    
    /**
     * Runs an automatch testing suite in a synchronous way. The connector will wait for the test results from the API Fortress service and return
     * it in the flow as payload.
     * The automatch system will determine which tests need to run based on the automatch pattern.
     * @api.doc http://apifortress.com/doc/automatch/
     * @param payload The payload, either in the form of string or object. The valid string formats are JSON, XML or plain text. The object can be anything
     * that can be converted into a JSON
     * @param hook The API Hook URL. You can retrieve this in your API Fortress company account
     * @param automatch An automatch pattern is a slash separated string, using "*" as wildcard, that describes the endpoint being tested. It will allow the API Fortress system
     * to determine which tests need to run.
     * @param headers The response headers of the payload it's being tested. 'content-type' is the only mandatory header 
     * @param variables Extra variables to be injected in the scope of the test. Ie. server name, flow name, geographic location, local time
     * @return A TestExecutionResponses object, a collection representing all the test execution results for this automatch operation
     * @throws ApiFortressParseException When the connector couldn't either convert the payload to json or couldn't parse the API Fortress response
     * @throws ApiFortressIOException When the communication with the API Fortress service fails  
     * @throws ApiFortressBadHookException When the provided API Hook URL is invalid
     */
    @Processor
    @Summary("Forwards the payload to the automatch processor which will run tests based on the apif.url inbound attribute and return the evaluation results")
    public TestExecutionResponses automatchSynchronous(
    		@RefOnly @Default("#[payload]") Object payload,
            @Placement(group = "Settings") @FriendlyName("API Hook") @Summary("The API hook URL. Create one using the API Fortress dashboard") String hook,
            @Placement(group = "Settings") @FriendlyName("Automatch path") @Summary("The Automatch path API Fortress uses to determine which tests to run") String automatch,
            @Placement(group = "Settings") @FriendlyName("Headers collection Reference") @Summary("The response headers") @RefOnly @Default("#[message.inboundProperties]") Map<String,Object>headers,
            @Placement(group = "Settings") @FriendlyName("Extra variables Reference") @Summary("Extra variables to be injected in the scope of the test") @RefOnly @Optional Map<String,Object>variables) throws ApiFortressParseException, ApiFortressIOException, ApiFortressBadHookException 
            {
        if(client == null) {
            createApiFortress();
        }
        final URL hookUrl = toURL(hook);
        final Object digestedPayload = digestPayload(payload);
        return evaluateResponses(client.runAutomatch(digestedPayload,sanitizeMap(headers), sanitizeMap(variables), hookUrl,true,automatch));
    }
    
    /**
     * Runs an automatch testing suite in an asynchronous way. the API Fortress service will not return the tests result and run them in background.
     * The connector will leave the payload untouched for further operations.
     * The automatch system will determine which tests need to run based on the automatch pattern.
     * @api.doc http://apifortress.com/doc/automatch/
     * @param payload The payload, either in the form of string or object. The valid string formats are JSON, XML or plain text. The object can be anything
     * that can be converted into a JSON
     * @param hook The API Hook URL. You can retrieve this in your API Fortress company account
     * @param automatch An automatch pattern is a slash separated string, using "*" as wildcard, that describes the endpoint being tested. It will allow the API Fortress system
     * to determine which tests need to run
     * @param headers The response headers of the payload it's being tested. 'content-type' is the only mandatory header 
     * @param variables Extra variables to be injected in the scope of the test. Ie. server name, flow name, geographic location, local time
     * @param failOnError When set to true, the operation will fail when an I/O exception is raised. Set it to 'false' if the connector is placed in a critical flow
     * @return The original payload passed to this operation
     * @throws ApiFortressParseException When the connector couldn't either convert the payload to JSON or couldn't parse the API Fortress response
     * @throws ApiFortressIOException When the communication with the API Fortress service fails
     * @throws ApiFortressBadHookException When the provided API Hook URL is invalid
     */
    @Processor
    @Summary("Forwards the payload to the automatch process which will run tests based on the apif.url inbound property and return the original payload")
    public Object automatchPassthrough(
    		@RefOnly @Default("#[payload]") Object payload,
            @Placement(group = "Settings") @FriendlyName("API Hook") @Summary("The API hook URL. Create one using the API Fortress dashboard") String hook,
            @Placement(group = "Settings") @FriendlyName("Automatch path") @Summary("The Automatch path API Fortress uses to determine which tests to run") String automatch,
            @Placement(group = "Settings") @FriendlyName("Headers collection Reference") @Summary("The response headers") @RefOnly @Default("#[message.inboundProperties]") Map<String,Object>headers,
            @Placement(group = "Settings") @FriendlyName("Extra variables Reference") @Summary("Extra variables to be injected in the scope of the test") @RefOnly @Optional Map<String,Object>variables,
    		@Placement(group = "Settings") @FriendlyName("Fail on Error") @Summary("Uncheck if the flow should continue even if an exception raises") @Default("true") boolean failOnError) throws ApiFortressIOException,ApiFortressParseException,ApiFortressBadHookException{
        if(client == null) {
            createApiFortress();
        }
        final URL hookUrl = toURL(hook);
        final Object digestedPayload = digestPayload(payload);
        try{
            client.runAutomatch(digestedPayload,sanitizeMap(headers),sanitizeMap(variables),hookUrl,true,automatch);
            
        }catch(ApiFortressIOException|ApiFortressParseException exception){
        	if(failOnError)
        		throw exception;
        	else
        		logger.error("An exception was thrown when the request was being sent, therefore no test has been executed. However, since the failOnError flag is set as true, the flow will continue unaffacted",exception);
        }
        return digestedPayload;
    }

    /**
     * 
     * @return the ConnectorConfig instance
     */
    public Config getConfig() {
        return config;
    }

    /**
     * Sets the connector config instance
     * @param config the connector config instance
     */
    public void setConfig(Config config) {
        this.config = config;
    }
    
    /**
     * Evaluates the response from API Fortress for a single test run
     * @param data the response from API Fortress
     * @return an ApiFortressResponse object
     * @throws ApiFortressParseException when the response from API Fortress could not be parsed
     */
    public static TestExecutionResponse evaluateResponse(String data) throws ApiFortressParseException {
        if(data == null){
            final TestExecutionResponse response = new TestExecutionResponse();
            response.setNoRun(true);
            return response;
        }
        try{
        	return objectMapper.readValue(data,TestExecutionResponse.class);
        }catch(IOException exception){
        	throw new ApiFortressParseException("Could not parse the response from API Fortress",exception);
        }
    }
    
    /**
     * Evaluates the response from API Fortress for an automatch run
     * @param data the response from API Fortress
     * @return an ApiFortressResponses object
     * @throws ApiFortressParseException when the response from API Fortress could not be parsed
     */
    public static TestExecutionResponses evaluateResponses(String data) throws ApiFortressParseException{
        if(data == null){
            final TestExecutionResponses response = new TestExecutionResponses();
            response.setNoRun(true);
            return response;
        }
        try{
        	return objectMapper.readValue(data,TestExecutionResponses.class);
        }catch(IOException exception){
        	throw new ApiFortressParseException("Could not parse the response from API Fortress",exception);
        }
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
            return new HashMap<>();
        }
        return map;
    }
    
    private static URL toURL(String hook) throws ApiFortressBadHookException{
    	try{
    		return new URL(hook);
    	}catch(MalformedURLException exception){
    		throw new ApiFortressBadHookException(hook,exception);
    	}
    }

}