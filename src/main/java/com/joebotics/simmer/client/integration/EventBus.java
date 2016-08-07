package com.joebotics.simmer.client.integration;

import com.google.gwt.core.client.JavaScriptObject;

import java.util.Map;

/**
 * Created by joe on 7/20/16.
 */
public class EventBus {

    public static native void fire(String ns, Map<String, Object> evt) /*-{
        $wnd.globalBus.fire(ns, evt);
    }-*/;

//    public static native void evtBusBind(String ns, JavaScriptObject evt) /*-{
//        var entry = $entry(function(amt) {
//            that.@mypackage.Account::add(I)(amt);
//        });
//
//        $wnd.globalBus.bind(ns, entry);
//    }-*/;
}
