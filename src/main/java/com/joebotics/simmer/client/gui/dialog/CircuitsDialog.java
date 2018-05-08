package com.joebotics.simmer.client.gui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import com.joebotics.simmer.client.CircuitLinkInfo;
import com.joebotics.simmer.client.Launcher;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.TreeNode;
import com.joebotics.simmer.client.gui.Bgpio;
import com.joebotics.simmer.client.gui.util.LoadFile;
import com.joebotics.simmer.client.util.FileUtils;

import gwt.material.design.addins.client.tree.MaterialTree;
import gwt.material.design.addins.client.tree.MaterialTreeItem;
import gwt.material.design.addins.client.window.MaterialWindow;
import gwt.material.design.client.constants.CenterOn;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialLink;

public class CircuitsDialog extends Composite {

    private static CircuitsDialogUiBinder uiBinder = GWT.create(CircuitsDialogUiBinder.class);

    interface CircuitsDialogUiBinder extends UiBinder<Widget, CircuitsDialog> {
    }

    @UiField
    MaterialWindow modal;

    @UiField
    MaterialTree circuitsTree;

    @UiField
    MaterialButton btnCollapse;

    @UiField
    MaterialButton btnExpand;

    @UiField
    MaterialLink btnImport;

    @UiField
    MaterialLink btnExport;

    private FileUtils fileUtils;

    private final String circuitIdPrefix = "circuit";

    public CircuitsDialog() {
        this.fileUtils = new FileUtils();

        initWidget(uiBinder.createAndBindUi(this));

        modal.setCenterOn(CenterOn.CENTER_ON_SMALL);
        circuitsTree.addSelectionHandler(event -> {

            String target = event.getSelectedItem().getTarget();
            if (target != null && target != "") {
                Launcher.mysim.getSimmerController().menuPerformed("circuits", "setup " + target);
                modal.close();
            }
        });
        if (LoadFile.isSupported()) {
            LoadFile lf = new LoadFile((Simmer.getInstance()));
            Simmer.getInstance().setLoadFileInput(lf);
            modal.add(lf);
        }
    }

    private void initTree() {
        TreeNode<CircuitLinkInfo> circuits = Simmer.getInstance().getCircuitsTree();
        if (circuits != null) {
            circuitsTree.clear();
            for (TreeNode<CircuitLinkInfo> node : circuits) {
                circuitsTree.add(parseNode(node));
            }
            circuitsTree.collapse();
        }
    }

    private MaterialTreeItem parseNode(TreeNode<CircuitLinkInfo> node) {
        MaterialTreeItem item;
        String nodeName;
        if (node.hasChildren()) {
            nodeName = node.getData().getName();

            item = new MaterialTreeItem(nodeName, IconType.FOLDER);
            item.setId(circuitIdPrefix + "-" + nodeName.toLowerCase().replace(' ', '-'));
            for (TreeNode<CircuitLinkInfo> child : node) {
                item.add(parseNode(child));
            }
        } else {
            nodeName = node.getData().getName();

            item = new MaterialTreeItem(nodeName, IconType.FILE_DOWNLOAD);
            item.setTarget(node.getData().getTarget());
            item.setId(circuitIdPrefix + "-" + nodeName.toLowerCase().replace(' ', '-'));
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

    @UiHandler("btnImport")
    public void btnImportHandler(ClickEvent event) {
        Simmer.getInstance().getLoadFileInput().click();
        modal.close();
    }

    @UiHandler("btnExport")
    public void btnExportHandler(ClickEvent event) {
        Simmer.getInstance().setBlocklyXml(Bgpio.getBlocks());
        String url = Simmer.getInstance().getFileOps().getCircuitUrl();
        this.fileUtils.download(url, Simmer.getInstance().getCircuitModel().getTitle());
        modal.close();
    }

    public void open() {
        initTree();
        modal.open();
    }
}
