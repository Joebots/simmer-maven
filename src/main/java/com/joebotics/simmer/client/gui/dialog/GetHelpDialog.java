package com.joebotics.simmer.client.gui.dialog;


import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.addins.client.window.MaterialWindow;

public class GetHelpDialog extends Composite {

    private static DrawDialogUiBinder uiBinder = GWT.create(DrawDialogUiBinder.class);

    interface DrawDialogUiBinder extends UiBinder<Widget, GetHelpDialog> {
    }

    @UiField
    MaterialWindow modal;
    @UiField
    Frame frame;

    public GetHelpDialog() {
        initWidget(uiBinder.createAndBindUi(this));

    }

    public void open() {

        modal.setHeight("600px");
        modal.setWidth("650px");
        modal.open();
        frame.setUrl(frame.getUrl());

    }
}
