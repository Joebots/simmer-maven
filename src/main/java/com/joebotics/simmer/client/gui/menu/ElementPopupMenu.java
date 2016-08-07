package com.joebotics.simmer.client.gui.menu;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.joebotics.simmer.client.gui.util.MenuCommand;
import com.joebotics.simmer.client.util.MessageI18N;

/**
 * Created by joe on 8/5/16.
 */
public class ElementPopupMenu extends MenuBar {

    private MenuItem						elmEditMenuItem;
    private MenuItem						elmScopeMenuItem;

    public ElementPopupMenu(){
        super(true);
        MenuBar elmMenuBar = this;
        elmMenuBar.addItem(elmEditMenuItem = new MenuItem(MessageI18N.getMessage("Edit"), new MenuCommand("elm", "edit")));
        elmMenuBar.addItem(elmScopeMenuItem = new MenuItem(MessageI18N.getMessage("View_in_Scope"), new MenuCommand("elm", "viewInScope")));
        elmMenuBar.addItem(new MenuItem(MessageI18N.getMessage("Cut"), new MenuCommand("elm", "cut")));
        elmMenuBar.addItem(new MenuItem(MessageI18N.getMessage("Copy"), new MenuCommand("elm", "copy")));
        elmMenuBar.addItem(new MenuItem(MessageI18N.getMessage("Delete"), new MenuCommand("elm", "delete")));
    }

    public MenuItem getElmEditMenuItem() {
        return elmEditMenuItem;
    }

    public MenuItem getElmScopeMenuItem() {
        return elmScopeMenuItem;
    }
}
