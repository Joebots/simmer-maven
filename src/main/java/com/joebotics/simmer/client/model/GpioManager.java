package com.joebotics.simmer.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gwt.i18n.client.Dictionary;

public class GpioManager {
    private static Dictionary gpioConfig = Dictionary.getDictionary("GpioHardwareConfig");

    private List<GpioPin> pins = new ArrayList<>();

    public GpioManager() {
        Set<String> keys = gpioConfig.keySet();
        for (String key : keys) {
            pins.add(new GpioPin(key, gpioConfig.get(key)));
        }
    }

    public List<GpioPin> getAvailablePins() {
        return pins.stream()
                // Pass only pins with unknown state
                .filter(pin -> pin.getState() == GpioPinState.UNKNOWN).collect(Collectors.toList());
    }

    public List<GpioPin> getPins() {
        return pins;
    }

    public void holdPin(GpioPin pin, GpioPinState state) {
        pin.setState(state);
    }

    public void releasePin(GpioPin pin) {
        pin.setState(GpioPinState.UNKNOWN);
    }

    public GpioPin getPinByName(String name) {
        return pins.stream()
                // Pass only pins with unknown state
                .filter(pin -> pin.getName().equals(name)).findAny().orElse(null);
    }
}
