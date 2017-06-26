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

public class LatchElm extends ChipElm {
	boolean lastLoad = false;

	int loadPin;

	public LatchElm(int xx, int yy) {
		super(xx, yy);
	}

	public LatchElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	public void execute() {
		int i;
		if (getPins()[loadPin].isValue() && !lastLoad)
			for (i = 0; i != getBits(); i++)
				getPins()[i + getBits()].setValue(getPins()[i].isValue());
		lastLoad = getPins()[loadPin].isValue();
	}

	public String getChipName() {
		return "Latch";
	}

	public int getDumpType() {
		return 168;
	}

	public int getPostCount() {
		return getBits() * 2 + 1;
	}

	public int getVoltageSourceCount() {
		return getBits();
	}

	public boolean needsBits() {
		return true;
	}

	public void setupPins() {
		setSizeX(2);
		setSizeY(getBits() + 1);
		setPins(new Pin[getPostCount()]);
		int i;
		for (i = 0; i != getBits(); i++)
			getPins()[i] = new Pin(getBits() - 1 - i, Side.WEST, "I" + i);
		for (i = 0; i != getBits(); i++) {
			getPins()[i + getBits()] = new Pin(getBits() - 1 - i, Side.EAST, "O");
			getPins()[i + getBits()].setOutput(true);
		}
		getPins()[loadPin = getBits() * 2] = new Pin(getBits(), Side.WEST, "Ld");
		allocNodes();
	}
}
