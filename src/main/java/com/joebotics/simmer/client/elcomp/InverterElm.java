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
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;




//import java.awt.*;
//import java.util.StringTokenizer;

public class InverterElm extends AbstractCircuitElement {
	Polygon gatePoly;

	Point pcircle;

	double slewRate; // V/ns

	public InverterElm(int xx, int yy) {
		super(xx, yy);
		setNoDiagonal(true);
		slewRate = .5;
	}

	public InverterElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		setNoDiagonal(true);
		try {
			slewRate = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
			slewRate = .5;
		}
	}

	public void doStep() {
		double v0 = getVolts()[1];
		double out = getVolts()[0] > 2.5 ? 0 : 5;
		double maxStep = slewRate * sim.getTimeStep() * 1e9;
		out = Math.max(Math.min(v0 + maxStep, out), v0 - maxStep);
		sim.updateVoltageSource(0, getNodes()[1], getVoltSource(), out);
	}

	public void draw(Graphics g) {
		drawPosts(g);
		draw2Leads(g);
		g.setColor(needsHighlight() ? selectColor : lightGrayColor);
		GraphicsUtil.drawThickPolygon(g, gatePoly);
		GraphicsUtil.drawThickCircle(g, pcircle.getX(), pcircle.getY(), 3);
		setCurcount(updateDotCount(getCurrent(), getCurcount()));
		drawDots(g, getLead2(), getPoint2(), getCurcount());
	}
	public String dump() {
		return super.dump() + " " + slewRate;
	}

	// there is no current path through the inverter input, but there
	// is an indirect path through the output to ground.
	public boolean getConnection(int n1, int n2) {
		return false;
	}

	public int getDumpType() {
		return 'I';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Slew Rate (V/ns)", slewRate, 0, 0);
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "inverter";
		arr[1] = "Vi = " + getVoltageText(getVolts()[0]);
		arr[2] = "Vo = " + getVoltageText(getVolts()[1]);
	}

	public int getShortcut() {
		return '1';
	}

	public double getVoltageDiff() {
		return getVolts()[0];
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	public boolean hasGroundConnection(int n1) {
		return (n1 == 1);
	}

	public void setEditValue(int n, EditInfo ei) {
		slewRate = ei.value;
	}

	public void setPoints() {
		super.setPoints();
		int hs = 16;
		int ww = 16;
		if (ww > getDn() / 2)
			ww = (int) (getDn() / 2);
		setLead1(interpPoint(getPoint1(), getPoint2(), .5 - ww / getDn()));
		setLead2(interpPoint(getPoint1(), getPoint2(), .5 + (ww + 2) / getDn()));
		pcircle = interpPoint(getPoint1(), getPoint2(), .5 + (ww - 2) / getDn());
		Point triPoints[] = newPointArray(3);
		interpPoint2(getLead1(), getLead2(), triPoints[0], triPoints[1], 0, hs);
		triPoints[2] = interpPoint(getPoint1(), getPoint2(), .5 + (ww - 5) / getDn());
		gatePoly = createPolygon(triPoints);
		setBbox(getPoint1(), getPoint2(), hs);
	}

	public void stamp() {
		sim.stampVoltageSource(0, getNodes()[1], getVoltSource());
	}
}
