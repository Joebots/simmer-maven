package com.joebotics.simmer.client.event;

import com.google.gwt.core.client.JavaScriptObject;

public class EventExample implements EventHandler {

	@Override
	public void response(JavaScriptObject input ) {
		System.out.println("event received!!");
	}
	public native void handle() /*-{
	  var that = this;
        $wnd.handler = $entry(function(input) {
          that.@com.joebotics.simmer.client.event.EventExample::response(Lcom/google/gwt/core/client/JavaScriptObject;)(input);

        });
   
  }-*/;

}
