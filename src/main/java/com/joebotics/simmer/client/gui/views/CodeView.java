package com.joebotics.simmer.client.gui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import com.joebotics.simmer.client.gui.Bgpio;
import com.joebotics.simmer.client.gui.widget.TextArea;

import gwt.material.design.client.ui.MaterialPanel;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class CodeView extends Composite {

    interface CodeViewUiBinder extends UiBinder<MaterialPanel, CodeView> {
    }

    private static CodeViewUiBinder uiBinder = GWT.create(CodeViewUiBinder.class);

    @UiField
    TextArea console;

    @UiField
    TextArea code;

    @UiField
    MaterialPanel codeContainer, consoleContainer;

    public CodeView() {
        initWidget(uiBinder.createAndBindUi(this));

        updateLayout();

        Window.addResizeHandler(event -> updateLayout());
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        Bgpio.setCodeArea(code);
        Bgpio.setConsoleArea(console);
    }

    private void updateLayout() {
        int parentHeight = RootLayoutPanel.get().getOffsetHeight() - 64;
        int codeContainerHeight = Math.round(parentHeight * .666f);
        codeContainer.setHeight(codeContainerHeight + "px");
        code.setHeight(codeContainerHeight + "px");

        int consoleContainerHeight = Math.round(parentHeight * .333f - 45);
        consoleContainer.setHeight(consoleContainerHeight + "px");
        console.setHeight((consoleContainerHeight - 30) + "px");
    }
}