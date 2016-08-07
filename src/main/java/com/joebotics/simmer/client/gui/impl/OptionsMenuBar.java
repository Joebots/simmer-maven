package com.joebotics.simmer.client.gui.impl;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.gui.impl.CheckboxAlignedMenuItem;
import com.joebotics.simmer.client.gui.impl.CheckboxMenuItem;
import com.joebotics.simmer.client.gui.impl.Scope;
import com.joebotics.simmer.client.gui.util.MenuCommand;
import com.joebotics.simmer.client.util.MessageI18N;

/**
 * Created by joe on 8/5/16.
 */
public class OptionsMenuBar extends MenuBar {

    private Command voltageHandler = new Command() {
        @Override
        public void execute() {
            if (simmer.getMainMenuBar().getOptionsMenuBar().getVoltsCheckItem().getState())
                simmer.getMainMenuBar().getOptionsMenuBar().getPowerCheckItem().setState(false);
            simmer.setPowerBarEnable();
        }
    };

    private Command powerHandler = new Command() {
        @Override
        public void execute() {
            if (simmer.getMainMenuBar().getOptionsMenuBar().getPowerCheckItem().getState())
                simmer.getMainMenuBar().getOptionsMenuBar().getVoltsCheckItem().setState(false);
            simmer.setPowerBarEnable();
        }
    };

    private Command smallGridHandler = new Command() {
        @Override
        public void execute() {
            simmer.setGrid();
        }
    };

    private Command backgroundHandler = new Command() {
        @Override
        public void execute() {
            simmer.setGrid();
        }
    };

    private Simmer simmer;
    private CheckboxMenuItem                conventionCheckItem = new CheckboxMenuItem(MessageI18N.getMessage("Conventional_Current_Motion"), new MenuCommand("options", "other"));
    private CheckboxMenuItem				euroResistorCheckItem = new CheckboxMenuItem(MessageI18N.getMessage("European_Resistors"));
    private CheckboxMenuItem				dotsCheckItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Current"));
    private CheckboxMenuItem				showValuesCheckItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Values"));
    private CheckboxMenuItem				smallGridCheckItem = new CheckboxMenuItem(MessageI18N.getMessage("Small_Grid"), smallGridHandler);
    private CheckboxMenuItem				voltsCheckItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Voltage"), voltageHandler);
    private CheckboxMenuItem				powerCheckItem = new CheckboxMenuItem(MessageI18N.getMessage("Show_Power"), powerHandler);
    private CheckboxMenuItem                backgroundCheckItem = new CheckboxMenuItem(MessageI18N.getMessage("White_Background"), backgroundHandler);
    private CheckboxAlignedMenuItem         otherOptions = new CheckboxAlignedMenuItem(MessageI18N.getMessage("Other_Options..."), new MenuCommand("options", "other"));

    public OptionsMenuBar(Simmer simmer) {
        super(true);
        this.simmer = simmer;
        layout();
    }

    private void layout() {

        MenuBar optionsMenuBar = this;
        optionsMenuBar.addItem(dotsCheckItem);
        optionsMenuBar.addItem(voltsCheckItem);
        optionsMenuBar.addItem(powerCheckItem);
        optionsMenuBar.addItem(showValuesCheckItem);
        // m.add(conductanceCheckItem = getCheckItem(MessageI18N.getMessage("Show_Conductance")));
        optionsMenuBar.addItem(smallGridCheckItem);
        optionsMenuBar.addItem(euroResistorCheckItem);
        optionsMenuBar.addItem(backgroundCheckItem);
        optionsMenuBar.addItem(conventionCheckItem);
        optionsMenuBar.addItem(otherOptions);

        dotsCheckItem.setState(true);

//        simmer.setShowValuesCheckItem(showValuesCheckItem);
//        simmer.setSmallGridCheckItem(smallGridCheckItem);
//        simmer.setDotsCheckItem(dotsCheckItem);
//        simmer.setVoltsCheckItem(voltsCheckItem);
//        simmer.setPowerCheckItem(powerCheckItem);
//        simmer.setPrintableCheckItem(backgroundCheckItem);
    }

    public CheckboxMenuItem getConventionCheckItem() {
        return conventionCheckItem;
    }

    public CheckboxMenuItem getEuroResistorCheckItem() {
        return euroResistorCheckItem;
    }

    public CheckboxMenuItem getDotsCheckItem() {
        return dotsCheckItem;
    }

    public CheckboxMenuItem getShowValuesCheckItem() {
        return showValuesCheckItem;
    }

    public CheckboxMenuItem getSmallGridCheckItem() {
        return smallGridCheckItem;
    }

    public CheckboxMenuItem getVoltsCheckItem() {
        return voltsCheckItem;
    }

    public CheckboxMenuItem getPowerCheckItem() {
        return powerCheckItem;
    }

    public CheckboxMenuItem getBackgroundCheckItem() {
        return backgroundCheckItem;
    }

    public CheckboxAlignedMenuItem getOtherOptions() {
        return otherOptions;
    }
}
