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

public class TriStateElm extends AbstractCircuitElement {
	Polygon gatePoly;

	boolean open;

	Point ps, point3, point4, lead3;

	double resistance, r_on, r_off;

	public TriStateElm(int xx, int yy) {
		super(xx, yy);
		r_on = 0.1;
		r_off = 1e10;
	}

	public TriStateElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		r_on = 0.1;
		r_off = 1e10;
		try {
			r_on = new Double(st.nextToken()).doubleValue();
			r_off = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
		}

	}

	public void calculateCurrent() {
		setCurrent((getVolts()[0] - getVolts()[1]) / resistance);
	}

	public void doStep() {
		open = (getVolts()[2] < 2.5);
		resistance = (open) ? r_off : r_on;
		sim.stampResistor(getNodes()[3], getNodes()[1], resistance);
		sim.updateVoltageSource(0, getNodes()[3], getVoltSource(), getVolts()[0] > 2.5 ? 5 : 0);
	}

	public void drag(int xx, int yy) {
		xx = sim.snapGrid(xx);
		yy = sim.snapGrid(yy);
		if (abs(getX() - xx) < abs(getY() - yy))
			xx = getX();
		else
			yy = getY();
		int q1 = abs(getX() - xx) + abs(getY() - yy);
		int q2 = (q1 / 2) % sim.getGridSize();
		if (q2 != 0)
			return;
		setX2(xx);
		setY2(yy);
		setPoints();
	}

	public void draw(Graphics g) {
		int hs = 16;
		setBbox(getPoint1(), getPoint2(), hs);

		draw2Leads(g);

		g.setColor(lightGrayColor);
		GraphicsUtil.drawThickPolygon(g, gatePoly);
		setVoltageColor(g, getVolts()[2]);
		GraphicsUtil.drawThickLine(g, point3, lead3);

		drawPosts(g);
	}

	public void drawPosts(Graphics g) {
		int i;
		for (i = 0; i != 3; i++) {
			Point p = getPost(i);
			drawPost(g, p.x, p.y, getNodes()[i]);
		}
	}

	public String dump() {
		return super.dump() + " " + r_on + " " + r_off;
	}

	public boolean getConnection(int n1, int n2) {
		if ((n1 == 1 && n2 == 3) || (n1 == 3 && n2 == 1))
			return true;
		return false;
	}

	public int getDumpType() {
		return 180;
	}

	public EditInfo getEditInfo(int n) {

		if (n == 0)
			return new EditInfo("On Resistance (ohms)", r_on, 0, 0);
		if (n == 1)
			return new EditInfo("Off Resistance (ohms)", r_off, 0, 0);
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "tri-state buffer";
		arr[1] = open ? "open" : "closed";
		arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
		arr[3] = "I = " + getCurrentDText(getCurrent());
		arr[4] = "Vc = " + getVoltageText(getVolts()[2]);
	}

	public Point getPost(int n) {
		if (point4 == null)
			System.out.print("Hello\n");
		return (n == 0) ? getPoint1() : (n == 1) ? getPoint2() : (n == 2) ? point3
				: point4;
	}

	public int getPostCount() {
		return 4;
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	// we need this to be able to change the matrix for each step
	public boolean nonLinear() {
		return true;
	}

	// we have to just assume current will flow either way, even though that
	// might cause singular matrix errors

	// 0---3----------1
	// /
	// 2

	public void setEditValue(int n, EditInfo ei) {

		if (n == 0 && ei.value > 0)
			r_on = ei.value;
		if (n == 1 && ei.value > 0)
			r_off = ei.value;
	}

	public void setPoints() {
		super.setPoints();
		calcLeads(32);
		ps = new Point();
		int hs = 16;

		int ww = 16;
		if (ww > getDn() / 2)
			ww = (int) (getDn() / 2);
		Point triPoints[] = newPointArray(3);
		interpPoint2(getLead1(), getLead2(), triPoints[0], triPoints[1], 0, hs + 2);
		triPoints[2] = interpPoint(getPoint1(), getPoint2(), .5 + (ww - 2) / getDn());
		gatePoly = createPolygon(triPoints);

		point3 = interpPoint(getPoint1(), getPoint2(), .5, -hs);
		point4 = interpPoint(getPoint1(), getPoint2(), .5, 0);
		lead3 = interpPoint(getPoint1(), getPoint2(), .5, -hs / 2);
	}

	public void stamp() {
		sim.stampVoltageSource(0, getNodes()[3], getVoltSource());
		sim.stampNonLinear(getNodes()[3]);
		sim.stampNonLinear(getNodes()[1]);
	}
}
