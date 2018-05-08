package com.joebotics.simmer.client.gui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;

import gwt.material.design.client.ui.MaterialPanel;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class ControlsView extends Composite {
    interface ControlsViewUiBinder extends UiBinder<MaterialPanel, ControlsView> {
    }

    private static ControlsViewUiBinder ourUiBinder = GWT.create(ControlsViewUiBinder.class);

    public ControlsView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}