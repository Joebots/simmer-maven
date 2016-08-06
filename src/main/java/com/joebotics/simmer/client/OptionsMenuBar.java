package com.joebotics.simmer.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.joebotics.simmer.client.gui.impl.CheckboxAlignedMenuItem;
import com.joebotics.simmer.client.gui.impl.CheckboxMenuItem;
import com.joebotics.simmer.client.gui.impl.Scope;
import com.joebotics.simmer.client.gui.util.MenuCommand;
import com.joebotics.simmer.client.util.MessageI18N;

/**
 * Created by joe on 8/5/16.
 */
public class OptionsMenuBar extends MenuBar{

    private Simmer simmer;

    public OptionsMenuBar(Simmer simmer){
        super(true);
        this.simmer = simmer;
        layout();
    }

    private void layout(){

        MenuBar optionsMenuBar = this;//new MenuBar(true);
        optionsMenuBar.addItem(simmer.setDotsCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("Show_Current"))));
        simmer.getDotsCheckItem().setState(true);
        optionsMenuBar.addItem(simmer.setVoltsCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("Show_Voltage"), new Command() {
            public void execute() {
                if (simmer.getVoltsCheckItem().getState())
                    simmer.getPowerCheckItem().setState(false);
                simmer.setPowerBarEnable();
            }
        })));
        optionsMenuBar.addItem(simmer.setPowerCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("Show_Power"), new Command() {
            public void execute() {
                if (simmer.getPowerCheckItem().getState())
                    simmer.getVoltsCheckItem().setState(false);
                simmer.setPowerBarEnable();
            }
        })));
        optionsMenuBar.addItem(simmer.setShowValuesCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("Show_Values"))));
        // m.add(conductanceCheckItem = getCheckItem(MessageI18N.getLocale("Show_Conductance")));
        optionsMenuBar.addItem(simmer.setSmallGridCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("Small_Grid"), new Command() {
            public void execute() {
                simmer.setGrid();
            }
        })));
        optionsMenuBar.addItem(simmer.setEuroResistorCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("European_Resistors"))));
        optionsMenuBar.addItem(simmer.setPrintableCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("White_Background"), new Command() {
            public void execute() {
                Scope[] scopes = simmer.getScopes();

                for( Scope s : scopes ){
                    s.setRect(s.getRect());
                }
            }
        })));
        optionsMenuBar.addItem(simmer.setConventionCheckItem(new CheckboxMenuItem(MessageI18N.getLocale("Conventional_Current_Motion"))));
        optionsMenuBar.addItem(new CheckboxAlignedMenuItem(MessageI18N.getLocale("Other_Options..."), new MenuCommand("options", "other")));

    }

}
