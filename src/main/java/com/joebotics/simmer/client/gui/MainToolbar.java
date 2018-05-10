package com.joebotics.simmer.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;

import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.SimmerController;
import com.joebotics.simmer.client.gui.util.LoadFile;
import com.joebotics.simmer.client.util.FileUtils;

import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialNavSection;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class MainToolbar extends Composite {

    interface MainToolbarUiBinder extends UiBinder<MaterialNavSection, MainToolbar> {

    }

    private static MainToolbarUiBinder uiBinder = GWT.create(MainToolbarUiBinder.class);

    @UiField
    MaterialLink connect, importSchema;

    private SimmerController controller;

    private FileUtils fileUtils;

    public MainToolbar(SimmerController controller) {
        initWidget(uiBinder.createAndBindUi(this));

        this.controller = controller;
        this.fileUtils = new FileUtils();

        controller.checkBoardConnectionState(connect);

        importSchema.setVisible(LoadFile.isSupported());
    }

    @UiHandler("connect")
    public void onConnectClick(ClickEvent event) {
        controller.switchUseBoard(connect);
    }

    @UiHandler("save")
    public void onSaveClick(ClickEvent event) {
        Simmer.getInstance().setBlocklyXml(Bgpio.getBlocks());
        fileUtils.download(
            Simmer.getInstance().getFileOps().getCircuitUrl(),
            Simmer.getInstance().getCircuitModel().getTitle()
        );
    }

    @UiHandler("shot")
    public void onShotClick(ClickEvent event) {
        controller.getMainPanel().showNotImplementedModal();
    }

    @UiHandler("help")
    public void onHelpClick(ClickEvent event) {
        controller.getMainPanel().showNotImplementedModal();
    }

    @UiHandler("open")
    public void onOpenClick(ClickEvent event) {
        controller.showCircuitDialog();
    }

    @UiHandler("export")
    public void onExportClick(ClickEvent event) {
        controller.getMainPanel().showNotImplementedModal();
    }

    @UiHandler("share")
    public void onShareClick(ClickEvent event) {
        controller.getMainPanel().showNotImplementedModal();
    }

    @UiHandler("importSchema")
    public void onImportSchemaClick(ClickEvent event) {
        Simmer.getInstance().getLoadFileInput().click();
    }

}