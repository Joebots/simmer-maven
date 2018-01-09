package com.joebotics.simmer.client.elcomp.sensors;

import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.util.Graphics;

/**
 * Created by gologuzov on 04.01.18.
 */
public abstract class SensorElm extends AbstractCircuitElement {
    protected SensorElm(int xx, int yy) {
        super(xx, yy);
    }

    protected SensorElm(int xa, int ya, int xb, int yb, int f) {
        super(xa, ya, xb, yb, f);
    }

    @Override
    public void draw(Graphics g) {

    }
}
