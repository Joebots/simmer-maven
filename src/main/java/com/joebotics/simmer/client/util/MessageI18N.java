package com.joebotics.simmer.client.util;

import com.google.gwt.core.client.GWT;

public class MessageI18N {
	static I18N constants = (I18N) GWT.create(I18N.class);
	
	public static String getMessage(String methodName) {
		char[] symbolsToReplace = new char[] {'.', '(', ')', '+', '-', '/', '*', ' '};
		for (char symbolToReplace : symbolsToReplace) {
			methodName = methodName.replace(symbolToReplace, '_');			
		}
		return constants.getString(methodName);
	}
}
