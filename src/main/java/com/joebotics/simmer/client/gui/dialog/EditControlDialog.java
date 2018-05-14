package com.joebotics.simmer.client.gui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;

import com.joebotics.simmer.client.gui.ControlsTypes;
import com.joebotics.simmer.client.gui.views.ControlsView.ControlModel;

import java.util.Arrays;

import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialListValueBox;
import gwt.material.design.client.ui.MaterialModal;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialTextBox;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class EditControlDialog extends Composite {

    public interface EditControlDialogListener {

        void onUpdateControlModel(ControlModel model);

    }

    interface EditControlDialogUiBinder extends UiBinder<MaterialModal, EditControlDialog> {

    }

    private static EditControlDialogUiBinder uiBinder = GWT.create(EditControlDialogUiBinder.class);

    @UiField
    MaterialModal modal;

    @UiField
    MaterialPanel editIconContainer, editTextContainer;

    @UiField
    MaterialTextBox editText;

    @UiField
    MaterialListValueBox<IconType> editIcon;

    @UiField
    MaterialIcon iconPreview;

    @UiField
    MaterialButton btnSave;

    private EditControlDialogListener listener;

    private ControlModel model;

    public EditControlDialog(EditControlDialogListener listener) {
        initWidget(uiBinder.createAndBindUi(this));

        this.listener = listener;

        Arrays.stream(IconType.values()).forEach(editIcon::addItem);

        editIcon.addValueChangeHandler(valueChangeEvent -> {
            iconPreview.setIconType(valueChangeEvent.getValue());
        });

        editIcon.addStyleName("edit-control-select");

    }

    @UiHandler("btnSave")
    public void onBtnSaveClick(ClickEvent event) {
        if (ControlsTypes.IMAGE.equals(model.getType())) {
            model.setValue(editIcon.getValue().name());
        } else {
            model.setValue(editText.getValue());
        }
        listener.onUpdateControlModel(model);
        modal.close();
    }

    @UiHandler("btnClose")
    public void onBtnCloseClick(ClickEvent event) {
        modal.close();
    }

    public void open(ControlModel model) {
        this.model = model;
        if (ControlsTypes.IMAGE.equals(model.getType())) {
            editTextContainer.setVisible(false);
            editIconContainer.setVisible(true);

            editIcon.setValue(IconType.valueOf(model.getValue()), true);
        } else {
            editTextContainer.setVisible(true);
            editIconContainer.setVisible(false);

            editText.setText(model.getValue());
        }
        modal.open();
    }

}