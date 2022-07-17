package com.joebotics.simmer.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;

import com.joebotics.simmer.client.SimmerController;

import gwt.material.design.client.ui.MaterialNavSection;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class ComponentToolbar extends Composite {

    interface ComponentToolbarUiBinder extends UiBinder<MaterialNavSection, ComponentToolbar> {
    }

    private static ComponentToolbarUiBinder uiBinder = GWT.create(ComponentToolbarUiBinder.class);

    private SimmerController controller;

    public ComponentToolbar(SimmerController controller) {
        initWidget(uiBinder.createAndBindUi(this));
        this.controller = controller;
    }

    @UiHandler("edit")
    public void onEditClick(ClickEvent event) {
        controller.menuPerformed("elm", "edit");
    }

    @UiHandler("view")
    public void onViewClick(ClickEvent event) {
        controller.menuPerformed("elm", "viewInScope");
    }

    @UiHandler("cut")
    public void onCutClick(ClickEvent event) {
        controller.menuPerformed("key", "cut");
    }

    @UiHandler("copy")
    public void onCopyClick(ClickEvent event) {
        controller.menuPerformed("key", "copy");
    }

    @UiHandler("rotateLeft")
    public void onRotateLeftClick(ClickEvent event) {
        controller.rotateElement(false);
    }

    @UiHandler("rotateRight")
    public void onRotateRightClick(ClickEvent event) {
        controller.rotateElement(true);
    }

    @UiHandler("delete")
    public void onDeleteClick(ClickEvent event) {
        controller.menuPerformed("key", "delete");
    }

}