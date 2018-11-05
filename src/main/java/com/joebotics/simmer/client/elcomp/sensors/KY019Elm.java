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
 * KY-019 5V relay module
 */
public class KY019Elm extends ChipElm {
    private final ImageElement relaySprite = ImageElement.as(new Image("imgs/components/relaySprite.svg").getElement());
    private Rectangle iconRect = new Rectangle();

    public KY019Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP6";
    }

    public KY019Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP6";
    }

    @Override
    public String getChipName() {
        return "KY-019";
    }

    @Override
    public int getVoltageSourceCount() {
        return 0;
    }

    @Override
    public void execute() {
        Pin sPin = getPins()[2];
        Pin nc = getPins()[3];
        Pin no = getPins()[5];
        double commonVoltage = getPins()[4].getVoltage();
        boolean sHigh = sPin.getValue();

        if(sHigh) {
            nc.setVoltage(commonVoltage);
            no.setVoltage(0);
        }
        else {
            no.setVoltage(commonVoltage);
            nc.setVoltage(0);
        }
        no.setValue(sHigh);
        nc.setValue(!sHigh);
    }

    public int getDumpType() {
        return 519;
    }

    public int getPostCount() {
        return 6;
    }

    public void setupPins() {
        setSizeX(2);
        setSizeY(3);
        setPins(new Pin[getPostCount()]);

        getPins()[0] = new Pin(0, Side.EAST, "-");
        getPins()[1] = new Pin(1, Side.EAST, "+");
        getPins()[2] = new Pin(2, Side.EAST, "S");

        getPins()[3] = new Pin(0, Side.WEST, "NC");
        getPins()[4] = new Pin(1, Side.WEST, "C");
        getPins()[5] = new Pin(2, Side.WEST, "NO");

        getPins()[4].setValue(true);
    }

    @Override
    public void draw(Graphics g) {
        drawState(g, getPins()[2].getValue());
        super.draw(g);
    }

    protected void drawState(Graphics g, boolean active) {
        Context2d context = g.getContext();
        Point center = getCenterPoint();
        iconRect.setBounds(center.getX() - relaySprite.getWidth() / 2, center.getY() - relaySprite.getHeight() / 4,
                relaySprite.getWidth(), relaySprite.getHeight() / 2);

        if (active) {
            context.drawImage(relaySprite, 0, relaySprite.getHeight() / 2,
                    relaySprite.getWidth(), relaySprite.getHeight() / 2,
                    iconRect.x, iconRect.y,
                    relaySprite.getWidth(), relaySprite.getHeight() / 2);
        }
        else {
            context.drawImage(relaySprite, 0, 0,
                    relaySprite.getWidth(), relaySprite.getHeight() / 2,
                    iconRect.x, iconRect.y,
                    relaySprite.getWidth(), relaySprite.getHeight() / 2);
        }
    }
}
