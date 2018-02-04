package com.joebotics.simmer.client.gui;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.joebotics.simmer.client.gui.dialog.EditDialog;
import com.joebotics.simmer.client.gui.util.Display;

import gwt.material.design.client.MaterialDesignBase;

public class MainPanel extends Composite {
    
    static {
        MaterialDesignBase.injectCss(SimmerUIClientBundle.INSTANCE.dialogsCss());
    }
    
    private static MainPanelUiBinder uiBinder = GWT.create(MainPanelUiBinder.class);
    
    interface MainPanelUiBinder extends UiBinder<Widget, MainPanel> {
    }
    
    @UiField
    DockLayoutPanel layoutPanel;
    
    @UiField
    Canvas canvas;
    
    @UiField
    DockLayoutPanel eastPanel;
    
    @UiField
    ToolsPanel toolsPanel;
    
    @UiField
    EditDialog editDialog;
    
    public MainPanel() {
        initWidget(uiBinder.createAndBindUi(this));
        // eastPanel.setWidgetSize(toolsPanel,
        // RootLayoutPanel.get().getOffsetHeight() - height);
        layoutPanel.setWidgetSize(eastPanel, Display.BREADBOARD_WIDTH);
    }
    
    @UiFactory
    Canvas createCanvas() {
        return Canvas.createIfSupported();
    }
    
    public MainPanel(String firstName) {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    public Canvas getCanvas() {
        return canvas;
    }
    
    public EditDialog getEditDialog() {
        return editDialog;
    }
}
