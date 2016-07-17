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

import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.gui.util.Point;
import com.jobotics.simmer.client.gui.util.Polygon;
import com.jobotics.simmer.client.util.GraphicsUtil;
import com.jobotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

// contributed by Edward Calver

public class SchmittElm extends InvertingSchmittElm {
	Polygon gatePoly;

	Polygon symbolPoly;

	public SchmittElm(int xx, int yy) {
		super(xx, yy);
	}

	public SchmittElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	public void doStep() {
		double v0 = getVolts()[1];
		double out;
		if (state) {// Output is high
			if (getVolts()[0] > upperTrigger)// Input voltage high enough to set
										// output high
			{
				state = false;
				out = 5;
			} else {
				out = 0;
			}
		} else {// Output is low
			if (getVolts()[0] < lowerTrigger)// Input voltage low enough to set
										// output low
			{
				state = true;
				out = 0;
			} else {
				out = 5;
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
		setCurcount(updateDotCount(getCurrent(), getCurcount()));
		drawDots(g, getLead2(), getPoint2(), getCurcount());
	}
	public int getDumpType() {
		return 182;
	}

	public void getInfo(String arr[]) {
		arr[0] = "Schmitt";
	}

	public void setPoints() {
		super.setPoints();
		int hs = 16;
		int ww = 16;
		if (ww > getDn() / 2)
			ww = (int) (getDn() / 2);
		setLead1(interpPoint(getPoint1(), getPoint2(), .5 - ww / getDn()));
		setLead2(interpPoint(getPoint1(), getPoint2(), .5 + (ww - 3) / getDn()));
		Point triPoints[] = newPointArray(3);
		Point symPoints[] = newPointArray(6);
		Point dummy = new Point(0, 0);
		interpPoint2(getLead1(), getLead2(), triPoints[0], triPoints[1], 0, hs);
		triPoints[2] = interpPoint(getPoint1(), getPoint2(), .5 + (ww - 5) / getDn());

		interpPoint2(getLead1(), getLead2(), symPoints[4], symPoints[5], 0.25, hs / 4);// 5
																				// 1
																				// 3
		interpPoint2(getLead1(), getLead2(), symPoints[2], symPoints[1], 0.4, hs / 4);// 0
																			// 4
																			// 2
		interpPoint2(getLead1(), getLead2(), dummy, symPoints[0], 0.1, hs / 4);
		interpPoint2(getLead1(), getLead2(), symPoints[3], dummy, 0.52, hs / 4);

		gatePoly = createPolygon(triPoints);
		symbolPoly = createPolygon(symPoints);
		setBbox(getPoint1(), getPoint2(), hs);
	}

}
