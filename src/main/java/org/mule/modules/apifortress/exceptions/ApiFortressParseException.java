/**
 * (c) 20013-2016 API Fortress, Inc. The software in this package is published under the terms of the Commercial Free Software license V.1, a copy of which has been included with this distribution in the LICENSE.md file.
 */
package org.mule.modules.apifortress.exceptions;

/**
 * Triggered when the conversion from / to JSON/POJO fails
 * @author Simone Pezzano - simone@apifortress.com
 */
public class ApiFortressParseException extends Exception {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5882521149154143409L;

	public ApiFortressParseException(String message){
		super(message);
	}
}
