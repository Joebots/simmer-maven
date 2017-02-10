package com.joebotics.simmer.client.gui.menu;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.joebotics.simmer.client.gui.dialog.ExportAsLocalFileDialog;
import com.joebotics.simmer.client.gui.util.LoadFile;
import com.joebotics.simmer.client.gui.util.MenuCommand;
import com.joebotics.simmer.client.util.MessageI18N;

/**
 * Created by joe on 7/16/16.
 */
public class FileMenu extends MenuBar{

    public FileMenu(){
        super(true);

        MenuBar fileMenuBar = this;

        MenuItem importFromLocalFileItem = new MenuItem(MessageI18N.getMessage("Import_From_Local_File"), new MenuCommand("file", "importfromlocalfile"));
        importFromLocalFileItem.setEnabled(LoadFile.isSupported());
        fileMenuBar.addItem(importFromLocalFileItem);

        MenuItem importFromTextItem = new MenuItem(MessageI18N.getMessage("Import_From_Text"), new MenuCommand("file", "importfromtext"));
        fileMenuBar.addItem(importFromTextItem);

        MenuItem exportAsUrlItem = new MenuItem(MessageI18N.getMessage("Export_as_Link"), new MenuCommand("file", "exportasurl"));
        fileMenuBar.addItem(exportAsUrlItem);

        MenuItem exportAsLocalFileItem = new MenuItem(MessageI18N.getMessage("Export_as_Local_File"), new MenuCommand("file", "exportaslocalfile"));
        exportAsLocalFileItem.setEnabled(ExportAsLocalFileDialog.downloadIsSupported());
        fileMenuBar.addItem(exportAsLocalFileItem);

        MenuItem exportAsTextItem = new MenuItem(MessageI18N.getMessage("Export_as_Text"), new MenuCommand("file", "exportastext"));
        fileMenuBar.addItem(exportAsTextItem);
        fileMenuBar.addSeparator();

        MenuItem aboutItem = new MenuItem(MessageI18N.getMessage("About"), (Command) null);
        fileMenuBar.addItem(aboutItem);
        aboutItem.setScheduledCommand(new MenuCommand("file", "about"));
    }
}
