/*    
    Copyright (C) Paul Falstad and Iain Sharp
    
    This file is part of CircuitJS1.

    CircuitJS1 is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    CircuitJS1 is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CircuitJS1.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jobotics.simmer.client.elcomp.chips;

import com.jobotics.simmer.client.elcomp.ChipElm;
import com.jobotics.simmer.client.gui.util.Color;
import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.util.GraphicsUtil;
import com.jobotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class SevenSegElm extends ChipElm {
	Color darkred;

	public SevenSegElm(int xx, int yy) {
		super(xx, yy);
	}

	public SevenSegElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	public void draw(Graphics g) {
		drawChip(g);
		g.setColor(Color.red);
		int xl = getX1() + getCspc() * 5;
		int yl = getY1() + getCspc();
		setColor(g, 0);
		GraphicsUtil.drawThickLine(g, xl, yl, xl + getCspc(), yl);
		setColor(g, 1);
		GraphicsUtil.drawThickLine(g, xl + getCspc(), yl, xl + getCspc(), yl + getCspc());
		setColor(g, 2);
		GraphicsUtil.drawThickLine(g, xl + getCspc(), yl + getCspc(), xl + getCspc(), yl + getCspc2());
		setColor(g, 3);
		GraphicsUtil.drawThickLine(g, xl, yl + getCspc2(), xl + getCspc(), yl + getCspc2());
		setColor(g, 4);
		GraphicsUtil.drawThickLine(g, xl, yl + getCspc(), xl, yl + getCspc2());
		setColor(g, 5);
		GraphicsUtil.drawThickLine(g, xl, yl, xl, yl + getCspc());
		setColor(g, 6);
		GraphicsUtil.drawThickLine(g, xl, yl + getCspc(), xl + getCspc(), yl + getCspc());
	}

	public String getChipName() {
		return "7-segment driver/display";
	}

	public int getDumpType() {
		return 157;
	}

	public int getPostCount() {
		return 7;
	}

	public int getVoltageSourceCount() {
		return 0;
	}

	void setColor(Graphics g, int p) {
		g.setColor(getPins()[p].isValue() ? Color.red : sim.getPrintableCheckItem()
				.getState() ? Color.white : darkred);
	}

	public void setupPins() {
		darkred = new Color(30, 0, 0);
		setSizeX(4);
		setSizeY(4);
		setPins(new Pin[7]);
		getPins()[0] = new Pin(0, SIDE_W, "a");
		getPins()[1] = new Pin(1, SIDE_W, "b");
		getPins()[2] = new Pin(2, SIDE_W, "c");
		getPins()[3] = new Pin(3, SIDE_W, "d");
		getPins()[4] = new Pin(1, SIDE_S, "e");
		getPins()[5] = new Pin(2, SIDE_S, "f");
		getPins()[6] = new Pin(3, SIDE_S, "g");
	}
}
