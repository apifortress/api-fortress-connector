/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.responses;

import java.util.LinkedList;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Represents the result of an automatch execution
 * @author Simone Pezzano - simone@apifortress.com
 *
 */
@JsonAutoDetect
public class ApiFortressResponses extends LinkedList<ApiFortressResponse> {

    private static final long serialVersionUID = -4901528768696145288L;
    
    private boolean noRun;

    public boolean isNoRun() {
        return noRun;
    }

    public void setNoRun(boolean noRun) {
        this.noRun = noRun;
    }



    public ApiFortressResponses(){
        super();
    }
}
