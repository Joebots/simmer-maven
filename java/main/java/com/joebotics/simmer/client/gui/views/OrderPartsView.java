package com.joebotics.simmer.client.gui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;

import com.google.gwt.user.client.ui.Frame;
import com.joebotics.simmer.client.SimmerController;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class OrderPartsView extends Composite {
    interface OrderPartsViewUiBinder extends UiBinder<MaterialPanel, OrderPartsView> {
    }

    private static OrderPartsViewUiBinder ourUiBinder = GWT.create(OrderPartsViewUiBinder.class);
    private static final String FRAME_ID = "order_parts_frame";

    @UiField
    Frame frame;

    public OrderPartsView() {
        initWidget(ourUiBinder.createAndBindUi(this));
        frame.getElement().setId(FRAME_ID);
    }


    @Override
    protected void onLoad() {
        sendMessege("Hello World", FRAME_ID);
    }

    private native void sendMessege(String obj,String id) /*-{
        $wnd.document.getElementById(id).contentWindow.postMessage(obj, "*");
        console.log("Post message sent");
    }-*/;
}