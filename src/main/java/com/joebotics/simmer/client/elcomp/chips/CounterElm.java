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
import com.joebotics.simmer.client.gui.widget.Checkbox;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class CounterElm extends ChipElm {
	final int FLAG_ENABLE = 2;
	boolean invertreset = false;

	public CounterElm(int xx, int yy) {
		super(xx, yy);
	}

	public CounterElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		if (st.hasMoreTokens())
			invertreset = new Boolean(st.nextToken()).booleanValue();
		else
			invertreset = true;
		getPins()[1].setBubble(invertreset);
	}

	public String dump() {
		return super.dump() + " " + invertreset;
	}

	public void execute() {
		boolean en = true;
		if (hasEnable())
			en = getPins()[getBits() + 2].isValue();
		if (getPins()[0].isValue() && !isLastClock() && en) {
			int i;
			for (i = getBits() - 1; i >= 0; i--) {
				int ii = i + 2;
				if (!getPins()[ii].isValue()) {
					getPins()[ii].setValue(true);
					break;
				}
				getPins()[ii].setValue(false);
			}
		}
		if (!getPins()[1].isValue() == invertreset) {
			int i;
			for (i = 0; i != getBits(); i++)
				getPins()[i + 2].setValue(false);
		}
		setLastClock(getPins()[0].isValue());
	}

	public String getChipName() {
		return "Counter";
	}

	public int getDumpType() {
		return 164;
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Flip X", (getFlags() & FLAG_FLIP_X) != 0);
			return ei;
		}
		if (n == 1) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Flip Y", (getFlags() & FLAG_FLIP_Y) != 0);
			return ei;
		}
		if (n == 2) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Invert reset pin", invertreset);
			return ei;
		}
		return null;
	}

	public int getPostCount() {
		if (hasEnable())
			return getBits() + 3;
		return getBits() + 2;
	}

	public int getVoltageSourceCount() {
		return getBits();
	}

	boolean hasEnable() {
		return (getFlags() & FLAG_ENABLE) != 0;
	}

	public boolean needsBits() {
		return true;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			if (ei.checkbox.getState())
				setFlags(getFlags() | FLAG_FLIP_X);
			else
				setFlags(getFlags() & ~FLAG_FLIP_X);
			setPoints();
		}
		if (n == 1) {
			if (ei.checkbox.getState())
				setFlags(getFlags() | FLAG_FLIP_Y);
			else
				setFlags(getFlags() & ~FLAG_FLIP_Y);
			setPoints();
		}
		if (n == 2) {
			if (ei.checkbox.getState()) {
				invertreset = true;
				getPins()[1].setBubble(true);
			} else {
				invertreset = false;
				getPins()[1].setBubble(false);
			}
			setPoints();
		}
	}

	public void setupPins() {
		setSizeX(2);
		setSizeY(getBits() > 2 ? getBits() : 2);
		setPins(new Pin[getPostCount()]);
		getPins()[0] = new Pin(0, SIDE_W, "");
		getPins()[0].setClock(true);
		getPins()[1] = new Pin(getSizeY() - 1, SIDE_W, "R");
		getPins()[1].setBubble(invertreset);
		int i;
		for (i = 0; i != getBits(); i++) {
			int ii = i + 2;
			getPins()[ii] = new Pin(i, SIDE_E, "Q" + (getBits() - i - 1));
			getPins()[ii].setOutput(getPins()[ii].setState(true));
		}
		if (hasEnable())
			getPins()[getBits() + 2] = new Pin(getSizeY() - 2, SIDE_W, "En");
		allocNodes();
	}
}
