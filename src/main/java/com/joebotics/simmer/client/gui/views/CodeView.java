package com.joebotics.simmer.client.gui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;

import gwt.material.design.client.ui.MaterialPanel;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class CodeView extends Composite {
    interface CodeViewUiBinder extends UiBinder<MaterialPanel, CodeView> {
    }

    private static CodeViewUiBinder uiBinder = GWT.create(CodeViewUiBinder.class);

    public CodeView() {
        initWidget(uiBinder.createAndBindUi(this));
    }
}