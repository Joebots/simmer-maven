package com.joebotics.simmer.client.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.Event.Type;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType
public class SimmerEventBus extends SimpleEventBus {
    
    @Override
    @JsMethod
    public void fireEvent(GwtEvent<?> event) {
        super.fireEvent(event);
    }
    
    @Override
    @JsMethod
    public <H> HandlerRegistration addHandler(Type<H> type, H handler) {
        return super.addHandler(type, handler);
    }
}
