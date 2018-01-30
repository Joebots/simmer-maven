package com.joebotics.simmer.client.event;

import jsinterop.annotations.JsType;

/**
 * Created by gologuzov on 30.01.18.
 */
@JsType(name = "ServoEvent")
public class ServoEvent extends GpioEvent {
    public ServoEvent(int pinNumber, int value) {
        super(pinNumber, value);
    }
}
