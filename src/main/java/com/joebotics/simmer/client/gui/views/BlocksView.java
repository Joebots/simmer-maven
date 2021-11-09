package com.joebotics.simmer.client.gui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.gui.Bgpio;
import com.joebotics.simmer.client.gui.widget.TextArea;

import gwt.material.design.client.ui.MaterialPanel;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class BlocksView extends Composite {

    interface BlocksViewUiBinder extends UiBinder<MaterialPanel, BlocksView> {

    }

    private static BlocksViewUiBinder uiBinder = GWT.create(BlocksViewUiBinder.class);

    @UiField
    HTMLPanel toolboxPanel;

    @UiField
    HTMLPanel blocklyPanel;

    @UiField
    HTMLPanel console;

    @UiField
    MaterialPanel consoleContainer;

    private Bgpio.Params params;

    private Element workspacePlayground;

    public BlocksView() {
        initWidget(uiBinder.createAndBindUi(this));

        updateLayout();

        params = new Bgpio.Params();
        params.media = "lib/blockly/media/";
        params.toolbox = toolboxPanel.getElement().getFirstChildElement();
        params.sounds = false;

        Window.addResizeHandler(event -> updateLayout());
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        if (workspacePlayground == null) {
            workspacePlayground = Bgpio.initWorkspace(blocklyPanel.getElement(), params);
        }
        String xmlText = Simmer.getInstance().getBlocklyXml();
        if (xmlText != null && Bgpio.getBlocksCount() == 0) {
            Bgpio.setBlocks(xmlText);
        }
        Bgpio.resize();
        Bgpio.setConsoleArea(console);
    }

    private void updateLayout() {
        int parentHeight = RootLayoutPanel.get().getOffsetHeight() - 64;
        blocklyPanel.setHeight(Math.round(parentHeight * .666f) + "px");

        int consoleContainerHeight = Math.round(parentHeight * .333f - 45);
        consoleContainer.setHeight(consoleContainerHeight + "px");
        console.setHeight((consoleContainerHeight - 30) + "px");

        if (workspacePlayground != null) {
            Bgpio.resize();
        }
    }

}