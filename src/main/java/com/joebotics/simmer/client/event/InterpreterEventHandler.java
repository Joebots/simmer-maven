package com.joebotics.simmer.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface InterpreterEventHandler extends EventHandler {
    public void onInterpreterStarted(InterpreterStartedEvent event);

    public void onInterpreterPaused(InterpreterPausedEvent event);

    public void onInterpreterStopped(InterpreterStoppedEvent event);
}
