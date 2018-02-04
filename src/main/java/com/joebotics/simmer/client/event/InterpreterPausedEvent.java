package com.joebotics.simmer.client.event;

import com.google.gwt.event.shared.GwtEvent;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType(name = "InterpreterPausedEvent")
public class InterpreterPausedEvent extends GwtEvent<InterpreterEventHandler> {
    @JsIgnore
    public static final Type<InterpreterEventHandler> TYPE = new Type<InterpreterEventHandler>();

    @JsConstructor
    public InterpreterPausedEvent() {
    }

    @JsIgnore
    @Override
    protected void dispatch(InterpreterEventHandler handler) {
        handler.onInterpreterPaused(this);
    }

    @JsIgnore
    @Override
    public GwtEvent.Type<InterpreterEventHandler> getAssociatedType() {
        return TYPE;
    }
}
