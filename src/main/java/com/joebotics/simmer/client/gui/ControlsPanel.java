package com.joebotics.simmer.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;

import gwt.material.design.client.ui.MaterialRow;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class ControlsPanel extends Composite {

    interface ControlsPanelUiBinder extends UiBinder<MaterialRow, ControlsPanel> {
    }

    private static ControlsPanelUiBinder uiBinder = GWT.create(ControlsPanelUiBinder.class);

    public ControlsPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }
}