package com.joebotics.simmer.client.elcomp.sensors;

import com.joebotics.simmer.client.elcomp.*;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.util.StringTokenizer;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
import com.joebotics.simmer.client.gui.util.Point;

/**
 * KY-001 Temperature sensor Element
 */
public class KY001Elm extends ChipElm {
    private final ImageElement thermometer = ImageElement.as(new Image("imgs/components/thermometer.svg").getElement());

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
        g.getContext().drawImage(thermometer, center.getX() - thermometer.getWidth() / 2, center.getY() - thermometer.getHeight() / 2);

        super.draw(g);
    }
}
