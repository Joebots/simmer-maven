package com.joebotics.simmer.client.elcomp.sensors;

import com.joebotics.simmer.client.elcomp.ChipElm;
import com.joebotics.simmer.client.elcomp.Pin;
import com.joebotics.simmer.client.elcomp.Side;
import com.joebotics.simmer.client.util.StringTokenizer;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.event.dom.client.ClickEvent;
import com.joebotics.simmer.client.gui.util.Rectangle;
import gwt.material.design.client.ui.MaterialSwitch;

/**
 * Created by gologuzov on 17.01.18.
 * KY-003 Hall magnetic sensor module
 */
public class KY003Elm extends ChipElm {
    private final ImageElement magnet = ImageElement.as(new Image("imgs/components/magnet.svg").getElement());
    private final ImageElement switchIcon = ImageElement.as(new Image("imgs/components/switchSprite.svg").getElement());
    private Rectangle switchRect = new Rectangle();

    public KY003Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP3";
    }

    public KY003Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP3";
    }

    public void execute() {
        getPins()[0].setValue(false);
    }

    @Override
    public String getChipName() {
        return "KY-003";
    }

    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    public int getDumpType() {
        return 503;
    }

    public int getPostCount() {
        return 3;
    }

    public void setupPins() {
        setSizeX(2);
        setSizeY(3);
        setPins(new Pin[getPostCount()]);

        getPins()[0] = new Pin(0, Side.EAST, "S");
        getPins()[0].setOutput(getPins()[0].setState(true));
        getPins()[1] = new Pin(1, Side.EAST, "+");
        getPins()[2] = new Pin(2, Side.EAST, "-");
    }

    @Override
    public void setEditValue(int n, EditInfo ei) {
        if (n == 2) {
            getPins()[0].setOutput(ei.switchElm.getValue());
        }
        else {
            super.setEditValue(n, ei);
        }
    }

    @Override
    public EditInfo getEditInfo(int n) {
        if (n == 2) {
            EditInfo ei = new EditInfo("", 0, -1, -1);
            ei.switchElm = new MaterialSwitch("1", "0");
            ei.switchElm.setValue(getPins()[0].isOutput());
            return ei;
        }

        return super.getEditInfo(n);
    }

    public void draw(Graphics g) {
        Point center = getCenterPoint();
        g.getContext().drawImage(magnet, center.getX() - magnet.getWidth() / 2, rectPointsY[0] + magnet.getHeight() / 2);

        drawSwitch(g, getPins()[0].isOutput());
        super.draw(g);
    }

    @Override
    public void click(Point point) {
        if(switchRect.contains(point.getX(), point.getY())) {
            Pin sPin = getPins()[0];
            sPin.setOutput(!sPin.isOutput());
        }
    }

    protected void drawSwitch(Graphics g, boolean on) {
        Context2d context = g.getContext();
        Point center = getCenterPoint();
        switchRect.setBounds(center.getX() - switchIcon.getWidth() / 2, center.getY() + switchIcon.getHeight() / 4,
                switchIcon.getWidth(), switchIcon.getHeight() / 2);

        if (on) {
            context.drawImage(switchIcon, 0, switchIcon.getHeight() / 2,
                    switchIcon.getWidth(), switchIcon.getHeight() / 2,
                    switchRect.x, switchRect.y,
                    switchIcon.getWidth(), switchIcon.getHeight() / 2);
        }
        else {
            context.drawImage(switchIcon, 0, 0,
                    switchIcon.getWidth(), switchIcon.getHeight() / 2,
                    switchRect.x, switchRect.y,
                    switchIcon.getWidth(), switchIcon.getHeight() / 2);
        }
    }
}
