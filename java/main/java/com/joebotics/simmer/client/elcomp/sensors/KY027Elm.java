package com.joebotics.simmer.client.elcomp.sensors;

import com.joebotics.simmer.client.elcomp.*;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.util.StringTokenizer;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
import com.joebotics.simmer.client.gui.util.Point;

/**
 * KY-027 Magic light cup module
 */
public class KY027Elm extends ChipElm {
    private final ImageElement mercuryBulb = ImageElement.as(new Image("imgs/components/mercury-bulb.svg").getElement());

    public KY027Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP4";
    }

    public KY027Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP4";
    }

    public void execute() {
        getPins()[1].setValue(false);
    }

    @Override
    public String getChipName() {
        return "KY-027";
    }

    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    public int getDumpType() {
        return 527;
    }

    public int getPostCount() {
        return 4;
    }

    public void setupPins() {
        setSizeX(2);
        setSizeY(4);
        setPins(new Pin[getPostCount()])
        ;

        getPins()[0] = new Pin(0, Side.EAST, "L");
        getPins()[1] = new Pin(1, Side.EAST, "S");
        getPins()[2] = new Pin(2, Side.EAST, "+");
        getPins()[3] = new Pin(3, Side.EAST, "-");

    }

    @Override
    public void draw(Graphics g) {
        Point center = getCenterPoint();
        g.getContext().drawImage(mercuryBulb, center.getX() - mercuryBulb.getWidth() / 2, center.getY() - mercuryBulb.getHeight() / 2);

        super.draw(g);
    }
}
