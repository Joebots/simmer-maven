package com.joebotics.simmer.client;

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
	}-*/;

    public static native void fire(String evt, CircuitLibrary	elmList) /*-{
        $wnd.console.log(evt, $wnd.parent);
       //$wnd.busInSimmer.fire(evt, elmList);
    }-*/;

}


