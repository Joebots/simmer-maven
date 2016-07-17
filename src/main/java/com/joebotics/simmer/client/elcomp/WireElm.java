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

package com.jobotics.simmer.client.elcomp;

import com.jobotics.simmer.client.gui.impl.Checkbox;
import com.jobotics.simmer.client.gui.impl.EditInfo;
import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.util.GraphicsUtil;
import com.jobotics.simmer.client.util.StringTokenizer;



//import java.awt.*;
//import java.util.StringTokenizer;

public class WireElm extends AbstractCircuitElement {
	static final int FLAG_SHOWCURRENT = 1;

	static final int FLAG_SHOWVOLTAGE = 2;

	public WireElm(int xx, int yy) {
		super(xx, yy);
	}
	public WireElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
	}

	public void draw(Graphics g) {
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, getPoint1(), getPoint2());
		doDots(g);
		setBbox(getPoint1(), getPoint2(), 3);
		if (mustShowCurrent()) {
			String s = getShortUnitText(Math.abs(getCurrent()), "A");
			drawValues(g, s, 4);
		} else if (mustShowVoltage()) {
			String s = getShortUnitText(getVolts()[0], "V");
			drawValues(g, s, 4);
		}
		drawPosts(g);
	}

	public int getDumpType() {
		return 'w';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Show Current", mustShowCurrent());
			return ei;
		}
		if (n == 1) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Show Voltage", mustShowVoltage());
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "wire";
		arr[1] = "I = " + getCurrentDText(getCurrent());
		arr[2] = "V = " + getVoltageText(getVolts()[0]);
	}

	public double getPower() {
		return 0;
	}

	public int getShortcut() {
		return 'w';
	}

	public double getVoltageDiff() {
		return getVolts()[0];
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	public boolean isWire() {
		return true;
	}

	boolean mustShowCurrent() {
		return (getFlags() & FLAG_SHOWCURRENT) != 0;
	}

	boolean mustShowVoltage() {
		return (getFlags() & FLAG_SHOWVOLTAGE) != 0;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			if (ei.checkbox.getState())
				setFlags(FLAG_SHOWCURRENT);
			else
				setFlags(getFlags() & ~FLAG_SHOWCURRENT);
		}
		if (n == 1) {
			if (ei.checkbox.getState())
				setFlags(FLAG_SHOWVOLTAGE);
			else
				setFlags(getFlags() & ~FLAG_SHOWVOLTAGE);
		}
	}

	public void stamp() {
		sim.stampVoltageSource(getNodes()[0], getNodes()[1], getVoltSource(), 0);
	}
}
