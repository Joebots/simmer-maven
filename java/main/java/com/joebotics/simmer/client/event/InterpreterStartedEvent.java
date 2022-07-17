package com.joebotics.simmer.client.event;

import com.google.gwt.event.shared.GwtEvent;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType(name = "InterpreterStartedEvent")
public class InterpreterStartedEvent extends GwtEvent<InterpreterEventHandler> {
    @JsIgnore
    public static final Type<InterpreterEventHandler> TYPE = new Type<InterpreterEventHandler>();

    private boolean debug;

    @JsConstructor
    public InterpreterStartedEvent(boolean debug) {
        this.debug = debug;
    }

    @JsIgnore
    @Override
    protected void dispatch(InterpreterEventHandler handler) {
        handler.onInterpreterStarted(this);
    }

    @JsIgnore
    @Override
    public GwtEvent.Type<InterpreterEventHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean isDebug() {
        return debug;
    }
}
