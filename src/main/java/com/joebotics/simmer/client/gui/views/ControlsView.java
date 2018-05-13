package com.joebotics.simmer.client.gui.views;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import com.github.vedenin.gwt.dnd.client.PickupDragController;
import com.github.vedenin.gwt.dnd.client.drop.GridConstrainedDropController;
import com.joebotics.simmer.client.SimmerController;
import com.joebotics.simmer.client.gui.ControlsTypes;
import com.joebotics.simmer.client.gui.dialog.AddControlDialog;
import com.joebotics.simmer.client.util.MessageI18N;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gwt.material.design.client.constants.IconSize;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class ControlsView extends Composite implements AddControlDialog.AddControllerDialogListener {

    interface ControlsViewUiBinder extends UiBinder<MaterialPanel, ControlsView> {

    }

    private static ControlsViewUiBinder ourUiBinder = GWT.create(ControlsViewUiBinder.class);

    private class ControlModel {

        private String id;

        private String value;

        public ControlModel(String id, String value) {
            this.id = id;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @UiField
    MaterialPanel controlsListContainer;

    @UiField
    AbsolutePanel controlsContainer;

    @UiField
    CellTable<ControlModel> table;

    private int buttonsCount;
    private int labelsCount;
    private int imagesCount;
    private int textInputCount;

    private GridConstrainedDropController dropController;
    private PickupDragController dragController;

    private SimmerController controller;

    private Map<String, Widget> controlsMap;
    private List<ControlModel> controls;

    public ControlsView(SimmerController controller) {
        initWidget(ourUiBinder.createAndBindUi(this));

        this.controller = controller;

        controlsMap = new HashMap<>();
        controls = new ArrayList<>();

        updateLayout();

        dragController = new PickupDragController(controlsContainer, true);
        dropController = new GridConstrainedDropController(controlsContainer, 30, 30);

        dragController.registerDropController(dropController);

        table.addColumn(new TextColumn<ControlModel>() {

            @Override
            public String getValue(ControlModel object) {
                return object.getId();
            }
        }, MessageI18N.constants.ControlName());

        Column<ControlModel, String> valueColumn = new Column<ControlModel, String>(new EditTextCell()) {

            @Override
            public String getValue(ControlModel object) {
                return object.getValue();
            }
        };
        valueColumn.setFieldUpdater((index, object, value) -> {
            object.setValue(value);
            controlsMap.get(object.getId()).getElement().setInnerText(value);
        });

        table.addColumn(valueColumn, MessageI18N.constants.ControlValue());

        table.setColumnWidth(valueColumn, 50, Style.Unit.PCT);

        table.setRowData(controls);
    }

    private void updateLayout() {
        int parentHeight = RootLayoutPanel.get().getOffsetHeight() - 64;
        int containerHeight = Math.round(parentHeight * .666f);
        controlsContainer.setHeight(containerHeight + "px");

        int consoleContainerHeight = Math.round(parentHeight * .333f - 45);
        controlsListContainer.setHeight(consoleContainerHeight + "px");
    }

    @Override
    public void onAddControl(ControlsTypes type) {
        Widget control = createControlWidget(type);
        dragController.makeDraggable(control);
        dropController.drop(control, 0, 0);
    }

    private Widget createControlWidget(ControlsTypes type) {
        String id;
        Widget result;
        switch (type) {
            case IMAGE:
                id = MessageI18N.constants.Image() + " " + ++imagesCount;
                MaterialIcon icon = new MaterialIcon(IconType.ANDROID);
                icon.setIconSize(IconSize.LARGE);
                result = icon;
                break;
            case LABEL:
                id = MessageI18N.constants.Label() + " " + ++labelsCount;
                result = new Label(id);
                result.setWidth("90px");
                result.getElement().getStyle().setLineHeight(30, Style.Unit.PX);
                break;
            case BUTTON:
                id = MessageI18N.constants.Button() + " " + ++buttonsCount;
                result = new Button(id);
                result.setHeight("30px");
                result.setWidth("90px");
                break;
            case TEXT_INPUT:
                id = MessageI18N.constants.TextInput() + " " + ++textInputCount;
                TextBox textBox = new TextBox();
                textBox.setText(id);
                textBox.setWidth("116px");
                textBox.setHeight("26px");
                textBox.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
                textBox.getElement().getStyle().setBorderWidth(2, Style.Unit.PX);
                textBox.getElement().getStyle().setBorderColor("#000000");
                result = textBox;
                break;
            default:
                throw new IllegalArgumentException("Unknown control type.");
        }
        controlsMap.put(id, result);
        controls.add(new ControlModel(id, id));
        table.setRowData(controls);
        return result;
    }
}