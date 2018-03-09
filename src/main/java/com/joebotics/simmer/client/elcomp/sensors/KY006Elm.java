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
 * KY-006 Passiv Piezo-Buzzer module
 */
public class KY006Elm extends ChipElm {
    private final ImageElement bellSprite = ImageElement.as(new Image("imgs/components/bellSprite.svg").getElement());
    private Rectangle iconRect = new Rectangle();

    public KY006Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP3";
    }

    public KY006Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP3";
    }

    public void execute() {
        getPins()[2].setValue(false);
    }

    @Override
    public String getChipName() {
        return "KY-006";
    }

    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    public int getDumpType() {
        return 502;
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
        getPins()[2].setOutput(getPins()[2].setState(true));

    }

    @Override
    public void draw(Graphics g) {
        drawState(g, getPins()[2].isOutput());
        super.draw(g);
    }

    protected void drawState(Graphics g, boolean active) {
        Context2d context = g.getContext();
        Point center = getCenterPoint();
        iconRect.setBounds(center.getX() - bellSprite.getWidth() / 2, center.getY() - bellSprite.getHeight() / 4,
                bellSprite.getWidth(), bellSprite.getHeight() / 2);

        if (active) {
            context.drawImage(bellSprite, 0, bellSprite.getHeight() / 2,
                    bellSprite.getWidth(), bellSprite.getHeight() / 2,
                    iconRect.x, iconRect.y,
                    bellSprite.getWidth(), bellSprite.getHeight() / 2);
        }
        else {
            context.drawImage(bellSprite, 0, 0,
                    bellSprite.getWidth(), bellSprite.getHeight() / 2,
                    iconRect.x, iconRect.y,
                    bellSprite.getWidth(), bellSprite.getHeight() / 2);
        }
    }
}
