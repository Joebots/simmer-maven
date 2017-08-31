package com.joebotics.simmer.client.gui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.gui.util.Display;
import com.joebotics.simmer.client.gui.util.LoadFile;
import com.joebotics.simmer.client.gui.widget.Checkbox;
import com.joebotics.simmer.client.util.MessageI18N;

/**
 * Created by joe on 8/7/16.
 */
public class SidePanel extends VerticalPanel{

    private Simmer simmer;
    private Scrollbar						powerBar;
    private Label powerLabel;
    private Button resetButton;
    private Scrollbar                       currentBar;
    private Checkbox stoppedCheck;
    private Frame iFrame;
    private Scrollbar						speedBar;

    public SidePanel(Simmer simmer){
        this.simmer = simmer;
    }

    protected void setStoppedCheck(Checkbox stoppedCheck) {
        this.stoppedCheck = stoppedCheck;
    }

    public void setiFrameHeight() {
        if (iFrame == null)
            return;
        int i;
        int cumheight = 0;
        for (i = 0; i < this.getWidgetIndex(iFrame); i++) {
            if (this.getWidget(i) != simmer.getLoadFileInput()) {
                cumheight = cumheight + this.getWidget(i).getOffsetHeight();
                if (this.getWidget(i).getStyleName().contains("topSpace"))
                    cumheight += 12;
            }
        }
        int ih = RootLayoutPanel.get().getOffsetHeight() - cumheight;
        if (ih < 0)
            ih = 0;
        iFrame.setHeight(ih + "px");
    }

    public void removeWidgetFromVerticalPanel(Widget w) {
        this.remove(w);
        if (iFrame != null)
            setiFrameHeight();
    }

    public void addWidgetToVerticalPanel(Widget w) {
        if (iFrame != null) {
            int i = this.getWidgetIndex(iFrame);
            this.insert(w, i);
            setiFrameHeight();
        } else
            this.add(w);
    }

    public void createSideBar() {
        this.add(resetButton = new Button(MessageI18N.getMessage("Reset")));
        resetButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                simmer.resetAction();
            }
        });
        // dumpMatrixButton = new Button(MessageI18N.getMessage("Dump_Matrix"));
        // main.add(dumpMatrixButton);// IES for debugging
        setStoppedCheck(new Checkbox(MessageI18N.getMessage("Stopped")));
        this.add(stoppedCheck);

        if (LoadFile.isSupported()){

            LoadFile lf = new LoadFile((simmer));
            simmer.setLoadFileInput(lf);
            this.add(lf);
        }

        Label l;
        this.add(l = new Label(MessageI18N.getMessage("Simulation_Speed")));
        l.addStyleName(MessageI18N.getMessage("topSpace"));

        // was max of 140
        this.add(speedBar = new Scrollbar(Scrollbar.HORIZONTAL, 3, 1, 0, 260));

        this.add(l = new Label(MessageI18N.getMessage("Current_Speed")));
        l.addStyleName("topSpace");
        currentBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100);
        this.add(currentBar);
        this.add(powerLabel = new Label(MessageI18N.getMessage("Power_Brightness")));
        powerLabel.addStyleName("topSpace");
        this.add(powerBar = new Scrollbar(Scrollbar.HORIZONTAL, 50, 1, 1, 100));
        setPowerBarEnable();
        this.add(iFrame = new Frame("iframe.html"));
        iFrame.setWidth(Display.VERTICALPANELWIDTH + "px");
        iFrame.setHeight("100 px");
        iFrame.getElement().setAttribute("scrolling","no");
    }

    public void setPowerBarEnable() {
        if (simmer.getMainMenuBar().getOptionsMenuBar().getPowerCheckItem().getState()) {
            powerLabel.setStyleName("disabled", false);
            powerBar.enable();
        } else {
            powerLabel.setStyleName("disabled", true);
            powerBar.disable();
        }
    }

    public Scrollbar getSpeedBar() {
        return speedBar;
    }

    public Scrollbar getCurrentBar() {
        return currentBar;
    }

    public Scrollbar getPowerBar() {
        return powerBar;
    }

    public Checkbox getStoppedCheck() {
        return stoppedCheck;
    }
}
