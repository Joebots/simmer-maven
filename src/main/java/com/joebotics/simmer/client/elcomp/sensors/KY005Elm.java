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
 * KY-005 Infrared emission sensor module
 */
public class KY005Elm extends ChipElm {
    private final ImageElement irSprite = ImageElement.as(new Image("imgs/components/irSprite.svg").getElement());
    private Rectangle iconRect = new Rectangle();

    public KY005Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP3";
    }

    public KY005Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP3";
    }

    public void execute() {
        Pin signalPin = getPins()[2];
        signalPin.setOutput(signalPin.getVoltage() > 0);
    }

    @Override
    public String getChipName() {
        return "KY-005";
    }

    @Override
    public int getVoltageSourceCount() {
        return 0;
    }

    public int getDumpType() {
        return 505;
    }

    public int getPostCount() {
        return 3;
    }

    public void setupPins() {
        setSizeX(2);
        setSizeY(3);
        setPins(new Pin[getPostCount()]);

        getPins()[0] = new Pin(0, Side.EAST, "-");
        getPins()[1] = new Pin(1, Side.EAST, "+");
        getPins()[2] = new Pin(2, Side.EAST, "S");

    }

    @Override
    public void draw(Graphics g) {
        drawState(g, getPins()[2].isOutput());

        super.draw(g);
    }

    protected void drawState(Graphics g, boolean active) {
        Context2d context = g.getContext();
        Point center = getCenterPoint();
        iconRect.setBounds(center.getX() - irSprite.getWidth() / 2, center.getY() - irSprite.getHeight() / 4,
                irSprite.getWidth(), irSprite.getHeight() / 2);

        if (active) {
            context.drawImage(irSprite, 0, irSprite.getHeight() / 2,
                    irSprite.getWidth(), irSprite.getHeight() / 2,
                    iconRect.x, iconRect.y,
                    irSprite.getWidth(), irSprite.getHeight() / 2);
        }
        else {
            context.drawImage(irSprite, 0, 0,
                    irSprite.getWidth(), irSprite.getHeight() / 2,
                    iconRect.x, iconRect.y,
                    irSprite.getWidth(), irSprite.getHeight() / 2);
        }
    }
}
