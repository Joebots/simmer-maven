package com.joebotics.simmer.client.elcomp.sensors;

import com.joebotics.simmer.client.elcomp.ChipElm;
import com.joebotics.simmer.client.elcomp.Pin;
import com.joebotics.simmer.client.elcomp.Side;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.util.StringTokenizer;

/**
 * Created by gologuzov on 11.01.18.
 * KY-013 Temperature sensor module
 */
public class KY013Elm extends ChipElm {
    public KY013Elm(int xx, int yy) {
        super(xx, yy);
        footprintName = "SIP3";
    }

    public KY013Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
        footprintName = "SIP3";
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
        return "KY-013";
    }

    @Override
    public int getVoltageSourceCount() {
        return 1;
    }

    public int getDumpType() {
        return 513;
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
