package com.joebotics.simmer.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.SimpleEventBus;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType
public class SimmerEventBus extends SimpleEventBus {
    
    @Override
    @JsMethod
    public void fireEvent(GwtEvent<?> event) {
        super.fireEvent(event);
    }
}
