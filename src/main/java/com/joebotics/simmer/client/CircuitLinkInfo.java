package com.joebotics.simmer.client;

public class CircuitLinkInfo {
    private String name;
    private String target;

    public CircuitLinkInfo(String name) {
        this(name, null);
    }

    public CircuitLinkInfo(String name, String target) {
        this.name = name;
        this.target = target;
    }

    public String getName() {
        return name;
    }

    public String getTarget() {
        return target;
    }
}
