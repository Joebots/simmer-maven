package com.joebotics.simmer.client.gui.util;

import com.google.gwt.i18n.client.Dictionary;

public class Display {
    private static Dictionary breadboardConfig = Dictionary.getDictionary("BreadboardConfig");

	public static final int INFOWIDTH = 120;
	public static final int MENUBARHEIGHT = 30;
	public static final int BREADBOARD_WIDTH = Integer.valueOf(breadboardConfig.get("width"));
	public static final int BREADBOARD_HEIGHT = Integer.valueOf(breadboardConfig.get("height"));
	public static final int POSTGRABSQ = 16;
}
