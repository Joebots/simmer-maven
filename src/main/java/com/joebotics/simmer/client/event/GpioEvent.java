package com.joebotics.simmer.client.event;

import com.google.gwt.event.shared.GwtEvent;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType(name = "GpioEvent")
public class GpioEvent extends GwtEvent<GpioEventHandler> {
    public static final Type<GpioEventHandler> TYPE = new Type<GpioEventHandler>();

    private String pinNumber;
    private boolean value;

    @JsConstructor
    public GpioEvent(int pinNumber, boolean value) {
        this.pinNumber = String.valueOf(pinNumber);
        this.value = value;
    }

    @JsIgnore
    @Override
    protected void dispatch(GpioEventHandler handler) {
        handler.onGpioEvent(this);
    }

    @JsIgnore
    @Override
    public GwtEvent.Type<GpioEventHandler> getAssociatedType() {
        return TYPE;
    }

    public String getPinNumber() {
        return pinNumber;
    }

    public boolean getValue() {
        return value;
    }
}
