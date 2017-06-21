package com.joebotics.simmer.client.integration;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.elcomp.CircuitNode;

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
    	if ($wnd.busInSimmer) {
        	$wnd.busInSimmer.bind(pattern, $entry(handler));
        }
    }-*/;

    private static native void fire(String evt, JSONObject data) /*-{
    	console.log("JSEventBusProxy.fire", evt, "data", data);
        if ($wnd.busInSimmer) {
	        var model = eval("(" + data + ")");
	        evt.model = model;
	        $wnd.busInSimmer.fire(evt, {model: model});
	    }
    }-*/;
    
    public static void fireEvent(SimmerEvents evt, JSONObject data) {
    	fire(evt.value, data);
    }
    
    public static void fireError(SimmerEvents evt, String message) {
    	JSONObject data = new JSONObject();
    	data.put("message", new JSONString(message));
    	fire(evt.value, data);
    }
    
    public static void fireError(SimmerEvents evt, String message, AbstractCircuitElement ce) {
    	JSONObject data = new JSONObject();
    	data.put("message", new JSONString(message));
    	if (ce != null) {
    		data.put("element", ce.toJSONObject());
    	}
    	fire(evt.value, data);
    }
    
    public static void fireError(SimmerEvents evt, String message, CircuitNode node) {
    	JSONObject data = new JSONObject();
    	data.put("message", new JSONString(message));
    	if (node != null) {
	    	data.put("node", node.toJSONObject());
    	}
    	fire(evt.value, data);
    }
}


