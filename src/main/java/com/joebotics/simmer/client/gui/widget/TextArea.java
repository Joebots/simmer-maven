package com.joebotics.simmer.client.gui.widget;

import com.google.gwt.dom.client.Style;

import jsinterop.annotations.JsType;

@JsType
public class TextArea extends com.google.gwt.user.client.ui.TextArea {

    public TextArea() {
        super();
        getElement().getStyle().setBorderStyle(Style.BorderStyle.NONE);
        getElement().getStyle().setProperty("resize", "none");
    }


    @Override
    public void setText(String text) {
        super.setText(text);
    }

    public void appendText(String text) {
        super.setText(getText() + text);
    }

    @Override
    public String getText() {
        return super.getText();
    }

    public void clear() {
        super.setText("");
    }
}
