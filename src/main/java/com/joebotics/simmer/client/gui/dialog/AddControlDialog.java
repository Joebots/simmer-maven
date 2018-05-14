package com.joebotics.simmer.client.gui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;

import com.joebotics.simmer.client.gui.ControlsTypes;

import gwt.material.design.addins.client.window.MaterialWindow;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class AddControlDialog extends Composite {

    public interface AddControllerDialogListener {

        void onAddControl(ControlsTypes type);

    }

    interface AddControllerDialogUiBinder extends UiBinder<MaterialWindow, AddControlDialog> {

    }

    private static AddControllerDialogUiBinder ourUiBinder = GWT.create(AddControllerDialogUiBinder.class);

    @UiField
    MaterialWindow modal;

    @UiField
    TextBox textBoxControl;

    private AddControllerDialogListener listener;

    public AddControlDialog() {
        initWidget(ourUiBinder.createAndBindUi(this));

        textBoxControl.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
        textBoxControl.getElement().getStyle().setBorderWidth(2, Style.Unit.PX);
        textBoxControl.getElement().getStyle().setBorderColor("#000000");
    }

    @UiHandler("buttonControl")
    public void onButtonControlClick(ClickEvent event) {
        listener.onAddControl(ControlsTypes.BUTTON);
        modal.close();
    }

    @UiHandler("labelControl")
    public void onLabelControlClick(ClickEvent event) {
        listener.onAddControl(ControlsTypes.LABEL);
        modal.close();
    }

    @UiHandler("iconControl")
    public void onIconControlClick(ClickEvent event) {
        listener.onAddControl(ControlsTypes.IMAGE);
        modal.close();
    }

    @UiHandler("textBoxControl")
    public void onTextBoxControlClick(ClickEvent event) {
        listener.onAddControl(ControlsTypes.TEXT_INPUT);
        modal.close();
    }

    public void open(AddControllerDialogListener listener) {
        modal.open();
        this.listener = listener;
    }

}