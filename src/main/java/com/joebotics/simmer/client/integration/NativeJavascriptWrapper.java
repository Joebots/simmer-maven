package com.joebotics.simmer.client.integration;

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


