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

//
//import java.awt.*;
//import java.util.StringTokenizer;

// contributed by Edward Calver

public class MultiplexerElm extends ChipElm {
	public MultiplexerElm(int xx, int yy) {
		super(xx, yy);
	}

	public MultiplexerElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	public void execute() {
		int selectedvalue = 0;
		if (getPins()[4].isValue())
			selectedvalue++;
		if (getPins()[5].isValue())
			selectedvalue += 2;
		getPins()[6].setValue(getPins()[selectedvalue].isValue());

	}

	public String getChipName() {
		return "Multiplexer";
	}

	public int getDumpType() {
		return 184;
	}

	public int getPostCount() {
		return 7;
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	boolean hasReset() {
		return false;
	}

	public void setupPins() {
		setSizeX(3);
		setSizeY(5);
		setPins(new Pin[getPostCount()]);

		getPins()[0] = new Pin(0, Side.WEST, "I0");
		getPins()[1] = new Pin(1, Side.WEST, "I1");
		getPins()[2] = new Pin(2, Side.WEST, "I2");
		getPins()[3] = new Pin(3, Side.WEST, "I3");

		getPins()[4] = new Pin(1, Side.SOUTH, "S0");
		getPins()[5] = new Pin(2, Side.SOUTH, "S1");

		getPins()[6] = new Pin(0, Side.EAST, "Q");
		getPins()[6].setOutput(true);

	}

}
