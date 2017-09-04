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

public class DFlipFlopElm extends ChipElm {
	final int FLAG_RESET = 2;
	final int FLAG_SET = 4;

	public DFlipFlopElm(int xx, int yy) {
		super(xx, yy);
	}

	public DFlipFlopElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		getPins()[2].setValue(!getPins()[1].getValue());
	}

	public void execute() {
		if (getPins()[3].getValue() && !isLastClock()) {
			getPins()[1].setValue(getPins()[0].getValue());
			getPins()[2].setValue(!getPins()[0].getValue());
		}
		if (hasSet() && getPins()[5].getValue()) {
			getPins()[1].setValue(true);
			getPins()[2].setValue(false);
		}
		if (hasReset() && getPins()[4].getValue()) {
			getPins()[1].setValue(false);
			getPins()[2].setValue(true);
		}
		setLastClock(getPins()[3].getValue());
	}

	public String getChipName() {
		return "D flip-flop";
	}

	public int getDumpType() {
		return 155;
	}

	public EditInfo getEditInfo(int n) {
		if (n == 2) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Reset Pin");
            ei.checkbox.setValue(hasReset());
			return ei;
		}
		if (n == 3) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Set Pin");
            ei.checkbox.setValue(hasSet());
			return ei;
		}
		return super.getEditInfo(n);
	}

	public int getPostCount() {
		return 4 + (hasReset() ? 1 : 0) + (hasSet() ? 1 : 0);
	}

	public int getVoltageSourceCount() {
		return 2;
	}

	boolean hasReset() {
		return (getFlags() & FLAG_RESET) != 0 || hasSet();
	}

	boolean hasSet() {
		return (getFlags() & FLAG_SET) != 0;
	}

	public void reset() {
		super.reset();
		getVolts()[2] = 5;
		getPins()[2].setValue(true);
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 2) {
			if (ei.checkbox.getValue())
				setFlags(getFlags() | FLAG_RESET);
			else
				setFlags(getFlags() & (~FLAG_RESET | FLAG_SET));
			setupPins();
			allocNodes();
			setPoints();
		}
		if (n == 3) {
			if (ei.checkbox.getValue())
				setFlags(getFlags() | FLAG_SET);
			else
				setFlags(getFlags() & ~FLAG_SET);
			setupPins();
			allocNodes();
			setPoints();
		}
		super.setEditValue(n, ei);
	}

	public void setupPins() {
		setSizeX(2);
		setSizeY(3);
		setPins(new Pin[getPostCount()]);
		getPins()[0] = new Pin(0, Side.WEST, "D");
		getPins()[1] = new Pin(0, Side.EAST, "Q");
		getPins()[1].setOutput(getPins()[1].setState(true));
		getPins()[2] = new Pin(hasSet() ? 1 : 2, Side.EAST, "Q");
		getPins()[2].setOutput(true);
		getPins()[2].setLineOver(true);
		getPins()[3] = new Pin(1, Side.WEST, "");
		getPins()[3].setClock(true);
		if (!hasSet()) {
			if (hasReset())
				getPins()[4] = new Pin(2, Side.WEST, "R");
		} else {
			getPins()[5] = new Pin(2, Side.WEST, "S");
			getPins()[4] = new Pin(2, Side.EAST, "R");
		}
	}
}
