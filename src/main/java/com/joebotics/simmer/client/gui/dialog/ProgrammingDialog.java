package com.joebotics.simmer.client.gui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.event.*;
import com.joebotics.simmer.client.gui.Bgpio;
import com.joebotics.simmer.client.gui.widget.TextArea;

import gwt.material.design.addins.client.window.MaterialWindow;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialSwitch;

public class ProgrammingDialog extends Composite implements InterpreterEventHandler {

    private static ProgrammingDialogUiBinder uiBinder = GWT.create(ProgrammingDialogUiBinder.class);

    interface ProgrammingDialogUiBinder extends UiBinder<Widget, ProgrammingDialog> {
    }

    @UiField
    MaterialWindow modal;

    @UiField
    MaterialButton btnRun;

    @UiField
    MaterialButton btnDebug;
    
    @UiField
    MaterialButton btnNextStep;

    @UiField
    MaterialButton btnStop;

    @UiField
    HTMLPanel blocklyPanel;

    @UiField
    HTMLPanel toolboxPanel;

    @UiField
    TextArea codeTab;

    @UiField
    TextArea consoleTab;

    @UiField
    MaterialSwitch useBoardSwitch;

    private Bgpio.Params params;
    private Element workspacePlayground;

    public ProgrammingDialog() {
        initWidget(uiBinder.createAndBindUi(this));
        blocklyPanel.setHeight("100%");

        params = new Bgpio.Params();
        params.media = "lib/blockly/media/";
        params.toolbox = toolboxPanel.getElement().getFirstChildElement();
        params.sounds = false;
        Bgpio.setCodeArea(codeTab);
        Bgpio.setConsoleArea(consoleTab);
        EventBus eventBus = Simmer.getInstance().getEventBus();
        eventBus.addHandler(InterpreterStartedEvent.TYPE, this);
        eventBus.addHandler(InterpreterStoppedEvent.TYPE, this);
        eventBus.addHandler(InterpreterPausedEvent.TYPE, this);
        Bgpio.setEventBus(eventBus);
    }

    @UiHandler("useBoardSwitch")
    void onValueChange(ValueChangeEvent<Boolean> e) {
        Bgpio.setUseBoard(e.getValue());
    }

    @UiHandler("btnRun")
    public void btnRunHandler(ClickEvent event) {
        Bgpio.RunMode.run();
    }
    
    @UiHandler("btnDebug")
    public void btnDebugHandler(ClickEvent event) {
        Bgpio.RunMode.debugInit();
    }
    
    @UiHandler("btnNextStep")
    public void btnNextStepHandler(ClickEvent event) {
        Bgpio.RunMode.debugStep();
    }

    @UiHandler("btnStop")
    public void btnStopHandler(ClickEvent event) {
        Bgpio.RunMode.stop();
    }
    
    @UiHandler("modal")
    public void modalOpeningHandler(OpenEvent<Boolean> event) {
        if (workspacePlayground == null) {
            workspacePlayground = Bgpio.init(blocklyPanel.getElement(), params);
            Bgpio.resize();
        }
        String xmlText = Simmer.getInstance().getBlocklyXml();
        if (xmlText != null) {
            Bgpio.setBlocks(xmlText);
        } else {
            Bgpio.clearBlocks();
        }
    }

    public void open() {
        modal.open();
    }

    @Override
    public void onInterpreterStarted(InterpreterStartedEvent event) {
        btnRun.setEnabled(false);
        btnDebug.setEnabled(false);
        btnNextStep.setEnabled(false);
        btnStop.setEnabled(true);
    }

    @Override
    public void onInterpreterPaused(InterpreterPausedEvent event) {
        btnNextStep.setEnabled(true);
    }

    @Override
    public void onInterpreterStopped(InterpreterStoppedEvent event) {
        btnRun.setEnabled(true);
        btnDebug.setEnabled(true);
        btnNextStep.setEnabled(false);
        btnStop.setEnabled(false);
    }
}
