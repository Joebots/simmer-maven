package com.joebotics.simmer.client.util;


import java.util.Map;

import com.google.gwt.core.client.GWT;

public class MessageI18N {
	static I18N constants = (I18N) GWT.create(I18N.class);
	private static Map<String,String> messages=  constants.mapOfMessages();
	public static String getLocale(String str){
		return messages.get(str);
	}
}
