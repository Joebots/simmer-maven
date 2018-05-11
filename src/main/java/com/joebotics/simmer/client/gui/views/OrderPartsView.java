package com.joebotics.simmer.client.gui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;

import gwt.material.design.client.ui.MaterialPanel;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class OrderPartsView extends Composite {
    interface OrderPartsViewUiBinder extends UiBinder<MaterialPanel, OrderPartsView> {
    }

    private static OrderPartsViewUiBinder ourUiBinder = GWT.create(OrderPartsViewUiBinder.class);

    public OrderPartsView() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}