package com.joebotics.simmer.client.elcomp.sensors;

import com.google.gwt.core.client.GWT;
import com.joebotics.simmer.client.elcomp.ChipElm;
import com.joebotics.simmer.client.elcomp.Pin;
import com.joebotics.simmer.client.elcomp.Side;
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.Diode;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;

/**
 * Created by gologuzov on 11.01.18.
 * KY-009 3-color full-color LED SMD modules
 */
public class KY009Elm extends ChipElm {

    public KY009Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP4";
    }

    public KY009Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP4";
    }

    @Override
    public void execute() {
    }

    @Override
    public String getChipName() {
        return "KY-009";
    }

    @Override
    public int getVoltageSourceCount() {
        return 0;
    }

    @Override
    public int getDumpType() {
        return 509;
    }

    @Override
    public int getPostCount() {
        return 4;
    }

    @Override
    public void setupPins() {
        setSizeX(2);
        setSizeY(4);
        setPins(new Pin[getPostCount()]);

        getPins()[0] = new Pin(0, Side.EAST, "B");
        getPins()[1] = new Pin(1, Side.EAST, "R");
        getPins()[2] = new Pin(2, Side.EAST, "G");
        getPins()[3] = new Pin(3, Side.EAST, "-");
    }

    @Override
    public void draw(Graphics g) {
        drawChip(g);
        Point centerPoint = getCenterPoint();
        int radius = 12;
        Point topLeft = new Point(centerPoint.getX() - radius, centerPoint.getY() - radius);
        Point bottomRight = new Point(centerPoint.getX() + radius, centerPoint.getY() + radius);
        GraphicsUtil.drawThickRect(g, topLeft, bottomRight);
        radius -= 4;
        g.setColor(getLEDColor());
        g.fillOval(centerPoint.getX() - radius, centerPoint.getY() - radius, radius * 2, radius * 2);
    }

    protected Color getLEDColor() {
        double redVoltage = getPins()[1].getVoltage();
        double greenVoltage = getPins()[2].getVoltage();
        double blueVoltage = getPins()[0].getVoltage();
        double summaryVoltage = redVoltage + greenVoltage + blueVoltage;
        double weight = 255;
        return new Color((int) (redVoltage/summaryVoltage * weight), (int) (greenVoltage/summaryVoltage * weight),
                (int) (blueVoltage/summaryVoltage * weight));

    }
}
