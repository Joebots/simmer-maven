package com.joebotics.simmer.client.gui;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

/**
 * Created by gologuzov on 30.11.17.
 */
@JsType(isNative = true, name = "bboard", namespace = JsPackage.GLOBAL)
public class BreadBoard {
    @JsProperty
    public static Config config;

    @JsMethod
    public static native void applyConfig();

    @JsMethod
    public static native void saveConfig();

    @JsMethod
    public static native void loadConfig();

    @JsMethod
    public static native void resetConfig();

    @JsMethod
    public static native void downloadConfig();

    @JsMethod
    public static native void uploadConfig();

    @JsMethod
    public static native void showAllBanks(boolean value);

    @JsType(isNative = true, name = "bboard.config", namespace = JsPackage.GLOBAL)
    public static class Config {
        @JsProperty
        public int width;

        @JsProperty
        public int height;

        @JsProperty
        public int rowCount;

        @JsProperty
        public int topMargin;

        @JsProperty
        public int leftMargin;

        @JsProperty
        public int thickness;

        @JsProperty
        public int pitch;

        @JsProperty
        public Bank[] banks;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Bank {
        @JsProperty
        public String type;

        @JsProperty
        public int rows;

        @JsProperty
        public int y;

        @JsProperty
        public int x;

        @JsProperty
        public int height;

        @JsProperty
        public String dir;
    }
}
