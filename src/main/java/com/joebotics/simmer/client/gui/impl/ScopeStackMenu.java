package com.joebotics.simmer.client.gui.impl;

import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.joebotics.simmer.client.gui.util.MenuCommand;
import com.joebotics.simmer.client.util.MessageI18N;

/**
 * Created by joe on 7/16/16.
 */
public class ScopeStackMenu extends MenuBar {

    public ScopeStackMenu() {
        super(true);
        this.addItem(new MenuItem(MessageI18N.getMessage("Stack_All"), new MenuCommand("scopes", "stackAll")));
        this.addItem(new MenuItem(MessageI18N.getMessage("Unstack_All"), new MenuCommand("scopes", "unstackAll")));
    }

}
