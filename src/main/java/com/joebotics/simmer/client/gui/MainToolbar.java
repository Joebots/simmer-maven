package com.joebotics.simmer.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;

import com.joebotics.simmer.client.SimmerController;

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

        connect.setVisible(Bgpio.hasBoard());
    }

    @UiHandler("connect")
    public void onConnectClick(ClickEvent event) {

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