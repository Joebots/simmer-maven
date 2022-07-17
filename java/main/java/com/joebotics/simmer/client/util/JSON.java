package com.joebotics.simmer.client.util;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class JSON {
    public static native String stringify(Object o);
    public static native <T> T parse(String json);
}
