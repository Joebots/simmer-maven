package com.joebotics.simmer.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class ComponentToolbar {
    interface ComponentToolbarUiBinder extends UiBinder<DivElement, ComponentToolbar> {
    }

    private static ComponentToolbarUiBinder ourUiBinder = GWT.create(ComponentToolbarUiBinder.class);

    public ComponentToolbar() {
        DivElement rootElement = ourUiBinder.createAndBindUi(this);
    }
}