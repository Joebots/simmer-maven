package com.joebotics.simmer.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface InterpreterEventHandler extends EventHandler {
    void onInterpreterStarted(InterpreterStartedEvent event);

    void onInterpreterPaused(InterpreterPausedEvent event);

    void onInterpreterStopped(InterpreterStoppedEvent event);
}
