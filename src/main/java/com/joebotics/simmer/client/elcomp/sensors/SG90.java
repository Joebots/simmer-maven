package com.joebotics.simmer.client.elcomp.sensors;

import com.joebotics.simmer.client.elcomp.ChipElm;
import com.joebotics.simmer.client.elcomp.Pin;
import com.joebotics.simmer.client.elcomp.Side;
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.StringTokenizer;

/**
 * Created by gologuzov on 30.01.18.
 * Arduino towerpro sg90 9g servo
 */
public class SG90 extends ChipElm {
    private int angle = 45;

    public SG90(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP3";
    }

    public SG90(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP3";
    }

    @Override
    public void execute() {
    }

    @Override
    public String getChipName() {
        return "SG90 Servo";
    }

    @Override
    public int getVoltageSourceCount() {
        return 0;
    }

    @Override
    public int getDumpType() {
        return 590;
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

        getPins()[0] = new Pin(0, Side.EAST, "S");
        getPins()[1] = new Pin(1, Side.EAST, "+");
        getPins()[2] = new Pin(2, Side.EAST, "-");
    }

    @Override
    public void draw(Graphics g) {
        drawChip(g);
        Point centerPoint = getCenterPoint();
        int radius = 5;
        int lineLength = 15;
        g.setColor(Color.white);
        g.fillOval(centerPoint.getX() - radius, centerPoint.getY() - radius, radius * 2, radius * 2);
        int lineX = (int)Math.round(centerPoint.getX() + lineLength * Math.sin(angle * Math.PI / 180));
        int lineY = (int)Math.round(centerPoint.getY() - lineLength * Math.cos(angle * Math.PI / 180));
        g.drawLine(centerPoint.getX(), centerPoint.getY(), lineX, lineY);
    }
}
