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
import com.joebotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class FullAdderElm extends ChipElm {
	public FullAdderElm(int xx, int yy) {
		super(xx, yy);
	}

	public FullAdderElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	public void execute() {
		getPins()[0].setValue((getPins()[2].isValue() ^ getPins()[3].isValue()) ^ getPins()[4].isValue());
		getPins()[1].setValue((getPins()[2].isValue() && getPins()[3].isValue())
				|| (getPins()[2].isValue() && getPins()[4].isValue())
				|| (getPins()[3].isValue() && getPins()[4].isValue()));
	}

	public String getChipName() {
		return "Full Adder";
	}

	public int getDumpType() {
		return 196;
	}

	public int getPostCount() {
		return 5;
	}

	public int getVoltageSourceCount() {
		return 2;
	}

	boolean hasReset() {
		return false;
	}

	public void setupPins() {
		setSizeX(2);
		setSizeY(3);
		setPins(new Pin[getPostCount()]);

		getPins()[0] = new Pin(2, SIDE_E, "S");
		getPins()[0].setOutput(true);
		getPins()[1] = new Pin(0, SIDE_E, "C");
		getPins()[1].setOutput(true);
		getPins()[2] = new Pin(0, SIDE_W, "A");
		getPins()[3] = new Pin(1, SIDE_W, "B");
		getPins()[4] = new Pin(2, SIDE_W, "Cin");

	}

}
