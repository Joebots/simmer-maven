package com.joebotics.simmer.client.gui;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.joebotics.simmer.client.SimmerController;
import gwt.material.design.client.ui.MaterialContainer;

public class CanvasContainer extends Composite {

    interface CanvasContainerUiBinder extends UiBinder<Widget, CanvasContainer> {

    }

    private static CanvasContainerUiBinder uiBinder = GWT.create(CanvasContainerUiBinder.class);

    @UiField
    Canvas canvas;

    @UiField
    MaterialContainer canvasContainer;

    private SimmerController controller;

    private CanvasContainer(SimmerController controller) {
        this.controller = controller;
        initWidget(uiBinder.createAndBindUi(this));
    }
}
