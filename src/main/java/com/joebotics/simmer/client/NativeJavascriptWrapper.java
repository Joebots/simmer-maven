package com.joebotics.simmer.client;

import java.util.Map;

/**
 * Created by joe on 7/20/16.
 */
public class NativeJavascriptWrapper {

    public static abstract class EventBusHandler{
        public abstract Object handleEvent(String ns, Map<String, Object> evt);
    }

    public static native void log(Object ... params) /*-{
        $wnd.console.log(params);
    }-*/;

}


