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

package com.joebotics.simmer.client.elcomp;

import com.joebotics.simmer.client.gui.widget.Checkbox;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


// import java.awt.*;
//import java.util.StringTokenizer;

public class SwitchElm extends AbstractCircuitElement {
	private boolean momentary;
	// position 0 == closed, position 1 == open
	int position, posCount;

	Point ps, ps2;

	public SwitchElm(int xx, int yy) {
		super(xx, yy);
		momentary = false;
		position = 0;
		posCount = 2;
	}

	SwitchElm(int xx, int yy, boolean mm) {
		super(xx, yy);
		position = (mm) ? 1 : 0;
		momentary = mm;
		posCount = 2;
	}

	public SwitchElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		String str = st.nextToken();
		if (str.compareTo("true") == 0)
			position = (this instanceof LogicInputElm) ? 0 : 1;
		else if (str.compareTo("false") == 0)
			position = (this instanceof LogicInputElm) ? 1 : 0;
		else
			position = new Integer(str).intValue();
		momentary = new Boolean(st.nextToken()).booleanValue();
		posCount = 2;
	}

	public void calculateCurrent() {
		if (position == 1)
			setCurrent(0);
	}

	public void draw(Graphics g) {
		int openhs = 16;
		int hs1 = (position == 1) ? 0 : 2;
		int hs2 = (position == 1) ? openhs : 2;
		setBbox(getPoint1(), getPoint2(), openhs);

		draw2Leads(g);

		if (position == 0)
			doDots(g);

		if (!needsHighlight())
			g.setColor(whiteColor);
		interpPoint(getLead1(), getLead2(), ps, 0, hs1);
		interpPoint(getLead1(), getLead2(), ps2, 1, hs2);

		GraphicsUtil.drawThickLine(g, ps, ps2);
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + position + " " + momentary;
	}

	public boolean getConnection(int n1, int n2) {
		return position == 0;
	}

	public int getDumpType() {
		return 's';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Momentary Switch", momentary);
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = (momentary) ? "push switch (SPST)" : "switch (SPST)";
		if (position == 1) {
			arr[1] = "open";
			arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
		} else {
			arr[1] = "closed";
			arr[2] = "V = " + getVoltageText(getVolts()[0]);
			arr[3] = "I = " + getCurrentDText(getCurrent());
		}
	}

	public int getShortcut() {
		return 's';
	}

	public int getVoltageSourceCount() {
		return (position == 1) ? 0 : 1;
	}

	public boolean isWire() {
		return true;
	}

	public void mouseUp() {
		if (momentary)
			toggle();
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			momentary = ei.checkbox.getState();
	}

	public void setPoints() {
		super.setPoints();
		calcLeads(32);
		ps = new Point();
		ps2 = new Point();
	}

	public void stamp() {
		if (position == 0)
			sim.stampVoltageSource(getNodes()[0], getNodes()[1], getVoltSource(), 0);
	}

	public void toggle() {
		position++;
		if (position >= posCount)
			position = 0;
	}

	public boolean isMomentary() {
		return momentary;
	}

	public void setMomentary(boolean momentary) {
		this.momentary = momentary;
	}
}
