package com.joebotics.simmer.client.elcomp.sensors;

import com.joebotics.simmer.client.elcomp.ChipElm;
import com.joebotics.simmer.client.elcomp.Pin;
import com.joebotics.simmer.client.elcomp.Side;
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;

/**
 * KY-011 2-Color (Red+Green) 5mm LED module
 */
public class KY011Elm extends ChipElm {

    public KY011Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP3";
    }

    public KY011Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP3";
    }

    @Override
    public void execute() {
    }

    @Override
    public String getChipName() {
        return "KY-011";
    }

    @Override
    public int getVoltageSourceCount() {
        return 0;
    }

    @Override
    public int getDumpType() {
        return 511;
    }

    @Override
    public int getPostCount() {
        return 3;
    }

    @Override
    public void setupPins() {
        setSizeX(2);
        setSizeY(3);
        setPins(new Pin[getPostCount()]);

        getPins()[0] = new Pin(0, Side.EAST, "R");
        getPins()[1] = new Pin(1, Side.EAST, "G");
        getPins()[2] = new Pin(2, Side.EAST, "-");
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
        double redVoltage = getPins()[0].getVoltage();
        double greenVoltage = getPins()[1].getVoltage();
        double summaryVoltage = redVoltage + greenVoltage;
        double weight = 255;
        return new Color((int) (redVoltage/summaryVoltage * weight), (int) (greenVoltage/summaryVoltage * weight),
                0);

    }
}
