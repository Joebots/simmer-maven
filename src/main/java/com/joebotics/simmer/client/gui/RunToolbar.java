package com.joebotics.simmer.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;

import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.SimmerController;
import com.joebotics.simmer.client.event.InterpreterEventHandler;
import com.joebotics.simmer.client.event.InterpreterPausedEvent;
import com.joebotics.simmer.client.event.InterpreterStartedEvent;
import com.joebotics.simmer.client.event.InterpreterStoppedEvent;

import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialNavSection;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class RunToolbar extends Composite implements InterpreterEventHandler {

    interface RunToolbarUiBinder extends UiBinder<MaterialNavSection, RunToolbar> {

    }

    private static RunToolbarUiBinder uiBinder = GWT.create(RunToolbarUiBinder.class);

    @UiField
    MaterialLink run, debug, connect;

    private SimmerController controller;

    RunToolbar(SimmerController controller) {
        initWidget(uiBinder.createAndBindUi(this));

        this.controller = controller;

        EventBus eventBus = Simmer.getInstance().getEventBus();
        eventBus.addHandler(InterpreterStartedEvent.TYPE, this);
        eventBus.addHandler(InterpreterStoppedEvent.TYPE, this);
        eventBus.addHandler(InterpreterPausedEvent.TYPE, this);
        Bgpio.setEventBus(eventBus);

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

    @UiHandler("run")
    public void onRunClick(ClickEvent event) {
        Bgpio.RunMode.run();
    }

    @UiHandler("debug")
    public void onDebugClick(ClickEvent event) {
        Bgpio.RunMode.debugInit();
    }

    @Override
    public void onInterpreterStarted(InterpreterStartedEvent event) {
        run.setEnabled(false);
        debug.setEnabled(false);
    }

    @Override
    public void onInterpreterPaused(InterpreterPausedEvent event) {

    }

    @Override
    public void onInterpreterStopped(InterpreterStoppedEvent event) {
        run.setEnabled(true);
        debug.setEnabled(true);
    }
}