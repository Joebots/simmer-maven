package com.joebotics.simmer.client.gui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.util.OptionKey;
import com.joebotics.simmer.client.util.Options;

import gwt.material.design.client.ui.MaterialCheckBox;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialModal;
import gwt.material.design.client.ui.MaterialRange;

public class OptionsDialog extends Composite {

    private static OptionsDialogUiBinder uiBinder = GWT.create(OptionsDialogUiBinder.class);

    interface OptionsDialogUiBinder extends UiBinder<Widget, OptionsDialog> {
    }

    @UiField
    MaterialModal modal;

    @UiField
    MaterialIcon btnClose;

    @UiField
    MaterialCheckBox showCurrentCheckItem, showVoltageCheckItem, showPowerCheckItem, showValuesCheckItem,
            smallGridCheckItem, euroResistorCheckItem, backgroundCheckItem, conventionCheckItem;

    @UiField
    MaterialRange speedBar, currentBar;

    private Options model;

    public OptionsDialog() {
        initWidget(uiBinder.createAndBindUi(this));
        model = Simmer.getInstance().getOptions();
        showCurrentCheckItem.setValue(model.getBoolean(OptionKey.SHOW_CURRENT));
        showVoltageCheckItem.setValue(model.getBoolean(OptionKey.SHOW_VOLTAGE));
        showPowerCheckItem.setValue(model.getBoolean(OptionKey.SHOW_POWER));
        showValuesCheckItem.setValue(model.getBoolean(OptionKey.SHOW_VALUES));
        smallGridCheckItem.setValue(model.getBoolean(OptionKey.SMALL_GRID));
        euroResistorCheckItem.setValue(model.getBoolean(OptionKey.EURO_RESISTORS));
        backgroundCheckItem.setValue(model.getBoolean(OptionKey.WHITE_BACKGROUND));
        conventionCheckItem.setValue(model.getBoolean(OptionKey.CONVENTIONAL_CURRENT_MOTION));
        speedBar.setValue(model.getInteger(OptionKey.SIMULATION_SPEED));
        speedBar.setValue(model.getInteger(OptionKey.CURRENT_SPEED));
    }

    @UiHandler("btnClose")
    public void btnCloseHandler(ClickEvent event) {
        modal.close();
    }

    @UiHandler({ "showCurrentCheckItem", "showVoltageCheckItem", "showPowerCheckItem", "showValuesCheckItem",
            "euroResistorCheckItem", "conventionCheckItem" })
    public void checkItemHandler(ValueChangeEvent<Boolean> event) {
        MaterialCheckBox comp = (MaterialCheckBox) event.getSource();
        model.setValue(OptionKey.valueOf(comp.getName()), comp.getValue());
    }

    @UiHandler("speedBar")
    public void speedBarHandler(ChangeEvent event) {
        model.setValue(OptionKey.SIMULATION_SPEED, speedBar.getValue());
    }

    @UiHandler("currentBar")
    public void currentBarHandler(ChangeEvent event) {
        model.setValue(OptionKey.CURRENT_SPEED, currentBar.getValue());
    }

    @UiHandler({ "smallGridCheckItem", "backgroundCheckItem" })
    public void checkGridHandler(ValueChangeEvent<Boolean> event) {
        checkItemHandler(event);
        Simmer.getInstance().setGrid();
    }

    public void open() {
        modal.open();
    }
}
