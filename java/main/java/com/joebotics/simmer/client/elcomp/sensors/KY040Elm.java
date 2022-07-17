package com.joebotics.simmer.client.elcomp.sensors;

import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
import com.joebotics.simmer.client.elcomp.ChipElm;
import com.joebotics.simmer.client.elcomp.Pin;
import com.joebotics.simmer.client.elcomp.Side;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.StringTokenizer;

/**
 * KY-040 Rotary encoder module
 */
public class KY040Elm extends ChipElm {
    private final ImageElement rotary = ImageElement.as(new Image("imgs/components/rotary.svg").getElement());

    public KY040Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP5";
    }

    public KY040Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP5";
    }

    public void execute() {
        getPins()[0].setValue(false);
    }

    @Override
    public String getChipName() {
        return "KY-040";
    }

    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    public int getDumpType() {
        return 540;
    }

    public int getPostCount() {
        return 5;
    }

    public void setupPins() {
        setSizeX(2);
        setSizeY(5);
        setPins(new Pin[getPostCount()]);

        getPins()[0] = new Pin(0, Side.EAST, "CLK");
        getPins()[1] = new Pin(1, Side.EAST, "DT");
        getPins()[2] = new Pin(2, Side.EAST, "SW");
        getPins()[3] = new Pin(3, Side.EAST, "+");
        getPins()[4] = new Pin(4, Side.EAST, "-");
    }

    @Override
    public void draw(Graphics g) {
        Point center = getCenterPoint();
        g.getContext().drawImage(rotary, center.getX() - rotary.getWidth() / 2, center.getY() - rotary.getHeight() / 2);

        super.draw(g);
    }
}
