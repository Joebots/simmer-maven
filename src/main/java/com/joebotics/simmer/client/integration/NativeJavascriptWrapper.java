package com.joebotics.simmer.client.integration;

import java.util.Map;
import java.util.Vector;

import com.joebotics.simmer.client.breadboard.CircuitLibrary;
import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;

/**
 * Created by joe on 7/20/16.
 */
public class NativeJavascriptWrapper {

    public static native void EventBus()/*-{
		$wnd.busInSimmer =  $wnd.parent.globalBus;
        $wnd.console.log("busInSimmer", $wnd.busInSimmer)
	}-*/;

    public static native void fire(String evt, Object data) /*-{
        $wnd.console.log(evt, data);
       //$wnd.busInSimmer.fire(evt, elmList);
    }-*/;

}


