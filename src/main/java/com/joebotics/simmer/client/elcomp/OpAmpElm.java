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

import com.joebotics.simmer.client.gui.impl.EditInfo;
import com.joebotics.simmer.client.gui.util.Font;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Polygon;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;

public class OpAmpElm extends AbstractCircuitElement {
	final int FLAG_LOWGAIN = 4;
	final int FLAG_SMALL = 2;
	final int FLAG_SWAP = 1;
	Point in1p[], in2p[], textp[];
	double lastvd;
	double maxOut, minOut, gain, gbw;

	int opsize, opheight, opwidth, opaddtext;

	Font plusFont;

	boolean reset;

	Polygon triangle;

	public OpAmpElm(int xx, int yy) {
		super(xx, yy);
		setNoDiagonal(true);
		maxOut = 15;
		minOut = -15;
		gbw = 1e6;
		setSize(sim.getSmallGridCheckItem().getState() ? 1 : 2);
		setGain();
	}

	public OpAmpElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		maxOut = 15;
		minOut = -15;
		// GBW has no effect in this version of the simulator, but we
		// retain it to keep the file format the same
		gbw = 1e6;
		try {
			maxOut = new Double(st.nextToken()).doubleValue();
			minOut = new Double(st.nextToken()).doubleValue();
			gbw = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
		}
		setNoDiagonal(true);
		setSize((f & FLAG_SMALL) != 0 ? 1 : 2);
		setGain();
	}

	public void doStep() {
		double vd = getVolts()[1] - getVolts()[0];
		if (Math.abs(lastvd - vd) > .1)
			sim.setConverged(false);
		else if (getVolts()[2] > maxOut + .1 || getVolts()[2] < minOut - .1)
			sim.setConverged(false);
		double x = 0;
		int vn = sim.getNodeList().size() + getVoltSource();
		double dx = 0;
		if (vd >= maxOut / gain && (lastvd >= 0 || sim.getrand(4) == 1)) {
			dx = 1e-4;
			x = maxOut - dx * maxOut / gain;
		} else if (vd <= minOut / gain && (lastvd <= 0 || sim.getrand(4) == 1)) {
			dx = 1e-4;
			x = minOut - dx * minOut / gain;
		} else
			dx = gain;
		// System.out.println("opamp " + vd + " " + volts[2] + " " + dx + " " +
		// x + " " + lastvd + " " + simmer.converged);

		// newton-raphson
		sim.stampMatrix(vn, getNodes()[0], dx);
		sim.stampMatrix(vn, getNodes()[1], -dx);
		sim.stampMatrix(vn, getNodes()[2], 1);
		sim.stampRightSide(vn, x);

		lastvd = vd;
		/*
		 * if (simmer.converged) System.out.println((volts[1]-volts[0]) + " " +
		 * volts[2] + " " + initvd);
		 */
	}

	public void draw(Graphics g) {
		setBbox(getPoint1(), getPoint2(), opheight * 2);
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, in1p[0], in1p[1]);
		setVoltageColor(g, getVolts()[1]);
		GraphicsUtil.drawThickLine(g, in2p[0], in2p[1]);
		g.setColor(needsHighlight() ? selectColor : lightGrayColor);
		setPowerColor(g, true);
		GraphicsUtil.drawThickPolygon(g, triangle);
		g.setFont(plusFont);
		drawCenteredText(g, "-", textp[0].getX(), textp[0].getY() - 2, true);
		drawCenteredText(g, "+", textp[1].getX(), textp[1].getY(), true);
		setVoltageColor(g, getVolts()[2]);
		GraphicsUtil.drawThickLine(g, getLead2(), getPoint2());
		setCurcount(updateDotCount(getCurrent(), getCurcount()));
		drawDots(g, getPoint2(), getLead2(), getCurcount());
		drawPosts(g);
	}
	public String dump() {
		return super.dump() + " " + maxOut + " " + minOut + " " + gbw;
	}
	// there is no current path through the op-amp inputs, but there
	// is an indirect path through the output to ground.
	public boolean getConnection(int n1, int n2) {
		return false;
	}

	public int getDumpType() {
		return 'a';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Max Output (V)", maxOut, 1, 20);
		if (n == 1)
			return new EditInfo("Min Output (V)", minOut, -20, 0);
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "op-amp";
		arr[1] = "V+ = " + getVoltageText(getVolts()[1]);
		arr[2] = "V- = " + getVoltageText(getVolts()[0]);
		// sometimes the voltage goes slightly outside range, to make
		// convergence easier. so we hide that here.
		double vo = Math.max(Math.min(getVolts()[2], maxOut), minOut);
		arr[3] = "Vout = " + getVoltageText(vo);
		arr[4] = "Iout = " + getCurrentText(getCurrent());
		arr[5] = "range = " + getVoltageText(minOut) + " to "
				+ getVoltageText(maxOut);
	}

	public Point getPost(int n) {
		return (n == 0) ? in1p[0] : (n == 1) ? in2p[0] : getPoint2();
	}

	public int getPostCount() {
		return 3;
	}

	public double getPower() {
		return getVolts()[2] * getCurrent();
	}

	public int getShortcut() {
		return 'a';
	}

	public double getVoltageDiff() {
		return getVolts()[2] - getVolts()[1];
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	public boolean hasGroundConnection(int n1) {
		return (n1 == 2);
	}

	public boolean nonLinear() {
		return true;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			maxOut = ei.value;
		if (n == 1)
			minOut = ei.value;
	}

	public void setGain() {
		// gain of 100000 breaks e-amp-dfdx.txt
		// gain was 1000, but it broke amp-schmitt.txt
		gain = ((getFlags() & FLAG_LOWGAIN) != 0) ? 1000 : 100000;

	}

	public void setPoints() {
		super.setPoints();
		if (getDn() > 150 && this == sim.getDragElm())
			setSize(2);
		int ww = opwidth;
		if (ww > getDn() / 2)
			ww = (int) (getDn() / 2);
		calcLeads(ww * 2);
		int hs = opheight * getDsign();
		if ((getFlags() & FLAG_SWAP) != 0)
			hs = -hs;
		in1p = newPointArray(2);
		in2p = newPointArray(2);
		textp = newPointArray(2);
		interpPoint2(getPoint1(), getPoint2(), in1p[0], in2p[0], 0, hs);
		interpPoint2(getLead1(), getLead2(), in1p[1], in2p[1], 0, hs);
		interpPoint2(getLead1(), getLead2(), textp[0], textp[1], .2, hs);
		Point tris[] = newPointArray(2);
		interpPoint2(getLead1(), getLead2(), tris[0], tris[1], 0, hs * 2);
		triangle = createPolygon(tris[0], tris[1], getLead2());
		plusFont = new Font("SansSerif", 0, opsize == 2 ? 14 : 10);
	}

	void setSize(int s) {
		opsize = s;
		opheight = 8 * s;
		opwidth = 13 * s;
		setFlags((getFlags() & ~FLAG_SMALL) | ((s == 1) ? FLAG_SMALL : 0));
	}

	public void stamp() {
		int vn = sim.getNodeList().size() + getVoltSource();
		sim.stampNonLinear(vn);
		sim.stampMatrix(getNodes()[2], vn, 1);
	}
}
