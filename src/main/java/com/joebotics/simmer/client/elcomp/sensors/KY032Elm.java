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
 * KY-032 Obstacle avoidance sensor module
 */
public class KY032Elm extends ChipElm {
    private final ImageElement obstacle = ImageElement.as(new Image("imgs/components/obstacle.svg").getElement());

    public KY032Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP3";
    }

    public KY032Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP3";
    }

    public void execute() {
        getPins()[0].setValue(false);
    }

    @Override
    public String getChipName() {
        return "KY-032";
    }

    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    public int getDumpType() {
        return 532;
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
        g.getContext().drawImage(obstacle, center.getX() - obstacle.getWidth() / 2, center.getY() - obstacle.getHeight() / 2);

        super.draw(g);
    }
}
