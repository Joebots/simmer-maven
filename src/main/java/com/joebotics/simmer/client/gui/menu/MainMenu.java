package com.joebotics.simmer.client.gui.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import gwt.material.design.client.ui.MaterialLink;

public class MainMenu extends Composite {

	private static MainMenuUiBinder uiBinder = GWT.create(MainMenuUiBinder.class);

	interface MainMenuUiBinder extends UiBinder<Widget, MainMenu> {
	}
	
	@UiField
	MaterialLink optionsLink;

	public MainMenu() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	
	@UiHandler("optionsLink")
    void handleOptionsLink(ClickEvent e) {
		
	}
}
