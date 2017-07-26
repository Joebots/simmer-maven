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

package com.joebotics.simmer.client.elcomp.chips;

import com.joebotics.simmer.client.elcomp.ChipElm;
import com.joebotics.simmer.client.elcomp.Pin;
import com.joebotics.simmer.client.elcomp.Side;
import com.joebotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class DACElm extends ChipElm {
	public DACElm(int xx, int yy) {
		super(xx, yy);
	}

	public DACElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	public void doStep() {
		int ival = 0;
		int i;
		for (i = 0; i != getBits(); i++)
			if (getVolts()[i] > Pin.VOLTAGE_THRESHOLD_LEVEL)
				ival |= 1 << i;
		int ivalmax = (1 << getBits()) - 1;
		double v = ival * getVolts()[getBits() + 1] / ivalmax;
		sim.updateVoltageSource(0, getNodes()[getBits()], getPins()[getBits()].getVoltageSource(), v);
	}

	public String getChipName() {
		return "DAC";
	}

	public int getDumpType() {
		return 166;
	}

	public int getPostCount() {
		return getBits() + 2;
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	public boolean needsBits() {
		return true;
	}

	public void setupPins() {
		setSizeX(2);
		setSizeY(getBits() > 2 ? getBits() : 2);
		setPins(new Pin[getPostCount()]);
		int i;
		for (i = 0; i != getBits(); i++)
			getPins()[i] = new Pin(getBits() - 1 - i, Side.WEST, "D" + i);
		getPins()[getBits()] = new Pin(0, Side.EAST, "O");
		getPins()[getBits()].setOutput(true);
		getPins()[getBits() + 1] = new Pin(getSizeY() - 1, Side.EAST, "V+");
		allocNodes();
	}
}
