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

package com.jobotics.simmer.client.elcomp;

import com.jobotics.simmer.client.gui.impl.EditInfo;
import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.gui.util.Point;
import com.jobotics.simmer.client.gui.util.Polygon;
import com.jobotics.simmer.client.util.GraphicsUtil;
import com.jobotics.simmer.client.util.StringTokenizer;




//import java.awt.*;
//import java.util.StringTokenizer;

// contributed by Edward Calver

public class InvertingSchmittElm extends AbstractCircuitElement {
	double dlt;
	double dut;
	Polygon gatePoly;
	double lowerTrigger;

	Point pcircle;

	double slewRate; // V/ns

	boolean state;

	Polygon symbolPoly;

	double upperTrigger;

	public InvertingSchmittElm(int xx, int yy) {
		super(xx, yy);
		setNoDiagonal(true);
		slewRate = .5;
		state = false;
		lowerTrigger = 1.66;
		upperTrigger = 3.33;
	}
	public InvertingSchmittElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		setNoDiagonal(true);
		try {
			slewRate = new Double(st.nextToken()).doubleValue();
			lowerTrigger = new Double(st.nextToken()).doubleValue();
			upperTrigger = new Double(st.nextToken()).doubleValue();

		} catch (Exception e) {
			slewRate = .5;
			lowerTrigger = 1.66;
			upperTrigger = 3.33;
		}
	}
	public void doStep() {
		double v0 = getVolts()[1];
		double out;
		if (state) {// Output is high
			if (getVolts()[0] > upperTrigger)// Input voltage high enough to set
										// output low
			{
				state = false;
				out = 0;
			} else {
				out = 5;
			}
		} else {// Output is low
			if (getVolts()[0] < lowerTrigger)// Input voltage low enough to set
										// output high
			{
				state = true;
				out = 5;
			} else {
				out = 0;
			}
		}

		double maxStep = slewRate * sim.getTimeStep() * 1e9;
		out = Math.max(Math.min(v0 + maxStep, out), v0 - maxStep);
		sim.updateVoltageSource(0, getNodes()[1], getVoltSource(), out);
	}

	public void draw(Graphics g) {
		drawPosts(g);
		draw2Leads(g);
		g.setColor(needsHighlight() ? selectColor : lightGrayColor);
		GraphicsUtil.drawThickPolygon(g, gatePoly);
		GraphicsUtil.drawThickPolygon(g, symbolPoly);
		GraphicsUtil.drawThickCircle(g, pcircle.getX(), pcircle.getY(), 3);
		setCurcount(updateDotCount(getCurrent(), getCurcount()));
		drawDots(g, getLead2(), getPoint2(), getCurcount());
	}

	public String dump() {
		return super.dump() + " " + slewRate + " " + lowerTrigger + " "
				+ upperTrigger;
	}

	// there is no current path through the InvertingSchmitt input, but there
	// is an indirect path through the output to ground.
	public boolean getConnection(int n1, int n2) {
		return false;
	}

	public int getDumpType() {
		return 183;
	}// Trying to find unused type

	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			dlt = lowerTrigger;
			return new EditInfo("Lower threshold (V)", lowerTrigger, 0.01, 5);
		}
		if (n == 1) {
			dut = upperTrigger;
			return new EditInfo("Upper threshold (V)", upperTrigger, 0.01, 5);
		}
		if (n == 2)
			return new EditInfo("Slew Rate (V/ns)", slewRate, 0, 0);
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "InvertingSchmitt";
		arr[1] = "Vi = " + getVoltageText(getVolts()[0]);
		arr[2] = "Vo = " + getVoltageText(getVolts()[1]);
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
		if (n == 0)
			dlt = ei.value;
		if (n == 1)
			dut = ei.value;
		if (n == 2)
			slewRate = ei.value;

		if (dlt > dut) {
			upperTrigger = dlt;
			lowerTrigger = dut;
		} else {
			upperTrigger = dut;
			lowerTrigger = dlt;
		}

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
		Point symPoints[] = newPointArray(6);
		Point dummy = new Point(0, 0);
		interpPoint2(getLead1(), getLead2(), triPoints[0], triPoints[1], 0, hs);
		triPoints[2] = interpPoint(getPoint1(), getPoint2(), .5 + (ww - 5) / getDn());

		interpPoint2(getLead1(), getLead2(), symPoints[5], symPoints[4], 0.2, hs / 4);// 0
																			// 5
																			// 1
		interpPoint2(getLead1(), getLead2(), symPoints[1], symPoints[2], 0.35, hs / 4);// 4
																				// 2
																				// 3
		interpPoint2(getLead1(), getLead2(), symPoints[0], dummy, 0.1, hs / 4);
		interpPoint2(getLead1(), getLead2(), dummy, symPoints[3], 0.45, hs / 4);

		gatePoly = createPolygon(triPoints);
		symbolPoly = createPolygon(symPoints);
		setBbox(getPoint1(), getPoint2(), hs);
	}

	public void stamp() {
		sim.stampVoltageSource(0, getNodes()[1], getVoltSource());
	}
}
