/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.exceptions;

import java.io.IOException;

/**
 * Triggered when the communication with API Fortress fails
 * @author Simone Pezzano - simone@apifortress.com
 */
public class ApiFortressIOException extends IOException {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1439319044619618701L;

	public ApiFortressIOException(String message){
		super(message);
	}
}
