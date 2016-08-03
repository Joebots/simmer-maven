package com.joebotics.simmer.client.integration;

import java.util.List;

/**
 * Created by joe on 7/23/16.
 */
public class CircuitLibrary extends java.util.HashMap<Double, Identifiable> {

    private List<Connection> connections;
    private List<CircuitComponent> components;

    public List<Connection> getConnectionsFor(CircuitComponent cc){
        return null;
    }

    public List<CircuitComponent> getComponentsFor(Connection c){
        return null;
    }

    public boolean isConnected(Identifiable i) {
        return false;
    }
}
