package com.joebotics.simmer.client.gui.dialog;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialModal;

public class OrderPartsDialog extends Composite {

    private static ExitDialogUiBinder uiBinder = GWT.create(ExitDialogUiBinder.class);

    interface ExitDialogUiBinder extends UiBinder<Widget, OrderPartsDialog> {
    }

    @UiField
    MaterialModal modal;

    public OrderPartsDialog() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void open() {

        modal.setHeight("600px");
        modal.setWidth("600px");
        modal.open();
    }
    @UiHandler("close_btn")
    public void popupTestDialogButtonHandler(ClickEvent e) {
        modal.close();
    }

}
