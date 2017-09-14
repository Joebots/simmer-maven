package com.joebotics.simmer.client.gui;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import com.google.gwt.dom.client.Element;

@JsType(isNative = true, name = "Blockly", namespace = JsPackage.GLOBAL)
public class Blockly {
    @JsMethod
    public static native Element inject(Element container, Params params);

    @JsMethod
    public static native Element svgResize(Element workspacePlayground);

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Params {
        public String media;
        public Element toolbox;
        public Boolean sounds;
    }
    
    @JsType(isNative = true, name = "Blockly.Xml", namespace = JsPackage.GLOBAL)
    public static class Xml {
        @JsMethod
        public static native void domToWorkspace(Element workspacePlayground, Element dom);
    }
}
