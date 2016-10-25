/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.unit;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.mule.modules.apifortress.responses.TestDetails;
import org.mule.modules.apifortress.responses.TestExecutionResponse;
import org.mule.modules.apifortress.responses.TestExecutionResponses;

public class ApiFortressResponsesTest {

	
	@Test
	public void verifyResponseEqualsAndHashCode(){
		TestDetails details = new TestDetails();
		details.setId("123");
		details.setName("The test");
		TestExecutionResponse resp1 = new TestExecutionResponse();
		resp1.setCompanyId(1);
		resp1.setProjectId(1);
		resp1.setDate(123);
		resp1.setLocation("New England");
		resp1.setFailuresCount(1);
		resp1.setWarningsCount(5);
		resp1.setTest(details);
		
		TestExecutionResponse resp2 = new TestExecutionResponse();
		resp2.setCompanyId(1);
		resp2.setProjectId(1);
		resp2.setDate(123);
		resp2.setLocation("New England");
		resp2.setFailuresCount(1);
		resp2.setWarningsCount(5);
		resp2.setTest(details);
		
		Assert.assertTrue(resp1.equals(resp2));
		Assert.assertEquals(resp1.hashCode(), resp2.hashCode());
		
		resp1.setLocation("Niagara Falls");
		Assert.assertFalse(resp1.equals(resp2));
		
		resp1.setLocation("New England");
		resp1.setCompanyId(2);
		Assert.assertFalse(resp1.equals(resp2));
		
	}
	
	@Test
	public void verifyResponsesEquals(){
		TestDetails details = new TestDetails();
		details.setId("123");
		details.setName("The test");
		TestExecutionResponse resp1 = new TestExecutionResponse();
		resp1.setCompanyId(1);
		resp1.setProjectId(1);
		resp1.setDate(123);
		resp1.setLocation("New England");
		resp1.setFailuresCount(1);
		resp1.setWarningsCount(5);
		resp1.setTest(details);
		
		TestExecutionResponses responses1 = new TestExecutionResponses();
		responses1.add(resp1);
		responses1.add(resp1);
		

		TestExecutionResponses responses2 = new TestExecutionResponses();
		responses2.add(resp1);
		responses2.add(resp1);
		
		Assert.assertTrue(responses1.equals(responses2));
		Assert.assertEquals(responses1.hashCode(), responses2.hashCode());
		
	}
	
	@Test
	public void verifyResponsesNotEquals(){
		TestDetails details = new TestDetails();
		details.setId("123");
		details.setName("The test");
		TestExecutionResponse resp1 = new TestExecutionResponse();
		resp1.setCompanyId(1);
		resp1.setProjectId(1);
		resp1.setDate(123);
		resp1.setLocation("New England");
		resp1.setFailuresCount(1);
		resp1.setWarningsCount(5);
		resp1.setTest(details);
		
		TestExecutionResponses responses1 = new TestExecutionResponses();
		responses1.add(resp1);
		responses1.add(resp1);
		

		TestExecutionResponses responses2 = new TestExecutionResponses();
		responses2.add(resp1);
		
		Assert.assertFalse(responses1.equals(responses2));
		Assert.assertFalse(responses1.hashCode()==responses2.hashCode());
		
	}
	
	@Test
	public void verifyTestDetailsEquals(){
		TestDetails details1 = new TestDetails();
		details1.setId("123");
		details1.setName("The test");
		
		TestDetails details2 = new TestDetails();
		details2.setId("123");
		details2.setName("The test");
		
		Assert.assertTrue(details1.equals(details2));
		
		Assert.assertFalse(details1.equals(new HashMap<>()));
	}
}
