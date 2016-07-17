package com.joebotics.simmer.client.gui.impl;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.joebotics.simmer.client.gui.util.MenuCommand;
import com.joebotics.simmer.client.util.MessageI18N;

/**
 * Created by joe on 7/16/16.
 */
public class ScopesMenu extends MenuBar {

    public ScopesMenu() {
        super(true);
        this.addItem(new MenuItem(MessageI18N.getLocale("Stack_All"), new MenuCommand("scopes", "stackAll")));
        this.addItem(new MenuItem(MessageI18N.getLocale("Unstack_All"), new MenuCommand("scopes", "unstackAll")));
    }

}
