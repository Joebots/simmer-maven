package com.joebotics.simmer.client.integration;

import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.elcomp.WireElm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by joe on 7/25/16.
 */
public class CircuitStateChangeEvent extends HashMap<String, Object> {
    private List<CircuitComponent> components = new ArrayList<>();
    private List<Connection> connections = new ArrayList<>();
    private boolean functioning;

    public CircuitStateChangeEvent(){}

    public CircuitStateChangeEvent(List<AbstractCircuitElement> ces, boolean functioning){

        for( AbstractCircuitElement ace : ces ){

            if( ace instanceof WireElm ){
                connections.add(new Connection(ace));
            }
            else{
                components.add(new CircuitComponent(ace));
            }

        }
    }
}
