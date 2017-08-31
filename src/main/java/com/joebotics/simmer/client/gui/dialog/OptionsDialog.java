package com.joebotics.simmer.client.gui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialModal;

public class OptionsDialog extends Composite {

    private static OptionsDialogUiBinder uiBinder = GWT.create(OptionsDialogUiBinder.class);

    interface OptionsDialogUiBinder extends UiBinder<Widget, OptionsDialog> {
    }

    @UiField
    MaterialModal modal;

    @UiField
    MaterialIcon btnClose;

    public OptionsDialog() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("btnClose")
    public void btnCloseHandler(ClickEvent event) {
        modal.close();
    }

    public void open() {
        modal.open();
    }
}
