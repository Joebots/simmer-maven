package com.joebotics.simmer.client.elcomp.sensors;

import com.joebotics.simmer.client.elcomp.*;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.util.StringTokenizer;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Rectangle;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.canvas.dom.client.Context2d;

/**
 * KY-008 Laser sensor module
 */
public class KY008Elm extends ChipElm {
    private final ImageElement laserSprite = ImageElement.as(new Image("imgs/components/laserSprite.svg").getElement());
    private Rectangle iconRect = new Rectangle();

    public KY008Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP3";
    }

    public KY008Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP3";
    }

    @Override
    public String getChipName() {
        return "KY-008";
    }

    @Override
    public int getVoltageSourceCount() {
        return 0;
    }

    public int getDumpType() {
        return 508;
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
        iconRect.setBounds(center.getX() - laserSprite.getWidth() / 2, center.getY() - laserSprite.getHeight() / 4,
                laserSprite.getWidth(), laserSprite.getHeight() / 2);

        if (active) {
            context.drawImage(laserSprite, 0, laserSprite.getHeight() / 2,
                    laserSprite.getWidth(), laserSprite.getHeight() / 2,
                    iconRect.x, iconRect.y,
                    laserSprite.getWidth(), laserSprite.getHeight() / 2);
        }
        else {
            context.drawImage(laserSprite, 0, 0,
                    laserSprite.getWidth(), laserSprite.getHeight() / 2,
                    iconRect.x, iconRect.y,
                    laserSprite.getWidth(), laserSprite.getHeight() / 2);
        }
    }
}
