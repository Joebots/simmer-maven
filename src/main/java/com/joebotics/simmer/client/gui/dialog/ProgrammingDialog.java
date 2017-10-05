package com.joebotics.simmer.client.gui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.joebotics.simmer.client.gui.Bgpio;
import com.joebotics.simmer.client.gui.widget.TextArea;

import gwt.material.design.addins.client.window.MaterialWindow;
import gwt.material.design.client.ui.MaterialButton;

public class ProgrammingDialog extends Composite {
    
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
    HTMLPanel blocklyPanel;
    
    @UiField
    HTMLPanel toolboxPanel;
    
    @UiField
    TextArea codeTab;
    
    @UiField
    TextArea consoleTab;
    
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
    }
    
    @UiHandler("btnRun")
    public void btnRunHandler(ClickEvent event) {
        Bgpio.RunMode.selectMode(1);
        Bgpio.RunMode.run();
    }
    
    @UiHandler("modal")
    public void modalOpeningHandler(OpenEvent<Boolean> event) {
        if (workspacePlayground == null) {
            workspacePlayground = Bgpio.init(blocklyPanel.getElement(), params);
            Bgpio.resize();
            Bgpio.setBlocks(DOM.getElementById("startBlocks"));
        }
    }
    
    public void open() {
        modal.open();
    }
}
