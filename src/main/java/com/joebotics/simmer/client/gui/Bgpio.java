package com.joebotics.simmer.client.gui;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

import com.google.gwt.dom.client.Element;

@JsType(isNative = true, name = "Bgpio", namespace = JsPackage.GLOBAL)
public class Bgpio {
    @JsProperty
    public static Element workspace;

    @JsProperty
    public static Element codePanel;

    @JsProperty
    public static Element jsConsole;

    @JsMethod
    public static native Element init(Element container, Params params);

    @JsMethod
    public static native void setBlocks(Element blocks);

    @JsMethod
    public static native Element resize();

    @JsType(isNative = true, name = "Bgpio.runMode", namespace = JsPackage.GLOBAL)
    public static class RunMode {
        @JsMethod
        public static native void run();

        @JsMethod
        public static native void debugInit();

        @JsMethod
        public static native void debugStep();

        @JsMethod
        public static native void stop();
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Params {
        public String media;
        public Element toolbox;
        public Boolean sounds;
    }
}
