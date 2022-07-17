package com.joebotics.simmer.client;

public class CircuitLinkInfo {
    private String name;
    private String fileName;

    public CircuitLinkInfo(String name) {
        this(name, null);
    }

    public CircuitLinkInfo(String name, String fileName) {
        this.name = name;
        this.fileName = fileName;
    }

    public String getName() {
        return name;
    }

    public String getFileName() {
        return fileName;
    }
}
