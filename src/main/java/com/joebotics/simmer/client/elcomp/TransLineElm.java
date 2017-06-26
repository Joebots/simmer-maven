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
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;

public class TransLineElm extends AbstractCircuitElement {
	double current1, current2, curCount1, curCount2;
	double delay, imped;
	int lenSteps, ptr, width;

	Point posts[], inner[];

	double voltageL[], voltageR[];

	int voltSource1, voltSource2;

	public TransLineElm(int xx, int yy) {
		super(xx, yy);
		delay = 1000 * sim.getTimeStep();
		imped = 75;
		setNoDiagonal(true);
		reset();
	}

	public TransLineElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		delay = new Double(st.nextToken()).doubleValue();
		imped = new Double(st.nextToken()).doubleValue();
		width = new Integer(st.nextToken()).intValue();
		// next slot is for resistance (losses), which is not implemented
		st.nextToken();
		setNoDiagonal(true);
		reset();
	}

	public void doStep() {
		if (voltageL == null) {
			sim.stop("Transmission line delay too large!", this);
			return;
		}
		sim.updateVoltageSource(getNodes()[4], getNodes()[0], voltSource1, -voltageR[ptr]);
		sim.updateVoltageSource(getNodes()[5], getNodes()[1], voltSource2, -voltageL[ptr]);
		if (Math.abs(getVolts()[0]) > 1e-5 || Math.abs(getVolts()[1]) > 1e-5) {
			sim.stop("Need to ground transmission line!", this);
			return;
		}
	}

	public void drag(int xx, int yy) {
		xx = sim.getSimmerController().snapGrid(xx);
		yy = sim.getSimmerController().snapGrid(yy);
		int w1 = max(sim.getGridSize(), abs(yy - getY1()));
		int w2 = max(sim.getGridSize(), abs(xx - getX1()));
		if (w1 > w2) {
			xx = getX1();
			width = w2;
		} else {
			yy = getY1();
			width = w1;
		}
		setX2(xx);
		setY2(yy);
		setPoints();
	}

	public void draw(Graphics g) {
		setBbox(posts[0], posts[3], 0);
		int segments = (int) (getDn() / 2);
		int ix0 = ptr - 1 + lenSteps;
		double segf = 1. / segments;
		int i;
		g.setColor(Color.darkGray);
		g.fillRect(inner[2].getX(), inner[2].getY(), inner[1].getX() - inner[2].getX() + 2,
				inner[1].getY() - inner[2].getY() + 2);
		for (i = 0; i != 4; i++) {
			setVoltageColor(g, getVolts()[i]);
			GraphicsUtil.drawThickLine(g, posts[i], inner[i]);
		}
		if (voltageL != null) {
			for (i = 0; i != segments; i++) {
				int ix1 = (ix0 - lenSteps * i / segments) % lenSteps;
				int ix2 = (ix0 - lenSteps * (segments - 1 - i) / segments)
						% lenSteps;
				double v = (voltageL[ix1] + voltageR[ix2]) / 2;
				setVoltageColor(g, v);
				interpPoint(inner[0], inner[1], ps1, i * segf);
				interpPoint(inner[2], inner[3], ps2, i * segf);
				g.drawLine(ps1.getX(), ps1.getY(), ps2.getX(), ps2.getY());
				interpPoint(inner[2], inner[3], ps1, (i + 1) * segf);
				GraphicsUtil.drawThickLine(g, ps1, ps2);
			}
		}
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, inner[0], inner[1]);
		drawPosts(g);

		curCount1 = updateDotCount(-current1, curCount1);
		curCount2 = updateDotCount(current2, curCount2);
		if (sim.getDragElm() != this) {
			drawDots(g, posts[0], inner[0], curCount1);
			drawDots(g, posts[2], inner[2], -curCount1);
			drawDots(g, posts[1], inner[1], -curCount2);
			drawDots(g, posts[3], inner[3], curCount2);
		}
	}

	public String dump() {
		return super.dump() + " " + delay + " " + imped + " " + width + " "
				+ 0.;
	}

	public boolean getConnection(int n1, int n2) {
		return false;
		/*
		 * if (comparePair(n1, n2, 0, 1)) return true; if (comparePair(n1, n2,
		 * 2, 3)) return true; return false;
		 */
	}

	public int getDumpType() {
		return 171;
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Delay (s)", delay, 0, 0);
		if (n == 1)
			return new EditInfo("Impedance (ohms)", imped, 0, 0);
		return null;
	}
	public void getInfo(String arr[]) {
		arr[0] = "transmission line";
		arr[1] = getUnitText(imped, Simmer.ohmString);
		arr[2] = "length = " + getUnitText(2.9979e8 * delay, "m");
		arr[3] = "delay = " + getUnitText(delay, "s");
	}

	public int getInternalNodeCount() {
		return 2;
	}

	public int getPostCount() {
		return 4;
	}

	// double getVoltageDiff() { return volts[0]; }
	public int getVoltageSourceCount() {
		return 2;
	}

	public void reset() {
		if (sim.getTimeStep() == 0)
			return;
		lenSteps = (int) (delay / sim.getTimeStep());
		System.out.println(lenSteps + " steps");
		if (lenSteps > 100000)
			voltageL = voltageR = null;
		else {
			voltageL = new double[lenSteps];
			voltageR = new double[lenSteps];
		}
		ptr = 0;
		super.reset();
	}

	public void setCurrent(int v, double c) {
		if (v == voltSource1)
			current1 = c;
		else
			current2 = c;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			delay = ei.value;
			reset();
		}
		if (n == 1) {
			imped = ei.value;
			reset();
		}
	}

	public void setPoints() {
		super.setPoints();
		int ds = (getDy() == 0) ? sign(getDx()) : -sign(getDy());
		Point p3 = interpPoint(getPoint1(), getPoint2(), 0, -width * ds);
		Point p4 = interpPoint(getPoint1(), getPoint2(), 1, -width * ds);
		int sep = sim.getGridSize() / 2;
		Point p5 = interpPoint(getPoint1(), getPoint2(), 0, -(width / 2 - sep) * ds);
		Point p6 = interpPoint(getPoint1(), getPoint2(), 1, -(width / 2 - sep) * ds);
		Point p7 = interpPoint(getPoint1(), getPoint2(), 0, -(width / 2 + sep) * ds);
		Point p8 = interpPoint(getPoint1(), getPoint2(), 1, -(width / 2 + sep) * ds);

		// we number the posts like this because we want the lower-numbered
		// points to be on the bottom, so that if some of them are unconnected
		// (which is often true) then the bottom ones will get automatically
		// attached to ground.
		posts = new Point[] { p3, p4, getPoint1(), getPoint2() };
		inner = new Point[] { p7, p8, p5, p6 };
		for (int i = 0; i < posts.length; i++) {
			getPins()[i].setPost(posts[i]);
		}
	}

	public void setVoltageSource(int n, int v) {
		if (n == 0)
			voltSource1 = v;
		else
			voltSource2 = v;
	}

	public void stamp() {
		sim.stampVoltageSource(getNodes()[4], getNodes()[0], voltSource1);
		sim.stampVoltageSource(getNodes()[5], getNodes()[1], voltSource2);
		sim.stampResistor(getNodes()[2], getNodes()[4], imped);
		sim.stampResistor(getNodes()[3], getNodes()[5], imped);
	}

	public void startIteration() {
		// calculate voltages, currents sent over wire
		if (voltageL == null) {
			sim.stop("Transmission line delay too large!", this);
			return;
		}
		voltageL[ptr] = getVolts()[2] - getVolts()[0] + getVolts()[2] - getVolts()[4];
		voltageR[ptr] = getVolts()[3] - getVolts()[1] + getVolts()[3] - getVolts()[5];
		// System.out.println(volts[2] + " " + volts[0] + " " +
		// (volts[2]-volts[0]) + " " + (imped*current1) + " " + voltageL[ptr]);
		/*
		 * System.out.println("sending fwd  " + currentL[ptr] + " " + current1);
		 * System.out.println("sending back " + currentR[ptr] + " " + current2);
		 */
		// System.out.println("sending back " + voltageR[ptr]);
		ptr = (ptr + 1) % lenSteps;
	}
}
