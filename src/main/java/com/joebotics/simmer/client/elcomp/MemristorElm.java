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

import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.gui.impl.EditInfo;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;

public class MemristorElm extends AbstractCircuitElement {
	Point ps3, ps4;

	double r_on, r_off, dopeWidth, totalWidth, mobility, resistance;

	public MemristorElm(int xx, int yy) {
		super(xx, yy);
		r_on = 100;
		r_off = 160 * r_on;
		dopeWidth = 0;
		totalWidth = 10e-9; // meters
		mobility = 1e-10; // m^2/sV
		resistance = 100;
	}

	public MemristorElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		r_on = new Double(st.nextToken()).doubleValue();
		r_off = new Double(st.nextToken()).doubleValue();
		dopeWidth = new Double(st.nextToken()).doubleValue();
		totalWidth = new Double(st.nextToken()).doubleValue();
		mobility = new Double(st.nextToken()).doubleValue();
		resistance = 100;
	}

	public void calculateCurrent() {
		setCurrent((getVolts()[0] - getVolts()[1]) / resistance);
	}

	public void doStep() {
		sim.stampResistor(getNodes()[0], getNodes()[1], resistance);
	}

	public void draw(Graphics g) {
		int segments = 6;
		int i;
		int ox = 0;
		double v1 = getVolts()[0];
		double v2 = getVolts()[1];
		int hs = 2 + (int) (8 * (1 - dopeWidth / totalWidth));
		setBbox(getPoint1(), getPoint2(), hs);
		draw2Leads(g);
		setPowerColor(g, true);
		double segf = 1. / segments;

		// draw zigzag
		for (i = 0; i <= segments; i++) {
			int nx = (i & 1) == 0 ? 1 : -1;
			if (i == segments)
				nx = 0;
			double v = v1 + (v2 - v1) * i / segments;
			setVoltageColor(g, v);
			interpPoint(getLead1(), getLead2(), ps1, i * segf, hs * ox);
			interpPoint(getLead1(), getLead2(), ps2, i * segf, hs * nx);
			GraphicsUtil.drawThickLine(g, ps1, ps2);
			if (i == segments)
				break;
			interpPoint(getLead1(), getLead2(), ps1, (i + 1) * segf, hs * nx);
			GraphicsUtil.drawThickLine(g, ps1, ps2);
			ox = nx;
		}

		doDots(g);
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + r_on + " " + r_off + " " + dopeWidth + " "
				+ totalWidth + " " + mobility;
	}

	public int getDumpType() {
		return 'm';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Max Resistance (ohms)", r_on, 0, 0);
		if (n == 1)
			return new EditInfo("Min Resistance (ohms)", r_off, 0, 0);
		if (n == 2)
			return new EditInfo("Width of Doped Region (nm)", dopeWidth * 1e9,
					0, 0);
		if (n == 3)
			return new EditInfo("Total Width (nm)", totalWidth * 1e9, 0, 0);
		if (n == 4)
			return new EditInfo("Mobility (um^2/(s*V))", mobility * 1e12, 0, 0);
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "memristor";
		getBasicInfo(arr);
		arr[3] = "R = " + getUnitText(resistance, Simmer.ohmString);
		arr[4] = "P = " + getUnitText(getPower(), "W");
	}

	public String getScopeUnits(int x) {
		return (x == 2) ? Simmer.ohmString : (x == 1) ? "W" : "V";
	}

	public double getScopeValue(int x) {
		return (x == 2) ? resistance : (x == 1) ? getPower() : getVoltageDiff();
	}

	public boolean nonLinear() {
		return true;
	}

	public void reset() {
		dopeWidth = 0;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			r_on = ei.value;
		if (n == 1)
			r_off = ei.value;
		if (n == 2)
			dopeWidth = ei.value * 1e-9;
		if (n == 3)
			totalWidth = ei.value * 1e-9;
		if (n == 4)
			mobility = ei.value * 1e-12;
	}

	public void setPoints() {
		super.setPoints();
		calcLeads(32);
		ps3 = new Point();
		ps4 = new Point();
	}

	public void stamp() {
		sim.stampNonLinear(getNodes()[0]);
		sim.stampNonLinear(getNodes()[1]);
	}

	public void startIteration() {
		double wd = dopeWidth / totalWidth;
		dopeWidth += sim.getTimeStep() * mobility * r_on * getCurrent() / totalWidth;
		if (dopeWidth < 0)
			dopeWidth = 0;
		if (dopeWidth > totalWidth)
			dopeWidth = totalWidth;
		resistance = r_on * wd + r_off * (1 - wd);
	}
}
