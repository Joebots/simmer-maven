package com.joebotics.simmer.client.elcomp.sensors;

import com.joebotics.simmer.client.elcomp.*;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.util.StringTokenizer;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
import com.joebotics.simmer.client.gui.util.Point;

/**
 * KY-023 XY-axis joystick
 */
public class KY023Elm extends ChipElm {
    private final ImageElement joystick = ImageElement.as(new Image("imgs/components/joystick.svg").getElement());

    public KY023Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP5";
    }

    public KY023Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP5";
    }

    public void execute() {
        getPins()[0].setValue(false);
    }

    @Override
    public String getChipName() {
        return "KY-023";
    }

    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    public int getDumpType() {
        return 523;
    }

    public int getPostCount() {
        return 5;
    }

    public void setupPins() {
        setSizeX(2);
        setSizeY(5);
        setPins(new Pin[getPostCount()]);

        getPins()[0] = new Pin(0, Side.EAST, "B");
        getPins()[1] = new Pin(1, Side.EAST, "Y");
        getPins()[2] = new Pin(2, Side.EAST, "X");
        getPins()[3] = new Pin(3, Side.EAST, "+");
        getPins()[4] = new Pin(4, Side.EAST, "-");
    }

    @Override
    public void draw(Graphics g) {
        Point center = getCenterPoint();
        g.getContext().drawImage(joystick, center.getX() - joystick.getWidth() / 2, center.getY() - joystick.getHeight() / 2);

        super.draw(g);
    }
}
