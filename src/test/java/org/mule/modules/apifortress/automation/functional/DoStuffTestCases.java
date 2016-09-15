/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.automation.functional;

import org.junit.Test;
import org.mockito.Mockito;
import org.mule.api.MuleMessage;
import org.mule.modules.apifortress.ApiFortress;
import org.mule.modules.apifortress.ApiFortressConnector;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

public class DoStuffTestCases extends AbstractTestCase<ApiFortressConnector>{
    
    public DoStuffTestCases(){
        super(ApiFortressConnector.class);
    }
    
    
    @Test
    public void verify() throws Exception{
      /*  MuleMessage message = Mockito.mock(MuleMessage.class);
        Mockito.when(message.getPayload()).thenReturn("a");
        Mockito.when(message.getPayloadAsString()).thenReturn("a");
        getConnector().doStuff(message);
        */    }

}
