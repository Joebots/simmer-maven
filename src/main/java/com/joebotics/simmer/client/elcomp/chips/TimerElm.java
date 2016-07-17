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

public class TimerElm extends ChipElm {
	final int FLAG_RESET = 2;
	final int N_CTL = 4;
	final int N_DIS = 0;
	final int N_OUT = 5;
	final int N_RST = 6;
	final int N_THRES = 2;
	final int N_TRIG = 1;
	final int N_VIN = 3;

	boolean setOut, out;

	public TimerElm(int xx, int yy) {
		super(xx, yy);
	}

	public TimerElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	public void calculateCurrent() {
		// need current for V, discharge, control; output current is
		// calculated for us, and other pins have no current
		getPins()[N_VIN].setCurrent((getVolts()[N_CTL] - getVolts()[N_VIN]) / 5000);
		getPins()[N_CTL].setCurrent(-getVolts()[N_CTL] / 10000 - getPins()[N_VIN].getCurrent());
		getPins()[N_DIS].setCurrent((!out && !setOut) ? -getVolts()[N_DIS] / 10 : 0);
	}

	public void doStep() {
		// if output is low, discharge pin 0. we use a small
		// resistor because it's easier, and sometimes people tie
		// the discharge pin to the trigger and threshold pins.
		// We check setOut to properly emulate the case where
		// trigger is low and threshold is high.
		if (!out && !setOut)
			sim.stampResistor(getNodes()[N_DIS], 0, 10);
		// output
		sim.updateVoltageSource(0, getNodes()[N_OUT], getPins()[N_OUT].getVoltSource(),
				out ? getVolts()[N_VIN] : 0);
	}

	public String getChipName() {
		return "555 Timer";
	}

	int getDefaultFlags() {
		return FLAG_RESET;
	}

	public int getDumpType() {
		return 165;
	}

	public int getPostCount() {
		return hasReset() ? 7 : 6;
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	boolean hasReset() {
		return (getFlags() & FLAG_RESET) != 0;
	}

	public boolean nonLinear() {
		return true;
	}

	public void setupPins() {
		setSizeX(3);
		setSizeY(5);
		setPins(new Pin[7]);
		getPins()[N_DIS] = new Pin(1, SIDE_W, "dis");
		getPins()[N_TRIG] = new Pin(3, SIDE_W, "tr");
		getPins()[N_TRIG].setLineOver(true);
		getPins()[N_THRES] = new Pin(4, SIDE_W, "th");
		getPins()[N_VIN] = new Pin(1, SIDE_N, "Vin");
		getPins()[N_CTL] = new Pin(1, SIDE_S, "ctl");
		getPins()[N_OUT] = new Pin(2, SIDE_E, "out");
		getPins()[N_OUT].setOutput(getPins()[N_OUT].setState(true));
		getPins()[N_RST] = new Pin(1, SIDE_E, "rst");
	}

	public void stamp() {
		// stamp voltage divider to put ctl pin at 2/3 V
		sim.stampResistor(getNodes()[N_VIN], getNodes()[N_CTL], 5000);
		sim.stampResistor(getNodes()[N_CTL], 0, 10000);
		// output pin
		sim.stampVoltageSource(0, getNodes()[N_OUT], getPins()[N_OUT].getVoltSource());
		// discharge pin
		sim.stampNonLinear(getNodes()[N_DIS]);
	}

	public void startIteration() {
		out = getVolts()[N_OUT] > getVolts()[N_VIN] / 2;
		setOut = false;
		// check comparators
		if (getVolts()[N_CTL] / 2 > getVolts()[N_TRIG])
			setOut = out = true;
		if (getVolts()[N_THRES] > getVolts()[N_CTL] || (hasReset() && getVolts()[N_RST] < .7))
			out = false;
	}
}
