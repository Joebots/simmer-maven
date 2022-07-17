package com.joebotics.simmer.client.model;

public class GpioPin {
    private String name;
    private int pinNumber;
    private GpioPinState state;

    public GpioPin(String name, int pinNumber) {
        this.name = name;
        this.pinNumber = pinNumber;
        this.state = GpioPinState.UNKNOWN;
    }

    public String getName() {
        return name;
    }

    public int getPinNumber() {
        return pinNumber;
    }

    public GpioPinState getState() {
        return state;
    }

    public void setState(GpioPinState state) {
        this.state = state;
    }
}
