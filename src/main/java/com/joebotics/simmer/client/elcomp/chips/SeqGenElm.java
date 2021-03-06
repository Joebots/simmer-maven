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
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.util.StringTokenizer;

import gwt.material.design.client.ui.MaterialCheckBox;

//import java.awt.*;
//import java.util.StringTokenizer;

// contributed by Edward Calver

public class SeqGenElm extends ChipElm {
	boolean clockstate = false;

	short data = 0;

	double lastchangetime = 0;

	boolean oneshot = false;
	byte position = 0;
	public SeqGenElm(int xx, int yy) {
		super(xx, yy);
	}
	public SeqGenElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		data = (short) (new Integer(st.nextToken()).intValue());
		if (st.hasMoreTokens()) {
			oneshot = new Boolean(st.nextToken()).booleanValue();
			position = 8;
		}
	}
	public String dump() {
		return super.dump() + " " + data + " " + oneshot;
	}

	public void execute() {
		if (oneshot) {
			if (sim.getT() - lastchangetime > 0.005) {
				if (position <= 8)
					GetNextBit();
				lastchangetime = sim.getT();
			}
		}
		if (getPins()[0].getValue() && !clockstate) {
			clockstate = true;
			if (oneshot) {
				position = 0;
			} else {
				GetNextBit();
				if (position >= 8)
					position = 0;
			}
		}
		if (!getPins()[0].getValue())
			clockstate = false;

	}

	public String getChipName() {
		return "Sequence generator";
	}

	public int getDumpType() {
		return 188;
	}

	public EditInfo getEditInfo(int n) {
		// My code
		if (n == 0) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Bit 0 set");
            ei.checkbox.setValue((data & 1) != 0);
			return ei;
		}

		if (n == 1) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Bit 1 set");
            ei.checkbox.setValue((data & 2) != 0);
			return ei;
		}
		if (n == 2) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Bit 2 set");
            ei.checkbox.setValue((data & 4) != 0);
			return ei;
		}
		if (n == 3) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Bit 3 set");
            ei.checkbox.setValue((data & 8) != 0);
			return ei;
		}

		if (n == 4) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Bit 4 set");
            ei.checkbox.setValue((data & 16) != 0);
			return ei;
		}
		if (n == 5) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Bit 5 set");
            ei.checkbox.setValue((data & 32) != 0);
			return ei;
		}

		if (n == 6) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Bit 6 set");
            ei.checkbox.setValue((data & 64) != 0);
			return ei;
		}

		if (n == 7) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Bit 7 set");
            ei.checkbox.setValue((data & 128) != 0);
			return ei;
		}
		if (n == 8) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("One shot");
            ei.checkbox.setValue(oneshot);
			return ei;
		}
		return null;
	}

	void GetNextBit() {
		if (((data >>> position) & 1) != 0)
			getPins()[1].setValue(true);
		else
			getPins()[1].setValue(false);
		position++;
	}

	public int getPostCount() {
		return 2;
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	boolean hasReset() {
		return false;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			if (ei.checkbox.getValue())
				data |= 1;
			else
				data &= ~1;
			setPoints();
		}
		if (n == 1) {
			if (ei.checkbox.getValue())
				data |= 2;
			else
				data &= ~2;
			setPoints();
		}
		if (n == 2) {
			if (ei.checkbox.getValue())
				data |= 4;
			else
				data &= ~4;
			setPoints();
		}
		if (n == 3) {
			if (ei.checkbox.getValue())
				data |= 8;
			else
				data &= ~8;
			setPoints();
		}
		if (n == 4) {
			if (ei.checkbox.getValue())
				data |= 16;
			else
				data &= ~16;
			setPoints();
		}
		if (n == 5) {
			if (ei.checkbox.getValue())
				data |= 32;
			else
				data &= ~32;
			setPoints();
		}
		if (n == 6) {
			if (ei.checkbox.getValue())
				data |= 64;
			else
				data &= ~64;
			setPoints();
		}
		if (n == 7) {
			if (ei.checkbox.getValue())
				data |= 128;
			else
				data &= ~128;
			setPoints();
		}
		if (n == 8) {
			if (ei.checkbox.getValue()) {
				oneshot = true;
				position = 8;
			} else {
				position = 0;
				oneshot = false;
			}
		}

	}

	public void setupPins() {
		setSizeX(2);
		setSizeY(2);
		setPins(new Pin[getPostCount()]);

		getPins()[0] = new Pin(0, Side.WEST, "");
		getPins()[0].setClock(true);
		getPins()[1] = new Pin(1, Side.EAST, "Q");
		getPins()[1].setOutput(true);
	}

}
