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

// contributed by Edward Calver

public class SipoShiftElm extends ChipElm {
	// and it's screwing with my code
	boolean clockstate = false;

	short data = 0;// This has to be a short because there's no unsigned byte

	public SipoShiftElm(int xx, int yy) {
		super(xx, yy);
	}

	public SipoShiftElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}
					public void execute() {

		if (getPins()[1].isValue() && !clockstate) {
			clockstate = true;
			data = (short) (data >>> 1);
			if (getPins()[0].isValue())
				data += 128;

			if ((data & 128) > 0)
				getPins()[2].setValue(true);
			else
				getPins()[2].setValue(false);
			if ((data & 64) > 0)
				getPins()[3].setValue(true);
			else
				getPins()[3].setValue(false);
			if ((data & 32) > 0)
				getPins()[4].setValue(true);
			else
				getPins()[4].setValue(false);
			if ((data & 16) > 0)
				getPins()[5].setValue(true);
			else
				getPins()[5].setValue(false);
			if ((data & 8) > 0)
				getPins()[6].setValue(true);
			else
				getPins()[6].setValue(false);
			if ((data & 4) > 0)
				getPins()[7].setValue(true);
			else
				getPins()[7].setValue(false);
			if ((data & 2) > 0)
				getPins()[8].setValue(true);
			else
				getPins()[8].setValue(false);
			if ((data & 1) > 0)
				getPins()[9].setValue(true);
			else
				getPins()[9].setValue(false);
		}
		if (!getPins()[1].isValue())
			clockstate = false;
	}

	public String getChipName() {
		return "SIPO shift register";
	}

	public int getDumpType() {
		return 189;
	}

	public int getPostCount() {
		return 10;
	}

	public int getVoltageSourceCount() {
		return 8;
	}

	boolean hasReset() {
		return false;
	}

	public void setupPins() {
		setSizeX(9);
		setSizeY(3);
		setPins(new Pin[getPostCount()]);

		getPins()[0] = new Pin(1, SIDE_W, "D");
		getPins()[1] = new Pin(2, SIDE_W, "");
		getPins()[1].setClock(true);

		getPins()[2] = new Pin(1, SIDE_N, "I7");
		getPins()[2].setOutput(true);
		getPins()[3] = new Pin(2, SIDE_N, "I6");
		getPins()[3].setOutput(true);
		getPins()[4] = new Pin(3, SIDE_N, "I5");
		getPins()[4].setOutput(true);
		getPins()[5] = new Pin(4, SIDE_N, "I4");
		getPins()[5].setOutput(true);
		getPins()[6] = new Pin(5, SIDE_N, "I3");
		getPins()[6].setOutput(true);
		getPins()[7] = new Pin(6, SIDE_N, "I2");
		getPins()[7].setOutput(true);
		getPins()[8] = new Pin(7, SIDE_N, "I1");
		getPins()[8].setOutput(true);
		getPins()[9] = new Pin(8, SIDE_N, "I0");
		getPins()[9].setOutput(true);

	}

}
