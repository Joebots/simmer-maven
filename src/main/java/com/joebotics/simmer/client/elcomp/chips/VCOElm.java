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
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class VCOElm extends ChipElm {
	double cCurrent;

	int cDir;

	final double cResistance = 1e6;

	public VCOElm(int xx, int yy) {
		super(xx, yy);
	}

	public VCOElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	// can't do this in calculateCurrent() because it's called before
	// we get pins[4].current and pins[5].current, which we need
	void computeCurrent() {
		if (cResistance == 0)
			return;
		
		double c = cDir * (getPins()[4].getCurrent() + getPins()[5].getCurrent())
				+ (getVolts()[3] - getVolts()[2]) / cResistance;
		getPins()[2].setCurrent(-c);
		getPins()[3].setCurrent(c);
		getPins()[0].setCurrent(-getPins()[4].getCurrent());
	}

	public void doStep() {
		double vc = getVolts()[3] - getVolts()[2];
		double vo = getVolts()[1];
		int dir = (vo < 2.5) ? 1 : -1;
		// switch direction of current through cap as we oscillate
		if (vo < 2.5 && vc > 4.5) {
			vo = 5;
			dir = -1;
		}
		if (vo > 2.5 && vc < .5) {
			vo = 0;
			dir = 1;
		}

		// generate output voltage
		sim.updateVoltageSource(0, getNodes()[1], getPins()[1].getVoltageSource(), vo);
		// now we set the current through the cap to be equal to the
		// current through R1 and R2, so we can measure the voltage
		// across the cap
		int cur1 = sim.getNodeList().size() + getPins()[4].getVoltageSource();
		int cur2 = sim.getNodeList().size() + getPins()[5].getVoltageSource();
		sim.stampMatrix(getNodes()[2], cur1, dir);
		sim.stampMatrix(getNodes()[2], cur2, dir);
		sim.stampMatrix(getNodes()[3], cur1, -dir);
		sim.stampMatrix(getNodes()[3], cur2, -dir);
		cDir = dir;
	}
	public void draw(Graphics g) {
		computeCurrent();
		drawChip(g);
	}
	public String getChipName() {
		return "VCO";
	}

	public int getDumpType() {
		return 158;
	}

	public int getPostCount() {
		return 6;
	}

	public int getVoltageSourceCount() {
		return 3;
	}

	public boolean nonLinear() {
		return true;
	}

	public void setupPins() {
		setSizeX(2);
		setSizeY(4);
		setPins(new Pin[6]);
		getPins()[0] = new Pin(0, Side.WEST, "Vi");
		getPins()[1] = new Pin(3, Side.WEST, "Vo");
		getPins()[1].setOutput(true);
		getPins()[2] = new Pin(0, Side.EAST, "C");
		getPins()[3] = new Pin(1, Side.EAST, "C");
		getPins()[4] = new Pin(2, Side.EAST, "R1");
		getPins()[4].setOutput(true);
		getPins()[5] = new Pin(3, Side.EAST, "R2");
		getPins()[5].setOutput(true);
	}

	public void stamp() {
		// output pin
		sim.stampVoltageSource(0, getNodes()[1], getPins()[1].getVoltageSource());
		// attach Vi to R1 pin so its current is proportional to Vi
		sim.stampVoltageSource(getNodes()[0], getNodes()[4], getPins()[4].getVoltageSource(), 0);
		// attach 5V to R2 pin so we get a current going
		sim.stampVoltageSource(0, getNodes()[5], getPins()[5].getVoltageSource(), 5);
		// put resistor across cap pins to give current somewhere to go
		// in case cap is not connected
		sim.stampResistor(getNodes()[2], getNodes()[3], cResistance);
		sim.stampNonLinear(getNodes()[2]);
		sim.stampNonLinear(getNodes()[3]);
	}
}
