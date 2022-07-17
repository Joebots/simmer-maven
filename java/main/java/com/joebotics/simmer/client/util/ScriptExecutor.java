package com.joebotics.simmer.client.util;

import jsinterop.annotations.JsType;

import com.joebotics.simmer.client.elcomp.Pin;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;

@JsType(isNative=true, name="ScriptExecutor", namespace=JsPackage.GLOBAL)
public class ScriptExecutor {
	
	@JsMethod
	public native void nativeCalcFunction(String scriptlet, Pin[] pins);
}
