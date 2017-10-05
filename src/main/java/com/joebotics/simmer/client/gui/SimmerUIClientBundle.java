package com.joebotics.simmer.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.TextResource;

public interface SimmerUIClientBundle extends ClientBundle {

    SimmerUIClientBundle INSTANCE = GWT.create(SimmerUIClientBundle.class);

    @Source("resources/css/dialogs.css")
    TextResource dialogsCss();
}
