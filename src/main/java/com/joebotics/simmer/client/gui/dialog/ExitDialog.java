package com.joebotics.simmer.client.gui.dialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialModal;

public class ExitDialog extends Composite {
    
    private static ExitDialogUiBinder uiBinder = GWT.create(ExitDialogUiBinder.class);
    
    interface ExitDialogUiBinder extends UiBinder<Widget, ExitDialog> {
    }
    
    @UiField
    MaterialModal modal;
    
    @UiField
    MaterialButton btnExit;
    
    @UiField
    MaterialButton btnCancel;
    
    public ExitDialog() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    @UiHandler("btnExit")
    public void btnExitHandler(ClickEvent event) {
        closeBrowser();
    }
    
    @UiHandler("btnCancel")
    public void btnbtnCancelHandler(ClickEvent event) {
        modal.close();
    }
    
    public void open() {
        modal.open();
    }
    
    private native void closeBrowser()
    /*-{
        $wnd.close();
    }-*/;
}
