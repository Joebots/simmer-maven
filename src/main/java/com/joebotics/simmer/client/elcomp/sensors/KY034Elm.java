package com.joebotics.simmer.client.elcomp.sensors;

import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
import com.joebotics.simmer.client.elcomp.ChipElm;
import com.joebotics.simmer.client.elcomp.Pin;
import com.joebotics.simmer.client.elcomp.Side;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Rectangle;
import com.joebotics.simmer.client.util.StringTokenizer;

/**
 * KY-034 7 Colour LED flash-module
 */
public class KY034Elm extends ChipElm {
    private final ImageElement ledSprite = ImageElement.as(new Image("imgs/components/ledSprite.svg").getElement());
    private Rectangle iconRect = new Rectangle();

    public KY034Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP3";
    }

    public KY034Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP3";
    }

    @Override
    public String getChipName() {
        return "KY-034";
    }

    @Override
    public int getVoltageSourceCount() {
        return 0;
    }

    public int getDumpType() {
        return 534;
    }

    public int getPostCount() {
        return 3;
    }

    public void setupPins() {
        setSizeX(2);
        setSizeY(3);
        setPins(new Pin[getPostCount()]);

        getPins()[0] = new Pin(0, Side.EAST, "-");
        getPins()[1] = new Pin(1, Side.EAST, "NC"); // Pin not connected
        getPins()[2] = new Pin(2, Side.EAST, "S");

    }

    @Override
    public void draw(Graphics g) {
        drawState(g, getPostVoltage(2) != 0);
        super.draw(g);
    }

    protected void drawState(Graphics g, boolean active) {
        Context2d context = g.getContext();
        Point center = getCenterPoint();
        iconRect.setBounds(center.getX() - ledSprite.getWidth() / 2, center.getY() - ledSprite.getHeight() / 4,
                ledSprite.getWidth(), ledSprite.getHeight() / 2);

        if (active) {
            context.drawImage(ledSprite, 0, ledSprite.getHeight() / 2,
                    ledSprite.getWidth(), ledSprite.getHeight() / 2,
                    iconRect.x, iconRect.y,
                    ledSprite.getWidth(), ledSprite.getHeight() / 2);
        }
        else {
            context.drawImage(ledSprite, 0, 0,
                    ledSprite.getWidth(), ledSprite.getHeight() / 2,
                    iconRect.x, iconRect.y,
                    ledSprite.getWidth(), ledSprite.getHeight() / 2);
        }
    }
}
