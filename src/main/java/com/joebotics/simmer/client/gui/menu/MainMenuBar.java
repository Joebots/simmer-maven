package com.joebotics.simmer.client.gui.menu;

import com.google.gwt.user.client.ui.MenuBar;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.util.MessageI18N;

/**
 * @deprecated Functionality is moved to the {@code ToolsPanel} class
 * Created by joe on 8/6/16.
 */
public class MainMenuBar extends MenuBar {

    private EditMenu editMenu;
    private DrawMenu drawMenu;
    private FileMenu fileMenu;
    private ScopeStackMenu scopeStackMenu;
    private OptionsMenu optionsMenuBar;

    public MainMenuBar(Simmer simmer){
        MenuBar menuBar = this;
        menuBar.addItem(MessageI18N.getMessage("File"), fileMenu = new FileMenu());
        menuBar.addItem(MessageI18N.getMessage("Edit"), editMenu = new EditMenu(simmer));
        menuBar.addItem(MessageI18N.getMessage("Draw"), drawMenu = new DrawMenu(simmer, true));
        menuBar.addItem(MessageI18N.getMessage("Scopes"), scopeStackMenu = new ScopeStackMenu());
        menuBar.addItem(MessageI18N.getMessage("Options"), optionsMenuBar = new OptionsMenu(simmer));
    }

    public EditMenu getEditMenu() {
        return editMenu;
    }

    public DrawMenu getDrawMenu() {
        return drawMenu;
    }

    public FileMenu getFileMenu() {
        return fileMenu;
    }

    public ScopeStackMenu getScopeStackMenu() {
        return scopeStackMenu;
    }

    public OptionsMenu getOptionsMenuBar() {
        return optionsMenuBar;
    }
}
