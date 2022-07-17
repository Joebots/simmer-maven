package com.joebotics.simmer.client.gui.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.ui.MaterialTooltip;

/**
 * @author pavel.sitnikov@accelior.com
 */
public class CustomMaterialTooltip extends MaterialTooltip {

    private HandlerRegistration clickHandler;

    @Override
    public void setWidget(Widget widget) {
        if (widget == null) {
            return;
        }

        super.setWidget(widget);

        if (clickHandler != null) {
            clickHandler.removeHandler();
            clickHandler = null;
        }

        widget.addHandler(event -> {
            remove();
            reinitialize();
        }, ClickEvent.getType());

    }
}
