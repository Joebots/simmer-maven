package com.joebotics.simmer.client.elcomp.sensors;

import com.joebotics.simmer.client.elcomp.ChipElm;
import com.joebotics.simmer.client.elcomp.Pin;
import com.joebotics.simmer.client.elcomp.Side;
import com.joebotics.simmer.client.util.StringTokenizer;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.dom.client.ImageElement;
import gwt.material.design.client.ui.MaterialSwitch;

/**
 * Created by gologuzov on 17.01.18.
 * KY-003 Hall magnetic sensor module
 */
public class KY003Elm extends ChipElm {
    private int sensorValue = 0;

    private final ImageElement magnet = ImageElement.as(new Image("imgs/components/magnet.svg").getElement());

    public KY003Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP3";
    }

    public KY003Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP3";
    }

    public void execute() {
        getPins()[0].setValue(false);
    }

    @Override
    public String getChipName() {
        return "KY-003";
    }

    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    public int getDumpType() {
        return 503;
    }

    public int getPostCount() {
        return 3;
    }

    public void setupPins() {
        setSizeX(2);
        setSizeY(3);
        setPins(new Pin[getPostCount()]);

        getPins()[0] = new Pin(0, Side.EAST, "S");
        getPins()[0].setOutput(getPins()[0].setState(true));
        getPins()[1] = new Pin(1, Side.EAST, "+");
        getPins()[2] = new Pin(2, Side.EAST, "-");
    }

    @Override
    public void setEditValue(int n, EditInfo ei) {
        if (n == 2) {
            sensorValue = ei.switchElm.getValue() ? 1 : 0;
        }
        else {
            super.setEditValue(n, ei);
        }
    }

    @Override
    public EditInfo getEditInfo(int n) {
        if (n == 2) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.switchElm = new MaterialSwitch("1", "0");
            ei.switchElm.setValue(sensorValue == 1);
            return ei;
        }

        return super.getEditInfo(n);
    }

    public void draw(Graphics g) {
        g.getContext().drawImage(magnet, rectPointsX[0] + magnet.getWidth() / 2, rectPointsY[0] + magnet.getHeight() / 2);

        super.draw(g);
    }
}
