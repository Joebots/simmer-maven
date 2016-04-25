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
import com.jobotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class ADCElm extends ChipElm {
	public ADCElm(int xx, int yy) {
		super(xx, yy);
	}

	public ADCElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	public void execute() {
		int imax = (1 << getBits()) - 1;
		// if we round, the half-flash doesn't work
		double val = imax * getVolts()[getBits()] / getVolts()[getBits() + 1]; // + .5;
		int ival = (int) val;
		ival = min(imax, max(0, ival));
		int i;
		for (i = 0; i != getBits(); i++)
			getPins()[i].setValue(((ival & (1 << i)) != 0));
	}

	public String getChipName() {
		return "ADC";
	}

	public int getDumpType() {
		return 167;
	}

	public int getPostCount() {
		return getBits() + 2;
	}

	public int getVoltageSourceCount() {
		return getBits();
	}

	public boolean needsBits() {
		return true;
	}

	public void setupPins() {
		setSizeX(2);
		setSizeY(getBits() > 2 ? getBits() : 2);
		setPins(new Pin[getPostCount()]);
		int i;
		for (i = 0; i != getBits(); i++) {
			getPins()[i] = new Pin(getBits() - 1 - i, SIDE_E, "D" + i);
			getPins()[i].setOutput(true);
		}
		getPins()[getBits()] = new Pin(0, SIDE_W, "In");
		getPins()[getBits() + 1] = new Pin(getSizeY() - 1, SIDE_W, "V+");
		allocNodes();
	}
}
