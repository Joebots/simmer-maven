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

abstract class GateElm extends AbstractCircuitElement {
	final int FLAG_SMALL = 1;
	Polygon gatePoly;
	int gsize, gwidth, gwidth2, gheight, hs2;

	Point inPosts[], inGates[];

	int inputCount = 2;

	boolean lastOutput;

	Point pcircle, linePoints[];

	int ww;

	public GateElm(int xx, int yy) {
		super(xx, yy);
		setNoDiagonal(true);
		inputCount = 2;

		if( sim.getMainMenuBar() != null )
			setSize(sim.getMainMenuBar().getOptionsMenuBar().getSmallGridCheckItem() != null && sim.getMainMenuBar().getOptionsMenuBar().getSmallGridCheckItem().getState() ? 1 : 2);
	}

	public GateElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		inputCount = new Integer(st.nextToken()).intValue();
		lastOutput = new Double(st.nextToken()).doubleValue() > 2.5;
		setNoDiagonal(true);
		setSize((f & FLAG_SMALL) != 0 ? 1 : 2);
	}
	abstract boolean calcFunction();

	public void doStep() {
//		int i;
		boolean f = calcFunction();
		if (isInverting())
			f = !f;
		lastOutput = f;
		double res = f ? 5 : 0;
		sim.updateVoltageSource(0, getNodes()[inputCount], getVoltSource(), res);
	}

	public void draw(Graphics g) {
		int i;
		for (i = 0; i != inputCount; i++) {
			setVoltageColor(g, getVolts()[i]);
			GraphicsUtil.drawThickLine(g, inPosts[i], inGates[i]);
		}
		setVoltageColor(g, getVolts()[inputCount]);
		GraphicsUtil.drawThickLine(g, getLead2(), getPoint2());
		g.setColor(needsHighlight() ? selectColor : lightGrayColor);
		GraphicsUtil.drawThickPolygon(g, gatePoly);

		if (linePoints != null)
			for (i = 0; i != linePoints.length - 1; i++)
				GraphicsUtil.drawThickLine(g, linePoints[i], linePoints[i + 1]);
		if (isInverting())
			GraphicsUtil.drawThickCircle(g, pcircle.getX(), pcircle.getY(), 3);

		setCurcount(updateDotCount(getCurrent(), getCurcount()));
		drawDots(g, getLead2(), getPoint2(), getCurcount());
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + inputCount + " " + getVolts()[inputCount];
	}
	// there is no current path through the gate inputs, but there
	// is an indirect path through the output to ground.
	public boolean getConnection(int n1, int n2) {
		return false;
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("# of Inputs", inputCount, 1, 8)
					.setDimensionless();
		return null;
	}

	abstract String getGateName();

	public void getInfo(String arr[]) {
		arr[0] = getGateName();
		arr[1] = "Vout = " + getVoltageText(getVolts()[inputCount]);
		arr[2] = "Iout = " + getCurrentText(getCurrent());
	}

	boolean getInput(int x) {
		return getVolts()[x] > 2.5;
	}

	public Point getPost(int n) {
		if (n == inputCount)
			return getPoint2();
		return inPosts[n];
	}

	public int getPostCount() {
		return inputCount + 1;
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	public boolean hasGroundConnection(int n1) {
		return (n1 == inputCount);
	}

	boolean isInverting() {
		return false;
	}

	public void setEditValue(int n, EditInfo ei) {
		inputCount = (int) ei.value;
		setPoints();
	}

	public void setPoints() {
		super.setPoints();
		if (getDn() > 150 && this == sim.getDragElm())
			setSize(2);
		int hs = gheight;
		int i;
		ww = gwidth2; // was 24
		if (ww > getDn() / 2)
			ww = (int) (getDn() / 2);
		if (isInverting() && ww + 8 > getDn() / 2)
			ww = (int) (getDn() / 2 - 8);
		calcLeads(ww * 2);
		inPosts = new Point[inputCount];
		inGates = new Point[inputCount];
		allocNodes();
		int i0 = -inputCount / 2;
		for (i = 0; i != inputCount; i++, i0++) {
			if (i0 == 0 && (inputCount & 1) == 0)
				i0++;
			inPosts[i] = interpPoint(getPoint1(), getPoint2(), 0, hs * i0);
			inGates[i] = interpPoint(getLead1(), getLead2(), 0, hs * i0);
			getVolts()[i] = (lastOutput ^ isInverting()) ? 5 : 0;
		}
		hs2 = gwidth * (inputCount / 2 + 1);
		setBbox(getPoint1(), getPoint2(), hs2);
	}

	void setSize(int s) {
		gsize = s;
		gwidth = 7 * s;
		gwidth2 = 14 * s;
		gheight = 8 * s;
		setFlags((s == 1) ? FLAG_SMALL : 0);
	}

	public void stamp() {
		sim.stampVoltageSource(0, getNodes()[inputCount], getVoltSource());
	}
}
