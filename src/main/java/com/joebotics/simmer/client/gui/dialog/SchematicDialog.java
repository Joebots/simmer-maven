package com.joebotics.simmer.client.gui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.joebotics.simmer.client.Launcher;

import gwt.material.design.addins.client.tree.MaterialTree;
import gwt.material.design.addins.client.tree.MaterialTreeItem;
import gwt.material.design.client.ui.MaterialModal;
import gwt.material.design.client.ui.MaterialToast;

public class SchematicDialog extends Composite {

    private static DrawDialogUiBinder uiBinder = GWT.create(DrawDialogUiBinder.class);

    interface DrawDialogUiBinder extends UiBinder<Widget, SchematicDialog> {
    }

    @UiField
    MaterialModal modal;

    @UiField
    MaterialTree compTree;

    public SchematicDialog() {
        initWidget(uiBinder.createAndBindUi(this));

        compTree.addSelectionHandler(new SelectionHandler<MaterialTreeItem>() {
           @Override
           public void onSelection(SelectionEvent<MaterialTreeItem> event) {
               String target = event.getSelectedItem().getTarget();
               MaterialToast.fireToast("Selected : " + target);
               if (target != null && target != "") {
                   Launcher.mysim.getSimmerController().menuPerformed("main", target);
                   modal.close();
               }
           }
       });
       compTree.collapse();
    }

    public void open() {
        modal.open();
    }
}
