package com.joebotics.simmer.client.integration;

import com.google.gwt.json.client.JSONObject;

/**
 * Created by joe on 7/20/16.
 */
public class NativeJavascriptWrapper {

    public static native void EventBus()/*-{
		$wnd.busInSimmer =  $wnd.parent.globalBus;
        $wnd.console.log("busInSimmer", $wnd.busInSimmer);
        var bus = $wnd.busInSimmer;
	}-*/;

    public static native void fire(String evt, JSONObject data) /*-{
        var model = eval("(" + data + ")");
        evt.model = model;
        $wnd.busInSimmer.fire(evt, {model: model});
    }-*/;

}


