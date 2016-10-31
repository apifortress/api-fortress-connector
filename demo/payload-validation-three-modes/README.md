# Mule API Fortress Connector Demo

## Introduction
The demo consists of 4 flows, demonstrating the various ways the connector can be used.

* single test input: demonstrates how to run a synchronous test on a provided JSON and return the result fo the execution
* single test endpoint response: shows a more realistic scenario where API Fortress launches an asynchronous test against a resource the flow retrieved using an HTTP call
* automatch endpoint response: another realistic scenario where the API Fortress plarform determines which tests to run based on the automatch string value

## How to run the demo

### Prerequisites
In order to run the demo, you will need:
* Anypoint Studio with Mule ESB 3.8 Runtime
* API Fortress Connector 1.0.0-RELEASE or higher
* You will need an API Fortress cloud paid account or trial to run your own tests. This example will run demo tests as default

### Test the flows
Import the flows using Anypoint Studio, properly configured to run Mule ESB 3.8.
If you have an API Fortress cloud paid account or a trial account, configure the API Hook URL and the test ids according to your data. Otherwise,
leave the example as is to run the demo tests.

* single test input: perform an HTTP POST to http://localhost:8081/single/input with a JSON body. Using the demo test, if the body is `{"sugo":1}` you will get a success.
* single test endpoint response: perfrom an HTTP GET to: http://localhost:8081/single/endpoint_response . The flow will retrieve the resource and test it according to the
settings. The connector is configured to not interfere with the flow and the retrieved data will be sent back to the requesting client, but API Fortress will run the test
and store the results within the cloud.
* automatch endpoint response: perform an HTTP GET to: http://localhost:8081/automatch/response. The flow will retrieve the resource and test it according to the
settings. API Fortress will decide which tests to run based on the Automatch path value. 
The connector is configured to not interfere with the flow and the retrieved data will be sent back to the requesting client, but API Fortress will run the test
and store the results within the cloud.
