package com.joebotics.simmer.client.event;

import com.google.gwt.event.shared.GwtEvent;

import jsinterop.annotations.JsConstructor;
import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsType;

@JsType(name = "GpioEvent")
public class GpioEvent extends GwtEvent<GpioEventHandler> {
    public static final Type<GpioEventHandler> TYPE = new Type<>();

    private int pinNumber;
    private int value;

    @JsConstructor
    public GpioEvent(int pinNumber, int value) {
        this.pinNumber = pinNumber;
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

    public int getPinNumber() {
        return pinNumber;
    }

    public int getValue() {
        return value;
    }
}
