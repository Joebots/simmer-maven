package com.joebotics.simmer.client.elcomp.chips;

import com.joebotics.simmer.client.elcomp.ChipElm;

/**
 * Created by joe on 8/14/16.
 */
public class ScriptElm extends ChipElm {

//    private ScriptElm() {
//        super(-1, -1);
//    }

    public ScriptElm(int x, int y) {
        super(x, y);
    }

    @Override
    public int getVoltageSourceCount() {
        return 0;
    }

    @Override
    public void setupPins() {

    }
}
