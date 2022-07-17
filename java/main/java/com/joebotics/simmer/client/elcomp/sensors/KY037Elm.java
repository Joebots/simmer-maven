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
 * KY-037 Microphone sensor module
 */
public class KY037Elm extends ChipElm {
    private final ImageElement microphone = ImageElement.as(new Image("imgs/components/microphone.svg").getElement());

    public KY037Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP4";
    }

    public KY037Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP4";
    }

    public void execute() {
        getPins()[0].setValue(false);
    }

    @Override
    public String getChipName() {
        return "KY-037";
    }

    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    public int getDumpType() {
        return 537;
    }

    public int getPostCount() {
        return 4;
    }

    public void setupPins() {
        setSizeX(2);
        setSizeY(4);
        setPins(new Pin[getPostCount()]);

        getPins()[0] = new Pin(0, Side.EAST, "DS"); // Digital Signal
        getPins()[1] = new Pin(1, Side.EAST, "+");
        getPins()[2] = new Pin(2, Side.EAST, "-");
        getPins()[3] = new Pin(3, Side.EAST, "AS"); // Analog Signal
    }

    @Override
    public void draw(Graphics g) {
        Point center = getCenterPoint();
        g.getContext().drawImage(microphone, center.getX() - microphone.getWidth() / 2, center.getY() - microphone.getHeight() / 2);

        super.draw(g);
    }
}
