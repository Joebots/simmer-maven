package com.joebotics.simmer.client.integration;

import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.util.Point;

/**
 * Created by joe on 7/23/16.
 */
public class Connection extends Identifiable{

    public double side1uuid, side2uuid;
    public int postIdx1, postIdx2;
    public Point side1point, side2point;

    public Connection(){}

    public Connection(AbstractCircuitElement ace){

    }

    public native double getSide1uuid()/*-{
        return this.side1uuid;
    }-*/;
}

