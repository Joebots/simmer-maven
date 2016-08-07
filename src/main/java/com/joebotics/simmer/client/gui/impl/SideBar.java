package com.joebotics.simmer.client.gui.impl;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.gui.util.Display;
import com.joebotics.simmer.client.util.MessageI18N;

/**
 * Created by joe on 7/18/16.
 */
public class SideBar extends VerticalPanel{

    private Scrollbar						powerBar;
    private Label                           powerLabel;
    private CheckboxMenuItem				printableCheckItem;
    private Button                          resetButton;
    private Scrollbar                       currentBar;
    private static Simmer                          simmer;
    private Checkbox                        stoppedCheck;
    private Scrollbar						speedBar;
    private Frame                           iFrame;

//    protected void setStoppedCheck(Checkbox stoppedCheck) {
//        this.stoppedCheck = stoppedCheck;
//    }

    public SideBar(final Simmer simmer) {
        this.simmer = simmer;

        this.add(resetButton = new Button(MessageI18N.getMessage("Reset")));

        resetButton.addClickHandler(new ClickHandler(){
            public void onClick(ClickEvent event){
                simmer.resetAction();
            }
        });
        // dumpMatrixButton = new Button(MessageI18N.getMessage("Dump_Matrix"));
        // main.add(dumpMatrixButton);// IES for debugging
        stoppedCheck = new Checkbox(MessageI18N.getMessage("Stopped"));
        this.add(stoppedCheck);

//        if (LoadFile.isSupported())
//            this.add(loadFileInput = new LoadFile(this));

        Label l;
        this.add(l = new Label(MessageI18N.getMessage("Simulation_Speed")));
        l.addStyleName(MessageI18N.getMessage("topSpace"));

        // was max of 140
        speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 0, 260);
        this.add(speedBar);

        this.add(l = new Label(MessageI18N.getMessage("Current_Speed")));
        l.addStyleName("topSpace")
        ;
        currentBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100);
        this.add(currentBar);
        this.add(powerLabel = new Label(MessageI18N.getMessage("Power_Brightness")));
        powerLabel.addStyleName("topSpace");
        this.add(powerBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100));

        simmer.setPowerBarEnable();

        iFrame = new Frame("iframe.html");
        this.add(iFrame);
        iFrame.setWidth(Display.VERTICALPANELWIDTH + "px");
        iFrame.setHeight("100 px");
        iFrame.getElement().setAttribute("scrolling","no");
        simmer.setIFrame(iFrame);
    }

    public Scrollbar getPowerBar() {
        return powerBar;
    }

    public Label getPowerLabel() {
        return powerLabel;
    }

    public CheckboxMenuItem getPrintableCheckItem() {
        return printableCheckItem;
    }

    public VerticalPanel getVerticalPanel() {
        return this;
    }

    public Button getResetButton() {
        return resetButton;
    }

    public Scrollbar getCurrentBar() {
        return currentBar;
    }

    public Checkbox getStoppedCheck() {
        return stoppedCheck;
    }

    public Scrollbar getSpeedBar() {
        return speedBar;
    }
}
