package com.joebotics.simmer.client.gui.dialog;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.window.MaterialWindow;
import gwt.material.design.client.ui.MaterialModal;

public class OrderPartsDialog extends Composite {

    private static ExitDialogUiBinder uiBinder = GWT.create(ExitDialogUiBinder.class);

    interface ExitDialogUiBinder extends UiBinder<Widget, OrderPartsDialog> {
    }

    @UiField
    MaterialWindow modal;

    @UiField
    Frame frame;
    public OrderPartsDialog() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void open() {
        modal.setHeight("600px");
        modal.setWidth("650px");
        modal.open();
        frame.setUrl(frame.getUrl());

    }

}
