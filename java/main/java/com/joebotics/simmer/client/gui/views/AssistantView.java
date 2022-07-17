package com.joebotics.simmer.client.gui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;

import gwt.material.design.client.ui.MaterialPanel;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class AssistantView extends Composite {

    interface AssistantViewUiBinder extends UiBinder<MaterialPanel, AssistantView> {

    }

    private static AssistantViewUiBinder uiBinder = GWT.create(AssistantViewUiBinder.class);

    public AssistantView() {
        initWidget(uiBinder.createAndBindUi(this));
    }
}