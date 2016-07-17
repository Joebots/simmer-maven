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
import com.joebotics.simmer.client.gui.impl.Checkbox;
import com.joebotics.simmer.client.gui.impl.EditInfo;
import com.joebotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class MonostableElm extends ChipElm {

	private double delay = 0.01;
	private double lastRisingEdge = 0;
	// Used to detect rising edge
	private boolean prevInputValue = false;
	private boolean retriggerable = false;
	private boolean triggered = false;

	public MonostableElm(int xx, int yy) {
		super(xx, yy);
	}

	public MonostableElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		retriggerable = new Boolean(st.nextToken()).booleanValue();
		delay = new Double(st.nextToken()).doubleValue();
	}

	public String dump() {
		return super.dump() + " " + retriggerable + " " + delay;
	}

	public void execute() {

		if (getPins()[0].isValue() && prevInputValue != getPins()[0].isValue()
				&& (retriggerable || !triggered)) {
			lastRisingEdge = sim.getT();
			getPins()[1].setValue(true);
			getPins()[2].setValue(false);
			triggered = true;
		}

		if (triggered && sim.getT() > lastRisingEdge + delay) {
			getPins()[1].setValue(false);
			getPins()[2].setValue(true);
			triggered = false;
		}
		prevInputValue = getPins()[0].isValue();
	}

	public String getChipName() {
		return "Monostable";
	}

	public int getDumpType() {
		return 194;
	}

	public EditInfo getEditInfo(int n) {
		if (n == 2) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Retriggerable", retriggerable);
			return ei;
		}
		if (n == 3) {
			EditInfo ei = new EditInfo("Period (s)", delay, 0.001, 0.1);
			return ei;
		}
		return super.getEditInfo(n);
	}

	public int getPostCount() {
		return 3;
	}

	public int getVoltageSourceCount() {
		return 2;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 2) {
			retriggerable = ei.checkbox.getState();
		}
		if (n == 3) {
			delay = ei.value;
		}
		super.setEditValue(n, ei);
	}

	public void setupPins() {
		setSizeX(2);
		setSizeY(2);
		setPins(new Pin[getPostCount()]);
		getPins()[0] = new Pin(0, SIDE_W, "");
		getPins()[0].setClock(true);
		getPins()[1] = new Pin(0, SIDE_E, "Q");
		getPins()[1].setOutput(true);
		getPins()[2] = new Pin(1, SIDE_E, "Q");
		getPins()[2].setOutput(true);
		getPins()[2].setLineOver(true);
	}
}
