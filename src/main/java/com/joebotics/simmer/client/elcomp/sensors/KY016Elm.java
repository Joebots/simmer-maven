package com.joebotics.simmer.client.elcomp.sensors;

import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;

/**
 * Created by gologuzov on 11.01.18.
 * KY-016 3-color LED module
 */
public class KY016Elm extends KY009Elm {
    public KY016Elm(int xx, int yy) {
        super(xx, yy);
    }

    public KY016Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
        super(xa, ya, xb, yb, f, st);
    }

    @Override
    public String getChipName() {
        return "KY-016";
    }

    public int getDumpType() {
        return 516;
    }

    @Override
    public void draw(Graphics g) {
        drawChip(g);
        Point centerPoint = getCenterPoint();
        int radius = 12;
        GraphicsUtil.drawThickCircle(g, centerPoint.getX(), centerPoint.getY(), radius);
        radius -= 4;
        g.setColor(getLEDColor());
        g.fillOval(centerPoint.getX() - radius, centerPoint.getY() - radius, radius * 2, radius * 2);
    }
}
