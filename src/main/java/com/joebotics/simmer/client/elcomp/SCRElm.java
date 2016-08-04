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
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Polygon;
import com.joebotics.simmer.client.util.Diode;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;

// Silicon-Controlled Rectifier
// 3 nodes, 1 internal node
// 0 = anode, 1 = cathode, 2 = gate
// 0, 3 = variable resistor
// 3, 2 = diode
// 2, 1 = 50 ohm resistor

public class SCRElm extends AbstractCircuitElement {
	final int anode = 0;
	double aresistance;
	Point cathode[], gate[];
	final int cnode = 1;
	double cresistance, triggerI, holdingI;

	Diode diode;

	final int gnode = 2;

	final int hs = 8;

	double ia, ic, ig, curcount_a, curcount_c, curcount_g;

	final int inode = 3;

	double lastvac, lastvag;

	Polygon poly;

	public SCRElm(int xx, int yy) {
		super(xx, yy);
		setDefaults();
		setup();
	}

	public SCRElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		setDefaults();
		try {
			lastvac = new Double(st.nextToken()).doubleValue();
			lastvag = new Double(st.nextToken()).doubleValue();
			getVolts()[anode] = 0;
			getVolts()[cnode] = -lastvac;
			getVolts()[gnode] = -lastvag;
			triggerI = new Double(st.nextToken()).doubleValue();
			holdingI = new Double(st.nextToken()).doubleValue();
			cresistance = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
		}
		setup();
	}
	public void calculateCurrent() {
		ic = (getVolts()[cnode] - getVolts()[gnode]) / cresistance;
		ia = (getVolts()[anode] - getVolts()[inode]) / aresistance;
		ig = -ic - ia;
	}
	public void doStep() {
		double vac = getVolts()[anode] - getVolts()[cnode]; // typically negative
		double vag = getVolts()[anode] - getVolts()[gnode]; // typically positive
		if (Math.abs(vac - lastvac) > .01 || Math.abs(vag - lastvag) > .01)
			sim.setConverged(false);
		lastvac = vac;
		lastvag = vag;
		diode.doStep(getVolts()[inode] - getVolts()[gnode]);
		double icmult = 1 / triggerI;
		double iamult = 1 / holdingI - icmult;
		// System.out.println(icmult + " " + iamult);
		aresistance = (-icmult * ic + ia * iamult > 1) ? .0105 : 10e5;
		// System.out.println(vac + " " + vag + " " + simmer.converged + " " + ic +
		// " " + ia + " " + aresistance + " " + volts[inode] + " " +
		// volts[gnode] + " " + volts[anode]);
		sim.stampResistor(getNodes()[anode], getNodes()[inode], aresistance);
	}

	public void draw(Graphics g) {
		setBbox(getPoint1(), getPoint2(), hs);
		adjustBbox(gate[0], gate[1]);

		double v1 = getVolts()[anode];
		double v2 = getVolts()[cnode];

		draw2Leads(g);

		// draw arrow thingy
		setPowerColor(g, true);
		setVoltageColor(g, v1);
		g.fillPolygon(poly);

		// draw thing arrow is pointing to
		setVoltageColor(g, v2);
		GraphicsUtil.drawThickLine(g, cathode[0], cathode[1]);

		GraphicsUtil.drawThickLine(g, getLead2(), gate[0]);
		GraphicsUtil.drawThickLine(g, gate[0], gate[1]);

		curcount_a = updateDotCount(ia, curcount_a);
		curcount_c = updateDotCount(ic, curcount_c);
		curcount_g = updateDotCount(ig, curcount_g);
		if (sim.getDragElm() != this) {
			drawDots(g, getPoint1(), getLead2(), curcount_a);
			drawDots(g, getPoint2(), getLead2(), curcount_c);
			drawDots(g, gate[1], gate[0], curcount_g);
			drawDots(g, gate[0], getLead2(), curcount_g + distance(gate[1], gate[0]));
		}
		drawPosts(g);
	}
	public String dump() {
		return super.dump() + " " + (getVolts()[anode] - getVolts()[cnode]) + " "
				+ (getVolts()[anode] - getVolts()[gnode]) + " " + triggerI + " "
				+ holdingI + " " + cresistance;
	}
	public int getDumpType() {
		return 177;
	}

	public EditInfo getEditInfo(int n) {
		// ohmString doesn't work here on linux
		if (n == 0)
			return new EditInfo("Trigger Current (A)", triggerI, 0, 0);
		if (n == 1)
			return new EditInfo("Holding Current (A)", holdingI, 0, 0);
		if (n == 2)
			return new EditInfo("Gate-Cathode Resistance (ohms)", cresistance,
					0, 0);
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "SCR";
		double vac = getVolts()[anode] - getVolts()[cnode];
		double vag = getVolts()[anode] - getVolts()[gnode];
		double vgc = getVolts()[gnode] - getVolts()[cnode];
		arr[1] = "Ia = " + getCurrentText(ia);
		arr[2] = "Ig = " + getCurrentText(ig);
		arr[3] = "Vac = " + getVoltageText(vac);
		arr[4] = "Vag = " + getVoltageText(vag);
		arr[5] = "Vgc = " + getVoltageText(vgc);
	}

	public int getInternalNodeCount() {
		return 1;
	}

	public Point getPost(int n) {
		return (n == 0) ? getPoint1() : (n == 1) ? getPoint2() : gate[1];
	}

	public int getPostCount() {
		return 3;
	}

	public double getPower() {
		return (getVolts()[anode] - getVolts()[gnode]) * ia
				+ (getVolts()[cnode] - getVolts()[gnode]) * ic;
	}

	public boolean nonLinear() {
		return true;
	}

	public void reset() {
		getVolts()[anode] = getVolts()[cnode] = getVolts()[gnode] = 0;
		diode.reset();
		lastvag = lastvac = curcount_a = curcount_c = curcount_g = 0;
	}

	void setDefaults() {
		cresistance = 50;
		holdingI = .0082;
		triggerI = .01;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0 && ei.value > 0)
			triggerI = ei.value;
		if (n == 1 && ei.value > 0)
			holdingI = ei.value;
		if (n == 2 && ei.value > 0)
			cresistance = ei.value;
	}

	public void setPoints() {
		super.setPoints();
		int dir = 0;
		if (abs(getDx()) > abs(getDy())) {
			dir = -sign(getDx()) * sign(getDy());
			getPoint2().setY(getPoint1().getY());
		} else {
			dir = sign(getDy()) * sign(getDx());
			getPoint2().setX(getPoint1().getX());
		}
		if (dir == 0)
			dir = 1;
		calcLeads(16);
		cathode = newPointArray(2);
		Point pa[] = newPointArray(2);
		interpPoint2(getLead1(), getLead2(), pa[0], pa[1], 0, hs);
		interpPoint2(getLead1(), getLead2(), cathode[0], cathode[1], 1, hs);
		poly = createPolygon(pa[0], pa[1], getLead2());

		gate = newPointArray(2);
		double leadlen = (getDn() - 16) / 2;
		int gatelen = sim.getGridSize();
		gatelen += leadlen % sim.getGridSize();
		if (leadlen < gatelen) {
			setX2(getX1());
			setY2(getY1());
			return;
		}
		interpPoint(getLead2(), getPoint2(), gate[0], gatelen / leadlen, gatelen * dir);
		interpPoint(getLead2(), getPoint2(), gate[1], gatelen / leadlen, sim.getGridSize() * 2
				* dir);
	}

	void setup() {
		diode = new Diode(sim);
		diode.setup(.8, 0);
	}

	public void stamp() {
		sim.stampNonLinear(getNodes()[anode]);
		sim.stampNonLinear(getNodes()[cnode]);
		sim.stampNonLinear(getNodes()[gnode]);
		sim.stampNonLinear(getNodes()[inode]);
		sim.stampResistor(getNodes()[gnode], getNodes()[cnode], cresistance);
		diode.stamp(getNodes()[inode], getNodes()[gnode]);
	}
}
