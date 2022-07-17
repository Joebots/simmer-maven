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

public class DecadeElm extends ChipElm {
	public DecadeElm(int xx, int yy) {
		super(xx, yy);
	}

	public DecadeElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	public void execute() {
		int i;
		if (getPins()[0].getValue() && !isLastClock()) {
			for (i = 0; i != getBits(); i++)
				if (getPins()[i + 2].getValue())
					break;
			if (i < getBits())
				getPins()[i++ + 2].setValue(false);
			i %= getBits();
			getPins()[i + 2].setValue(true);
		}
		if (!getPins()[1].getValue()) {
			for (i = 1; i != getBits(); i++)
				getPins()[i + 2].setValue(false);
			getPins()[2].setValue(true);
		}
		setLastClock(getPins()[0].getValue());
	}

	public String getChipName() {
		return "decade counter";
	}

	public int getDumpType() {
		return 163;
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
		setSizeX(getBits() > 2 ? getBits() : 2);
		setSizeY(2);
		setPins(new Pin[getPostCount()]);
		getPins()[0] = new Pin(1, Side.WEST, "");
		getPins()[0].setClock(true);
		getPins()[1] = new Pin(getSizeX() - 1, Side.SOUTH, "R");
		getPins()[1].setBubble(true);
		int i;
		for (i = 0; i != getBits(); i++) {
			int ii = i + 2;
			getPins()[ii] = new Pin(i, Side.NORTH, "Q" + i);
			getPins()[ii].setOutput(getPins()[ii].setState(true));
		}
		allocNodes();
	}
}
