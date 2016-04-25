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
import com.jobotics.simmer.client.gui.impl.Checkbox;
import com.jobotics.simmer.client.gui.impl.EditInfo;
import com.jobotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class JKFlipFlopElm extends ChipElm {
	final int FLAG_RESET = 2;

	public JKFlipFlopElm(int xx, int yy) {
		super(xx, yy);
	}

	public JKFlipFlopElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		getPins()[4].setValue(!getPins()[3].isValue());
	}

	public void execute() {
		if (!getPins()[1].isValue() && isLastClock()) {
			boolean q = getPins()[3].isValue();
			if (getPins()[0].isValue()) {
				if (getPins()[2].isValue())
					q = !q;
				else
					q = true;
			} else if (getPins()[2].isValue())
				q = false;
			getPins()[3].setValue(q);
			getPins()[4].setValue(!q);
		}
		setLastClock(getPins()[1].isValue());

		if (hasReset()) {
			if (getPins()[5].isValue()) {
				getPins()[3].setValue(false);
				getPins()[4].setValue(true);
			}
		}
	}

	public String getChipName() {
		return "JK flip-flop";
	}

	public int getDumpType() {
		return 156;
	}

	public EditInfo getEditInfo(int n) {
		if (n == 2) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Reset Pin", hasReset());
			return ei;
		}

		return super.getEditInfo(n);
	}

	public int getPostCount() {
		return 5 + (hasReset() ? 1 : 0);
	}

	public int getVoltageSourceCount() {
		return 2;
	}

	boolean hasReset() {
		return (getFlags() & FLAG_RESET) != 0;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 2) {
			if (ei.checkbox.getState()) {
				setFlags(getFlags() | FLAG_RESET);
			} else {
				setFlags(getFlags() & ~FLAG_RESET);
			}

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
		getPins()[0] = new Pin(0, SIDE_W, "J");
		getPins()[1] = new Pin(1, SIDE_W, "");
		getPins()[1].setClock(true);
		getPins()[1].setBubble(true);
		getPins()[2] = new Pin(2, SIDE_W, "K");
		getPins()[3] = new Pin(0, SIDE_E, "Q");
		getPins()[3].setOutput(getPins()[3].setState(true));
		getPins()[4] = new Pin(2, SIDE_E, "Q");
		getPins()[4].setOutput(true);
		getPins()[4].setLineOver(true);

		if (hasReset()) {
			getPins()[5] = new Pin(1, SIDE_E, "R");
		}
	}
}
