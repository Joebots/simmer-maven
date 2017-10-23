package com.joebotics.simmer.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface GpioEventHandler extends EventHandler {
    public void onGpioEvent(GpioEvent event);
}
