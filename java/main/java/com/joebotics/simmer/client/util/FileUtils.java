package com.joebotics.simmer.client.util;

import com.google.gwt.dom.client.Element;
import com.joebotics.simmer.client.gui.Bgpio;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Created by gologuzov on 13.02.18.
 */
@JsType(isNative = true, name = "FileUtils", namespace = JsPackage.GLOBAL)
public class FileUtils {
    /**
     * @param source URL of the server to download the file, as encoded by encodeURI().
     * @param target Filesystem url representing the file on the device. For backwards compatibility, this can also be
     *               the full path of the file on the device.
     */
    @JsMethod
    public native void download(String source, String target);

    /**
     * @param fileURL Filesystem URL representing the file on the device or a data URI. For backwards compatibility,
     *               this can also be the full path of the file on the device.
     * @param server URL of the server to receive the file, as encoded by encodeURI().
     */
    @JsMethod
    public native void upload(String fileURL, String server);
}
