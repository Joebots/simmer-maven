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

import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Polygon;
import com.joebotics.simmer.client.util.Diode;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;

public class DiodeElm extends AbstractCircuitElement {
	static final int FLAG_FWDROP = 1;
	Point cathode[];
	final double defaultdrop = .805904783;
	Diode diode;

	double fwdrop, zvoltage;

	final int hs = 8;

	Polygon poly;

	public DiodeElm(int xx, int yy) {
		super(xx, yy);
		diode = new Diode(sim);
		fwdrop = defaultdrop;
		zvoltage = 0;
		setup();
	}

	public DiodeElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		diode = new Diode(sim);
		fwdrop = defaultdrop;
		zvoltage = 0;
		if ((f & FLAG_FWDROP) > 0) {
			try {
				fwdrop = new Double(st.nextToken()).doubleValue();
			} catch (Exception e) {
			}
		}
		setup();
	}

	public void calculateCurrent() {
		setCurrent(diode.calculateCurrent(getVolts()[0] - getVolts()[1]));
	}

	public void doStep() {
		diode.doStep(getVolts()[0] - getVolts()[1]);
	}
	public void draw(Graphics g) {
		drawDiode(g);
		doDots(g);
		drawPosts(g);
	}
	void drawDiode(Graphics g) {
		setBbox(getPoint1(), getPoint2(), hs);

		double v1 = getVolts()[0];
		double v2 = getVolts()[1];

		draw2Leads(g);

		// draw arrow thingy
		setPowerColor(g, true);
		setVoltageColor(g, v1);
		g.fillPolygon(poly);

		// draw thing arrow is pointing to
		setVoltageColor(g, v2);
		GraphicsUtil.drawThickLine(g, cathode[0], cathode[1]);
	}

	public String dump() {
		setFlags(getFlags() | FLAG_FWDROP);
		return super.dump() + " " + fwdrop;
	}

	public int getDumpType() {
		return 'd';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Fwd Voltage @ 1A", fwdrop, 10, 1000);
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "diode";
		arr[1] = "I = " + getCurrentText(getCurrent());
		arr[2] = "Vd = " + getVoltageText(getVoltageDiff());
		arr[3] = "P = " + getUnitText(getPower(), "W");
		arr[4] = "Vf = " + getVoltageText(fwdrop);
	}

	public int getShortcut() {
		return 'd';
	}

	public boolean nonLinear() {
		return true;
	}

	public void reset() {
		diode.reset();
		getVolts()[0] = getVolts()[1] = setCurcount(0);
	}

	public void setEditValue(int n, EditInfo ei) {
		fwdrop = ei.value;
		setup();
	}

	public void setPoints() {
		super.setPoints();
		calcLeads(16);
		cathode = newPointArray(2);
		Point pa[] = newPointArray(2);
		interpPoint2(getLead1(), getLead2(), pa[0], pa[1], 0, hs);
		interpPoint2(getLead1(), getLead2(), cathode[0], cathode[1], 1, hs);
		poly = createPolygon(pa[0], pa[1], getLead2());
	}

	void setup() {
		diode.setup(fwdrop, zvoltage);
	}

	public void stamp() {
		diode.stamp(getNodes()[0], getNodes()[1]);
	}
}
