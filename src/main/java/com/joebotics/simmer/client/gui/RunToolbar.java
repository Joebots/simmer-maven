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
import com.joebotics.simmer.client.gui.util.LoadFile;
import com.joebotics.simmer.client.util.FileUtils;

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
    MaterialLink run, debug, stop, nextStep, connect, importSchema;

    private SimmerController controller;

    private FileUtils fileUtils;

    private boolean isDebug;

    RunToolbar(SimmerController controller) {
        initWidget(uiBinder.createAndBindUi(this));

        this.controller = controller;
        this.fileUtils = new FileUtils();

        EventBus eventBus = Simmer.getInstance().getEventBus();
        eventBus.addHandler(InterpreterStartedEvent.TYPE, this);
        eventBus.addHandler(InterpreterStoppedEvent.TYPE, this);
        eventBus.addHandler(InterpreterPausedEvent.TYPE, this);
        Bgpio.setEventBus(eventBus);

        controller.checkBoardConnectionState(connect);

        importSchema.setVisible(LoadFile.isSupported());
        stop.setVisible(false);
        nextStep.setVisible(false);
    }

    @UiHandler("connect")
    public void onConnectClick(ClickEvent event) {
        controller.switchUseBoard(connect);
    }

    @UiHandler("save")
    public void onSaveClick(ClickEvent event) {
        Simmer.getInstance().setBlocklyXml(Bgpio.getBlocks());
        fileUtils.download(
            Simmer.getInstance().getFileOps().getCircuitUrl(),
            Simmer.getInstance().getCircuitModel().getTitle()
        );
    }

    @UiHandler("shot")
    public void onShotClick(ClickEvent event) {
        controller.getMainPanel().showNotImplementedModal();
    }

    @UiHandler("help")
    public void onHelpClick(ClickEvent event) {
        controller.getMainPanel().showNotImplementedModal();
    }

    @UiHandler("open")
    public void onOpenClick(ClickEvent event) {
        controller.showCircuitDialog();
    }

    @UiHandler("export")
    public void onExportClick(ClickEvent event) {
        controller.getMainPanel().showNotImplementedModal();
    }

    @UiHandler("share")
    public void onShareClick(ClickEvent event) {
        controller.getMainPanel().showNotImplementedModal();
    }

    @UiHandler("run")
    public void onRunClick(ClickEvent event) {
        Bgpio.RunMode.run();
    }

    @UiHandler("stop")
    public void onStopClick(ClickEvent event) {
        Bgpio.RunMode.stop();
    }

    @UiHandler("debug")
    public void onDebugClick(ClickEvent event) {
        Bgpio.RunMode.debugInit();
        isDebug = true;
    }

    @UiHandler("nextStep")
    public void onNextStepClick(ClickEvent event) {
        Bgpio.RunMode.debugStep();
    }

    @UiHandler("importSchema")
    public void onImportSchemaClick(ClickEvent event) {
        Simmer.getInstance().getLoadFileInput().click();
    }

    @Override
    public void onInterpreterStarted(InterpreterStartedEvent event) {
        run.setVisible(false);
        debug.setVisible(false);
        stop.setVisible(true);
        nextStep.setVisible(isDebug);
    }

    @Override
    public void onInterpreterPaused(InterpreterPausedEvent event) {

    }

    @Override
    public void onInterpreterStopped(InterpreterStoppedEvent event) {
        run.setVisible(true);
        debug.setVisible(true);
        stop.setVisible(false);
        nextStep.setVisible(true);
        isDebug = false;
    }
}