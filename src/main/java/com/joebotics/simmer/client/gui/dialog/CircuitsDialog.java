package com.joebotics.simmer.client.gui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.joebotics.simmer.client.CircuitLinkInfo;
import com.joebotics.simmer.client.Launcher;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.TreeNode;

import gwt.material.design.addins.client.tree.MaterialTree;
import gwt.material.design.addins.client.tree.MaterialTreeItem;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialModal;

public class CircuitsDialog extends Composite {

    private static CircuitsDialogUiBinder uiBinder = GWT.create(CircuitsDialogUiBinder.class);

    interface CircuitsDialogUiBinder extends UiBinder<Widget, CircuitsDialog> {
    }

    @UiField
    MaterialModal modal;

    @UiField
    MaterialTree circuitsTree;

    @UiField
    MaterialIcon btnCollapse;

    @UiField
    MaterialIcon btnExpand;

    @UiField
    MaterialIcon btnClose;

    public CircuitsDialog() {
        initWidget(uiBinder.createAndBindUi(this));
        circuitsTree.addSelectionHandler(new SelectionHandler<MaterialTreeItem>() {
            @Override
            public void onSelection(SelectionEvent<MaterialTreeItem> event) {
                String target = event.getSelectedItem().getTarget();
                if (target != null && target != "") {
                    Launcher.mysim.getSimmerController().menuPerformed("circuits", "setup " + target);
                    modal.close();
                }
            }
        });
    }
    
    private void initTree() {
        TreeNode<CircuitLinkInfo> circuits = Simmer.getInstance().getCircuitsTree();
        if (circuits != null) {
            for(TreeNode<CircuitLinkInfo> node : circuits) {
                circuitsTree.add(parseNode(node));
            }
            circuitsTree.collapse();
        }
    }
    
    private MaterialTreeItem parseNode(TreeNode<CircuitLinkInfo> node) {
        MaterialTreeItem item;
        if (node.hasChildren()) {
            item = new MaterialTreeItem(node.getData().getName(), IconType.FOLDER);
            for (TreeNode<CircuitLinkInfo> child : node) {
                item.add(parseNode(child));
            }
        } else {
            item = new MaterialTreeItem(node.getData().getName(), IconType.FILE_DOWNLOAD);
            item.setTarget(node.getData().getTarget());
        }
        return item;
    }

    @UiHandler("btnCollapse")
    public void btnCollapseHandler(ClickEvent event) {
        circuitsTree.collapse();
    }

    @UiHandler("btnExpand")
    public void btnExpandHandler(ClickEvent event) {
        circuitsTree.expand();
    }

    @UiHandler("btnClose")
    public void btnCloseHandler(ClickEvent event) {
        modal.close();
    }

    public void open() {
        initTree();
        modal.open();
    }
}
