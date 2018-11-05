package com.joebotics.simmer.client.elcomp.sensors;

import com.google.gwt.canvas.dom.client.Context2d;
import com.joebotics.simmer.client.elcomp.*;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.util.StringTokenizer;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
import com.joebotics.simmer.client.gui.util.Point;
import gwt.material.design.client.ui.MaterialSwitch;

/**
 * KY-001 Temperature sensor Element
 */
public class KY001Elm extends ChipElm {
    private final ImageElement thermometer = ImageElement.as(new Image("imgs/components/thermometer.svg").getElement());
    private boolean celsiusScale = true;

    public KY001Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP3";
    }

    public KY001Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP3";
    }

    public void execute() {
        getPins()[0].setValue(false);
    }

    @Override
    public String getChipName() {
        return "KY-001";
    }

    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    @Override
    public void setEditValue(int n, EditInfo ei) {
        if (n == 2) {
            celsiusScale = ei.switchElm.getValue();
        } else {
            super.setEditValue(n, ei);
        }
    }

    @Override
    public EditInfo getEditInfo(int n) {
        if (n == 2) {
            EditInfo ei = new EditInfo("Scale", 0, -1, -1);
            ei.switchElm = new MaterialSwitch("Celsius", "Fahrenheit");
            ei.switchElm.setValue(celsiusScale);
            return ei;
        }

        return super.getEditInfo(n);
    }

    public int getDumpType() {
        return 501;
    }

    public int getPostCount() {
        return 3;
    }

    public void setupPins() {
        setSizeX(2);
        setSizeY(3);
        setPins(new Pin[getPostCount()]);

        getPins()[0] = new Pin(0, Side.EAST, "S");
        getPins()[1] = new Pin(1, Side.EAST, "+");
        getPins()[2] = new Pin(2, Side.EAST, "-");
    }

    @Override
    public void draw(Graphics g) {
        Point center = getCenterPoint();
        Pin signalPin = getPins()[0];

        // TODO: Get the temp in a proper way
        double temperatureValue = signalPin.getCurrent();
        Context2d context = g.getContext();
        context.drawImage(thermometer, center.getX() - thermometer.getWidth() / 2, center.getY() - thermometer.getHeight() / 2);

        drawPinValue(g, signalPin, String.valueOf(celsiusScale ? temperatureValue : fahrenheitCelsius(temperatureValue)));
        super.draw(g);
    }

    private double fahrenheitCelsius(double fahrenheit) {
        return Math.round(((fahrenheit - 32) * 5) / 9);
    }
}
