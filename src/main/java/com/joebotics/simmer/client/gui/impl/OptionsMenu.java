package com.joebotics.simmer.client.gui.impl;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.gui.util.MenuCommand;
import com.joebotics.simmer.client.util.MessageI18N;

/**
 * Created by joe on 7/18/16.
 */
public class OptionsMenu extends MenuBar {

    private Simmer simmer;
/*
    public OptionsMenu(Simmer simmer){
        this.simmer = simmer;
    }

    private CheckboxMenuItem				powerCheckItem;

    public CheckboxMenuItem getPowerCheckItem() {
        return powerCheckItem;
    }

    private CheckboxMenuItem setPowerCheckItem(CheckboxMenuItem powerCheckItem) {
        this.powerCheckItem = powerCheckItem;
        return powerCheckItem;
    }

    private void setPowerBarEnable() {
        if (getPowerCheckItem().getState()) {
            powerLabel.setStyleName("disabled", false);
            powerBar.enable();
        } else {
            powerLabel.setStyleName("disabled", true);
            powerBar.disable();
        }
    }
    private CheckboxMenuItem setConventionCheckItem(CheckboxMenuItem conventionCheckItem) {
        this.conventionCheckItem = conventionCheckItem;
        return conventionCheckItem;
    }

    private CheckboxMenuItem setDotsCheckItem(CheckboxMenuItem dotsCheckItem) {
        this.dotsCheckItem = dotsCheckItem;
        return dotsCheckItem;
    }

    // public void setElmList(Vector<AbstractCircuitElement> elmList) {
    // this.elmList = elmList;
    // }

    private CheckboxMenuItem setEuroResistorCheckItem(CheckboxMenuItem euroResistorCheckItem) {
        this.euroResistorCheckItem = euroResistorCheckItem;
        return euroResistorCheckItem;
    }

    public CheckboxMenuItem getPrintableCheckItem() {
        return printableCheckItem;
    }

    private CheckboxMenuItem setPrintableCheckItem(CheckboxMenuItem printableCheckItem) {
        this.printableCheckItem = printableCheckItem;
        return printableCheckItem;
    }

    protected CheckboxMenuItem setShowValuesCheckItem(CheckboxMenuItem showValuesCheckItem) {
        this.showValuesCheckItem = showValuesCheckItem;
        return showValuesCheckItem;
    }

    protected CheckboxMenuItem setSmallGridCheckItem(CheckboxMenuItem smallGridCheckItem) {
        this.smallGridCheckItem = smallGridCheckItem;
        return smallGridCheckItem;
    }

    private void enableItems() {
        // if (powerCheckItem.getState()) {
        // powerBar.enable();
        // powerLabel.enable();
        // } else {
        // powerBar.disable();
        // powerLabel.disable();
        // }
        // enableUndoRedo();
    }


    private MenuBar buildOptionsMenu() {

        MenuBar optionsMenuBar = new MenuBar(true);
        optionsMenuBar.addItem(setDotsCheckItem(new CheckboxMenuItem(MessageI18N.getMessage("Show_Current"))));
        simmer.getDotsCheckItem().setState(true);
        optionsMenuBar.addItem(simmer.setVoltsCheckItem(new CheckboxMenuItem(MessageI18N.getMessage("Show_Voltage"), new Command() {
            public void execute() {
                if (simmer.getVoltsCheckItem().getState())
                    getPowerCheckItem().setState(false);
                setPowerBarEnable();
            }
        })));
        optionsMenuBar.addItem(setPowerCheckItem(new CheckboxMenuItem(MessageI18N.getMessage("Show_Power"), new Command() {
            public void execute() {
                if (getPowerCheckItem().getState())
                    simmer.getVoltsCheckItem().setState(false);
                setPowerBarEnable();
            }
        })));
        optionsMenuBar.addItem(setShowValuesCheckItem(new CheckboxMenuItem(MessageI18N.getMessage("Show_Values"))));
        // m.add(conductanceCheckItem = getCheckItem(MessageI18N.getMessage("Show_Conductance")));
        optionsMenuBar.addItem(setSmallGridCheckItem(new CheckboxMenuItem(MessageI18N.getMessage("Small_Grid"), new Command() {
            public void execute() {
                simmer.setGrid();
            }
        })));
        optionsMenuBar.addItem(setEuroResistorCheckItem(new CheckboxMenuItem(MessageI18N.getMessage("European_Resistors"))));
        optionsMenuBar.addItem(setPrintableCheckItem(new CheckboxMenuItem(MessageI18N.getMessage("White_Background"), new Command() {
            public void execute() {
                for (int i = 0; i < simmer.getScopeCount(); i++)
                    simmer.getScope(i).setRect(simmer.getScope(i).getRect());
            }
        })));
        optionsMenuBar.addItem(setConventionCheckItem(new CheckboxMenuItem(MessageI18N.getMessage("Conventional_Current_Motion"))));
        optionsMenuBar.addItem(new CheckboxAlignedMenuItem(MessageI18N.getMessage("Other_Options..."), new MenuCommand("options", "other")));

        return optionsMenuBar;
    }
    / ** end options menu **/
}
