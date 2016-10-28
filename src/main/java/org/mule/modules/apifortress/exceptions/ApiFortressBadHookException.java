package org.mule.modules.apifortress.exceptions;

import java.io.IOException;

@SuppressWarnings("serial")
public class ApiFortressBadHookException extends IOException {

	
	public ApiFortressBadHookException(String hookUrl){
		super("The API Hook "+hookUrl+" is not a valid URL");
	}
}
