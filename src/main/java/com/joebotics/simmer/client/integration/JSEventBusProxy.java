package com.joebotics.simmer.client.integration;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by joe on 7/20/16.
 */
public class JSEventBusProxy {

    public interface Handler{
        void execute(JavaScriptObject evt);
    }

    private static final Map<String, Handler> handlers = new HashMap<>();

    public static native void init()/*-{
		$wnd.busInSimmer =  $wnd.parent.globalBus;
        $wnd.console.log("busInSimmer", $wnd.busInSimmer);
        var bus = $wnd.busInSimmer;
	}-*/;

    public static native void bind(String pattern, Handler handler) /*-{
        $wnd.busInSimmer.bind(pattern, $entry(handler));
    }-*/;

    public static native void fire(String evt, JSONObject data) /*-{
        var model = eval("(" + data + ")");
        evt.model = model;
        $wnd.busInSimmer.fire(evt, {model: model});
    }-*/;

}


