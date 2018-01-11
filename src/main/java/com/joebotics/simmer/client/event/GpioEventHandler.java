package com.joebotics.simmer.client.event;

import com.google.gwt.event.shared.EventHandler;

import jsinterop.annotations.JsFunction;

public interface GpioEventHandler extends EventHandler {
    void onGpioEvent(GpioEvent event);
}
