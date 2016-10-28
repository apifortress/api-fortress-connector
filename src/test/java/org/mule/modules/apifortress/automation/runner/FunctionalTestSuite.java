/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.runner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.mule.modules.apifortress.ApiFortressConnector;
import org.mule.modules.apifortress.automation.functional.AutomatchPassthroughTestCases;
import org.mule.modules.apifortress.automation.functional.AutomatchSynchronousTestCases;
import org.mule.modules.apifortress.automation.functional.SingleTestPassthroughTestCases;
import org.mule.modules.apifortress.automation.functional.SingleTestSynchronousTestCases;
import org.mule.tools.devkit.ctf.mockup.ConnectorTestContext;

@RunWith(Suite.class)
@SuiteClasses({
SingleTestSynchronousTestCases.class,
SingleTestPassthroughTestCases.class,
AutomatchSynchronousTestCases.class,
AutomatchPassthroughTestCases.class
})

public class FunctionalTestSuite {
	
	@BeforeClass
	public static void initialiseSuite(){
		ConnectorTestContext.initialize(ApiFortressConnector.class);
	}
	
	@AfterClass
    public static void shutdownSuite() {
    	ConnectorTestContext.shutDown();
    }
	
}