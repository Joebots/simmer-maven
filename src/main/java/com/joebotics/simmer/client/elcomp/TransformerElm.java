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

import com.joebotics.simmer.client.gui.impl.Checkbox;
import com.joebotics.simmer.client.gui.impl.EditInfo;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.Inductor;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;

public class TransformerElm extends AbstractCircuitElement {
	public static final int FLAG_BACK_EULER = 2;
	double a1, a2, a3, a4;
	double current[], curcount[];
	double curSourceValue1, curSourceValue2;
	double inductance, ratio, couplingCoef;

	Point ptEnds[], ptCoil[], ptCore[];

	int width;

	public TransformerElm(int xx, int yy) {
		super(xx, yy);
		inductance = 4;
		ratio = 1;
		width = 32;
		setNoDiagonal(true);
		couplingCoef = .999;
		current = new double[2];
		curcount = new double[2];
	}

	public TransformerElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		width = max(32, abs(yb - ya));
		inductance = new Double(st.nextToken()).doubleValue();
		ratio = new Double(st.nextToken()).doubleValue();
		current = new double[2];
		curcount = new double[2];
		current[0] = new Double(st.nextToken()).doubleValue();
		current[1] = new Double(st.nextToken()).doubleValue();
		couplingCoef = .999;
		try {
			couplingCoef = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
		}
		setNoDiagonal(true);
	}

	public void calculateCurrent() {
		double voltdiff1 = getVolts()[0] - getVolts()[2];
		double voltdiff2 = getVolts()[1] - getVolts()[3];
		current[0] = voltdiff1 * a1 + voltdiff2 * a2 + curSourceValue1;
		current[1] = voltdiff1 * a3 + voltdiff2 * a4 + curSourceValue2;
	}

	public void doStep() {
		sim.stampCurrentSource(getNodes()[0], getNodes()[2], curSourceValue1);
		sim.stampCurrentSource(getNodes()[1], getNodes()[3], curSourceValue2);
	}

	public void drag(int xx, int yy) {
		xx = sim.getSimmerController().snapGrid(xx);
		yy = sim.getSimmerController().snapGrid(yy);
		width = max(32, abs(yy - getY1()));
		if (xx == getX1())
			yy = getY1();
		setX2(xx);
		setY2(yy);
		setPoints();
	}

	public void draw(Graphics g) {
		int i;
		for (i = 0; i != 4; i++) {
			setVoltageColor(g, getVolts()[i]);
			GraphicsUtil.drawThickLine(g, ptEnds[i], ptCoil[i]);
		}
		for (i = 0; i != 2; i++) {
			setPowerColor(g, current[i] * (getVolts()[i] - getVolts()[i + 2]));
			drawCoil(g, getDsign() * (i == 1 ? -6 : 6), ptCoil[i], ptCoil[i + 2],
					getVolts()[i], getVolts()[i + 2]);
		}
		g.setColor(needsHighlight() ? selectColor : lightGrayColor);
		for (i = 0; i != 2; i++) {
			GraphicsUtil.drawThickLine(g, ptCore[i], ptCore[i + 2]);
			curcount[i] = updateDotCount(current[i], curcount[i]);
		}
		for (i = 0; i != 2; i++) {
			drawDots(g, ptEnds[i], ptCoil[i], curcount[i]);
			drawDots(g, ptCoil[i], ptCoil[i + 2], curcount[i]);
			drawDots(g, ptEnds[i + 2], ptCoil[i + 2], -curcount[i]);
		}

		drawPosts(g);
		setBbox(ptEnds[0], ptEnds[3], 0);
	}

	public String dump() {
		return super.dump() + " " + inductance + " " + ratio + " " + current[0]
				+ " " + current[1] + " " + couplingCoef;
	}

	public boolean getConnection(int n1, int n2) {
		if (comparePair(n1, n2, 0, 2))
			return true;
		if (comparePair(n1, n2, 1, 3))
			return true;
		return false;
	}

	public int getDumpType() {
		return 'T';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Primary Inductance (H)", inductance, .01, 5);
		if (n == 1)
			return new EditInfo("Ratio", ratio, 1, 10).setDimensionless();
		if (n == 2)
			return new EditInfo("Coupling Coefficient", couplingCoef, 0, 1)
					.setDimensionless();
		if (n == 3) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Trapezoidal Approximation",
					isTrapezoidal());
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "transformer";
		arr[1] = "L = " + getUnitText(inductance, "H");
		arr[2] = "Ratio = 1:" + ratio;
		arr[3] = "Vd1 = " + getVoltageText(getVolts()[0] - getVolts()[2]);
		arr[4] = "Vd2 = " + getVoltageText(getVolts()[1] - getVolts()[3]);
		arr[5] = "I1 = " + getCurrentText(current[0]);
		arr[6] = "I2 = " + getCurrentText(current[1]);
	}

	public Point getPost(int n) {
		return ptEnds[n];
	}

	public int getPostCount() {
		return 4;
	}

	boolean isTrapezoidal() {
		return (getFlags() & FLAG_BACK_EULER) == 0;
	}

	public void reset() {
		current[0] = current[1] = getVolts()[0] = getVolts()[1] = getVolts()[2] = getVolts()[3] = curcount[0] = curcount[1] = 0;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			inductance = ei.value;
		if (n == 1)
			ratio = ei.value;
		if (n == 2 && ei.value > 0 && ei.value < 1)
			couplingCoef = ei.value;
		if (n == 3) {
			if (ei.checkbox.getState())
				setFlags(getFlags() & ~Inductor.FLAG_BACK_EULER);
			else
				setFlags(getFlags() | Inductor.FLAG_BACK_EULER);
		}
	}

	public void setPoints() {
		super.setPoints();
		getPoint2().setY(getPoint1().getY());
		ptEnds = newPointArray(4);
		ptCoil = newPointArray(4);
		ptCore = newPointArray(4);
		ptEnds[0] = getPoint1();
		ptEnds[1] = getPoint2();
		interpPoint(getPoint1(), getPoint2(), ptEnds[2], 0, -getDsign() * width);
		interpPoint(getPoint1(), getPoint2(), ptEnds[3], 1, -getDsign() * width);
		double ce = .5 - 12 / getDn();
		double cd = .5 - 2 / getDn();
		int i;
		for (i = 0; i != 4; i += 2) {
			interpPoint(ptEnds[i], ptEnds[i + 1], ptCoil[i], ce);
			interpPoint(ptEnds[i], ptEnds[i + 1], ptCoil[i + 1], 1 - ce);
			interpPoint(ptEnds[i], ptEnds[i + 1], ptCore[i], cd);
			interpPoint(ptEnds[i], ptEnds[i + 1], ptCore[i + 1], 1 - cd);
		}
	}

	public void stamp() {
		// equations for transformer:
		// v1 = L1 di1/dt + M di2/dt
		// v2 = M di1/dt + L2 di2/dt
		// we invert that to get:
		// di1/dt = a1 v1 + a2 v2
		// di2/dt = a3 v1 + a4 v2
		// integrate di1/dt using trapezoidal approx and we get:
		// i1(t2) = i1(t1) + dt/2 (i1(t1) + i1(t2))
		// = i1(t1) + a1 dt/2 v1(t1) + a2 dt/2 v2(t1) +
		// a1 dt/2 v1(t2) + a2 dt/2 v2(t2)
		// the norton equivalent of this for i1 is:
		// a. current source, I = i1(t1) + a1 dt/2 v1(t1) + a2 dt/2 v2(t1)
		// b. resistor, G = a1 dt/2
		// c. current source controlled by voltage v2, G = a2 dt/2
		// and for i2:
		// a. current source, I = i2(t1) + a3 dt/2 v1(t1) + a4 dt/2 v2(t1)
		// b. resistor, G = a3 dt/2
		// c. current source controlled by voltage v2, G = a4 dt/2
		//
		// For backward euler,
		//
		// i1(t2) = i1(t1) + a1 dt v1(t2) + a2 dt v2(t2)
		//
		// So the current source value is just i1(t1) and we use
		// dt instead of dt/2 for the resistor and VCCS.
		//
		// first winding goes from node 0 to 2, second is from 1 to 3
		double l1 = inductance;
		double l2 = inductance * ratio * ratio;
		double m = couplingCoef * Math.sqrt(l1 * l2);
		// build inverted matrix
		double deti = 1 / (l1 * l2 - m * m);
		double ts = isTrapezoidal() ? sim.getTimeStep() / 2 : sim.getTimeStep();
		a1 = l2 * deti * ts; // we multiply dt/2 into a1..a4 here
		a2 = -m * deti * ts;
		a3 = -m * deti * ts;
		a4 = l1 * deti * ts;
		sim.stampConductance(getNodes()[0], getNodes()[2], a1);
		sim.stampVCCurrentSource(getNodes()[0], getNodes()[2], getNodes()[1], getNodes()[3], a2);
		sim.stampVCCurrentSource(getNodes()[1], getNodes()[3], getNodes()[0], getNodes()[2], a3);
		sim.stampConductance(getNodes()[1], getNodes()[3], a4);
		sim.stampRightSide(getNodes()[0]);
		sim.stampRightSide(getNodes()[1]);
		sim.stampRightSide(getNodes()[2]);
		sim.stampRightSide(getNodes()[3]);
	}

	public void startIteration() {
		double voltdiff1 = getVolts()[0] - getVolts()[2];
		double voltdiff2 = getVolts()[1] - getVolts()[3];
		if (isTrapezoidal()) {
			curSourceValue1 = voltdiff1 * a1 + voltdiff2 * a2 + current[0];
			curSourceValue2 = voltdiff1 * a3 + voltdiff2 * a4 + current[1];
		} else {
			curSourceValue1 = current[0];
			curSourceValue2 = current[1];
		}
	}
}
