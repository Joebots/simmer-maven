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

public class PhaseCompElm extends ChipElm {
	boolean ff1, ff2;

	public PhaseCompElm(int xx, int yy) {
		super(xx, yy);
	}

	public PhaseCompElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	public void doStep() {
		boolean v1 = getVolts()[0] > 2.5;
		boolean v2 = getVolts()[1] > 2.5;
		if (v1 && !getPins()[0].isValue())
			ff1 = true;
		if (v2 && !getPins()[1].isValue())
			ff2 = true;
		if (ff1 && ff2)
			ff1 = ff2 = false;
		double out = (ff1) ? 5 : (ff2) ? 0 : -1;
		// System.out.println(out + " " + v1 + " " + v2);
		if (out != -1)
			sim.stampVoltageSource(0, getNodes()[2], getPins()[2].getVoltSource(), out);
		else {
			// tie current through output pin to 0
			int vn = sim.getNodeList().size() + getPins()[2].getVoltSource();
			sim.stampMatrix(vn, vn, 1);
		}
		getPins()[0].setValue(v1);
		getPins()[1].setValue(v2);
	}

	public String getChipName() {
		return "phase comparator";
	}

	public int getDumpType() {
		return 161;
	}

	public int getPostCount() {
		return 3;
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	public boolean nonLinear() {
		return true;
	}

	public void setupPins() {
		setSizeX(2);
		setSizeY(2);
		setPins(new Pin[3]);
		getPins()[0] = new Pin(0, SIDE_W, "I1");
		getPins()[1] = new Pin(1, SIDE_W, "I2");
		getPins()[2] = new Pin(0, SIDE_E, "O");
		getPins()[2].setOutput(true);
	}

	public void stamp() {
		int vn = sim.getNodeList().size() + getPins()[2].getVoltSource();
		sim.stampNonLinear(vn);
		sim.stampNonLinear(0);
		sim.stampNonLinear(getNodes()[2]);
	}
}
