package com.joebotics.simmer.client.integration;

import java.io.Serializable;

/**
 * Created by joe on 7/23/16.
 */
public class Identifiable implements Serializable{

    protected double uuid = Math.random();

    public double getUuid(){
        return uuid;
    }
}
