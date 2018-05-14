package com.joebotics.simmer.client.gui.views;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

import com.github.vedenin.gwt.dnd.client.PickupDragController;
import com.github.vedenin.gwt.dnd.client.drop.GridConstrainedDropController;
import com.joebotics.simmer.client.SimmerController;
import com.joebotics.simmer.client.gui.ControlsTypes;
import com.joebotics.simmer.client.gui.dialog.AddControlDialog;
import com.joebotics.simmer.client.gui.dialog.EditControlDialog;
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
public class ControlsView extends Composite implements AddControlDialog.AddControllerDialogListener, EditControlDialog.EditControlDialogListener {

    interface ControlsViewUiBinder extends UiBinder<MaterialPanel, ControlsView> {

    }

    private static ControlsViewUiBinder ourUiBinder = GWT.create(ControlsViewUiBinder.class);

    public static class ControlModel {

        private String id;

        private String value;

        private ControlsTypes type;

        public ControlModel(String id, String value, ControlsTypes type) {
            this.id = id;
            this.value = value;
            this.type = type;
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

        public ControlsTypes getType() {
            return type;
        }

        public void setType(ControlsTypes type) {
            this.type = type;
        }
    }

    @UiField
    MaterialPanel controlsListContainer;

    @UiField
    AbsolutePanel controlsContainer;

    @UiField
    CellTable<ControlModel> table;

    @UiField
    EditControlDialog editControlDialog;

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
        dragController.setBehaviorDragStartSensitivity(5);
        dropController = new GridConstrainedDropController(controlsContainer, 30, 30);

        dragController.registerDropController(dropController);

        table.addColumn(new TextColumn<ControlModel>() {

            @Override
            public String getValue(ControlModel object) {
                return object.getId();
            }
        }, MessageI18N.constants.ControlName());

        table.addColumn(new TextColumn<ControlModel>() {

            @Override
            public String getValue(ControlModel object) {
                return object.getValue();
            }
        }, MessageI18N.constants.ControlValue());

        table.setColumnWidth(0, 50, Style.Unit.PCT);
        table.setColumnWidth(1, 50, Style.Unit.PCT);

        table.setRowData(controls);

        final SingleSelectionModel<ControlModel> selectionModel = new SingleSelectionModel<>();

        table.setSelectionModel(selectionModel);

        table.addDomHandler(event -> {
            editControlDialog.open(selectionModel.getSelectedObject());
        }, DoubleClickEvent.getType());

        Window.addResizeHandler(event -> updateLayout());
    }

    @UiFactory
    EditControlDialog createEditControlDialog() {
        return new EditControlDialog(this);
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

    @Override
    public void onUpdateControlModel(ControlModel model) {
        switch (model.getType()) {
            case IMAGE:
                ((MaterialIcon) controlsMap.get(model.getId())).setIconType(IconType.valueOf(model.getValue()));
                break;
            case TEXT_INPUT:
                ((TextBox) controlsMap.get(model.getId())).setText(model.getValue());
            default:
                controlsMap.get(model.getId()).getElement().setInnerText(model.getValue());
        }
        table.setRowData(controls);
    }

    private Widget createControlWidget(ControlsTypes type) {
        String id;
        String value;
        Widget result;
        switch (type) {
            case IMAGE:
                id = MessageI18N.constants.Image() + " " + ++imagesCount;
                value = IconType.ANDROID.name();
                MaterialIcon icon = new MaterialIcon(IconType.ANDROID);
                icon.setIconSize(IconSize.LARGE);
                result = icon;
                break;
            case LABEL:
                id = MessageI18N.constants.Label() + " " + ++labelsCount;
                value = id;
                result = new Label(id);
                result.setWidth("90px");
                result.getElement().getStyle().setLineHeight(30, Style.Unit.PX);
                break;
            case BUTTON:
                id = MessageI18N.constants.Button() + " " + ++buttonsCount;
                value = id;
                result = new Button(id);
                result.setHeight("30px");
                result.setWidth("90px");
                break;
            case TEXT_INPUT:
                id = MessageI18N.constants.TextInput() + " " + ++textInputCount;
                value = id;
                TextBox textBox = new TextBox();
                textBox.setText(id);
                textBox.setWidth("116px");
                textBox.setHeight("26px");
                textBox.getElement().getStyle().setBorderStyle(Style.BorderStyle.SOLID);
                textBox.getElement().getStyle().setBorderWidth(2, Style.Unit.PX);
                textBox.getElement().getStyle().setBorderColor("#000000");
                textBox.getElement().getStyle().setBackgroundColor("#ffffff");
                result = textBox;
                break;
            default:
                throw new IllegalArgumentException("Unknown control type.");
        }

        final ControlModel model = new ControlModel(id, value, type);

        result.addDomHandler(event -> editControlDialog.open(model), DoubleClickEvent.getType());

        controlsMap.put(id, result);
        controls.add(model);
        table.setRowData(controls);
        return result;
    }
}