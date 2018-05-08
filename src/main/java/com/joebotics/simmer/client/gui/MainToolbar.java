package com.joebotics.simmer.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;

import com.joebotics.simmer.client.SimmerController;

import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialNavSection;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class MainToolbar extends Composite {

    interface MainToolbarUiBinder extends UiBinder<MaterialNavSection, MainToolbar> {

    }

    private static MainToolbarUiBinder uiBinder = GWT.create(MainToolbarUiBinder.class);

    @UiField
    MaterialLink connect;

    private SimmerController controller;

    public MainToolbar(SimmerController controller) {
        initWidget(uiBinder.createAndBindUi(this));

        this.controller = controller;

        checkConnectButtonState();
    }

    private void checkConnectButtonState() {
        if (!Bgpio.hasBoard()) {
            connect.setIconColor(Color.GREEN_DARKEN_4);
        } else if (controller.isUseBoard()) {
            connect.setIconColor(Color.BLUE);
        } else {
            connect.setIconColor(Color.WHITE);
        }
    }

    @UiHandler("connect")
    public void onConnectClick(ClickEvent event) {
        if (Bgpio.hasBoard()) {
            controller.switchUseBoard();
            checkConnectButtonState();
        }
    }

    @UiHandler("save")
    public void onSaveClick(ClickEvent event) {

    }

    @UiHandler("shot")
    public void onShotClick(ClickEvent event) {

    }

    @UiHandler("help")
    public void onHelpClick(ClickEvent event) {

    }

    @UiHandler("open")
    public void onOpenClick(ClickEvent event) {

    }

    @UiHandler("export")
    public void onExportClick(ClickEvent event) {

    }

    @UiHandler("share")
    public void onShareClick(ClickEvent event) {

    }

}