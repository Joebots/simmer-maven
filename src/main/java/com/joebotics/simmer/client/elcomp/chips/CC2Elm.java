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
import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class CC2Elm extends ChipElm {
	double gain;

	public CC2Elm(int xx, int yy) {
		super(xx, yy);
		gain = 1;
	}

	public CC2Elm(int xx, int yy, int g) {
		super(xx, yy);
		gain = g;
	}

	public CC2Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		gain = new Double(st.nextToken()).doubleValue();
	}

	public void draw(Graphics g) {
		getPins()[2].setCurrent(getPins()[0].getCurrent() * gain);
		drawChip(g);
	}

	public String dump() {
		return super.dump() + " " + gain;
	}

	public String getChipName() {
		return "CC2";
	}

	public int getDumpType() {
		return 179;
	}

	public void getInfo(String arr[]) {
		arr[0] = (gain == 1) ? "CCII+" : "CCII-";
		arr[1] = "X,Y = " + getVoltageText(getVolts()[0]);
		arr[2] = "Z = " + getVoltageText(getVolts()[2]);
		arr[3] = "I = " + getCurrentText(getPins()[0].getCurrent());
	}

	public int getPostCount() {
		return 3;
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	public void setupPins() {
		setSizeX(2);
		setSizeY(3);
		setPins(new Pin[3]);
		getPins()[0] = new Pin(0, SIDE_W, "X");
		getPins()[0].setOutput(true);
		getPins()[1] = new Pin(2, SIDE_W, "Y");
		getPins()[2] = new Pin(1, SIDE_E, "Z");
	}

	// public boolean nonLinear() { return true; }
	public void stamp() {
		// X voltage = Y voltage
		sim.stampVoltageSource(0, getNodes()[0], getPins()[0].getVoltSource());
		sim.stampVCVS(0, getNodes()[1], 1, getPins()[0].getVoltSource());
		// Z current = gain * X current
		sim.stampCCCS(0, getNodes()[2], getPins()[0].getVoltSource(), gain);
	}
}


