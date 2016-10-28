/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.exceptions;

import java.io.IOException;

@SuppressWarnings("serial")
public class ApiFortressBadHookException extends IOException {

	
	public ApiFortressBadHookException(String hookUrl){
		super("The API Hook "+hookUrl+" is not a valid URL");
	}
}
