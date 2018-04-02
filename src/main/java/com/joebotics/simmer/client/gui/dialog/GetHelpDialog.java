package com.joebotics.simmer.client.gui.dialog;


import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialModal;

public class GetHelpDialog extends Composite {

    private static DrawDialogUiBinder uiBinder = GWT.create(DrawDialogUiBinder.class);

    interface DrawDialogUiBinder extends UiBinder<Widget, GetHelpDialog> {
    }

    @UiField
    MaterialModal modal;


    public GetHelpDialog() {
        initWidget(uiBinder.createAndBindUi(this));

    }

    public void open() {

        modal.setHeight("500px");
        modal.setWidth("500px");
        modal.open();

    }
    @UiHandler("close")
    public void popupTestDialogButtonHandler(ClickEvent e) {
        modal.close();
    }
}
