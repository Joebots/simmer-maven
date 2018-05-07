package com.joebotics.simmer.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;

import com.joebotics.simmer.client.SimmerController;

import gwt.material.design.client.ui.MaterialNavSection;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class MainToolbar extends Composite {

    interface MainToolbarUiBinder extends UiBinder<MaterialNavSection, MainToolbar> {

    }

    private static MainToolbarUiBinder uiBinder = GWT.create(MainToolbarUiBinder.class);

    private SimmerController controller;

    public MainToolbar(SimmerController controller) {
        initWidget(uiBinder.createAndBindUi(this));

        this.controller = controller;
    }
}