package com.joebotics.simmer.client.elcomp.sensors;

import com.joebotics.simmer.client.elcomp.ChipElm;
import com.joebotics.simmer.client.elcomp.Pin;
import com.joebotics.simmer.client.elcomp.Side;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.util.StringTokenizer;

/**
 * Created by gologuzov on 11.01.18.
 * KY-009 3-color full-color LED SMD modules
 */
public class KY009Elm extends ChipElm {
    public KY009Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP4";
    }

    public KY009Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP4";
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        drawChipName(g);
    }

    public void execute() {
        getPins()[0].setValue(false);
    }

    @Override
    public String getChipName() {
        return "KY-009";
    }

    @Override
    public int getVoltageSourceCount() {
        return 0;
    }

    public int getDumpType() {
        return 509;
    }

    public int getPostCount() {
        return 4;
    }

    public void setupPins() {
        setSizeX(2);
        setSizeY(4);
        setPins(new Pin[getPostCount()]);

        getPins()[0] = new Pin(0, Side.EAST, "R");
        getPins()[1] = new Pin(1, Side.EAST, "G");
        getPins()[2] = new Pin(2, Side.EAST, "B");
        getPins()[3] = new Pin(3, Side.EAST, "-");
    }

    protected void drawChipName(Graphics g) {
        String s = getChipName();
        if (s == null)
            return;
        g.setFont(unitsFont);
        int w = (int) g.getContext().measureText(s).getWidth();
        g.setColor(whiteColor);
        int ya = (int) g.getCurrentFontSize() / 2;
        int xc = rectPointsX[0] + (rectPointsX[1] - rectPointsX[0]) / 2, yc = rectPointsY[0];
        g.drawString(s, xc - w / 2, yc - ya - 2);
    }
}
