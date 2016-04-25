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

import com.jobotics.simmer.client.gui.util.Color;
import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.gui.util.Point;
import com.jobotics.simmer.client.util.GraphicsUtil;
import com.jobotics.simmer.client.util.StringTokenizer;



//import java.awt.*;
//import java.util.StringTokenizer;

public class TriodeElm extends AbstractCircuitElement {
	int circler;
	double curcountp, curcountc, curcountg, currentp, currentg, currentc;
	final double gridCurrentR = 6000;

	double lastv0, lastv1, lastv2;

	double mu, kg1;

	Point plate[], grid[], cath[], midgrid, midcath;

	public TriodeElm(int xx, int yy) {
		super(xx, yy);
		mu = 93;
		kg1 = 680;
		setup();
	}

	public TriodeElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		mu = new Double(st.nextToken()).doubleValue();
		kg1 = new Double(st.nextToken()).doubleValue();
		setup();
	}

	public void doStep() {
		double vs[] = new double[3];
		vs[0] = getVolts()[0];
		vs[1] = getVolts()[1];
		vs[2] = getVolts()[2];
		if (vs[1] > lastv1 + .5)
			vs[1] = lastv1 + .5;
		if (vs[1] < lastv1 - .5)
			vs[1] = lastv1 - .5;
		if (vs[2] > lastv2 + .5)
			vs[2] = lastv2 + .5;
		if (vs[2] < lastv2 - .5)
			vs[2] = lastv2 - .5;
		int grid = 1;
		int cath = 2;
		int plate = 0;
		double vgk = vs[grid] - vs[cath];
		double vpk = vs[plate] - vs[cath];
		if (Math.abs(lastv0 - vs[0]) > .01 || Math.abs(lastv1 - vs[1]) > .01
				|| Math.abs(lastv2 - vs[2]) > .01)
			sim.setConverged(false);
		lastv0 = vs[0];
		lastv1 = vs[1];
		lastv2 = vs[2];
		double ids = 0;
		double gm = 0;
		double Gds = 0;
		double ival = vgk + vpk / mu;
		currentg = 0;
		if (vgk > .01) {
			sim.stampResistor(getNodes()[grid], getNodes()[cath], gridCurrentR);
			currentg = vgk / gridCurrentR;
		}
		if (ival < 0) {
			// should be all zero, but that causes a singular matrix,
			// so instead we treat it as a large resistor
			Gds = 1e-8;
			ids = vpk * Gds;
		} else {
			ids = Math.pow(ival, 1.5) / kg1;
			double q = 1.5 * Math.sqrt(ival) / kg1;
			// gm = dids/dgk;
			// Gds = dids/dpk;
			Gds = q;
			gm = q / mu;
		}
		currentp = ids;
		currentc = ids + currentg;
		double rs = -ids + Gds * vpk + gm * vgk;
		sim.stampMatrix(getNodes()[plate], getNodes()[plate], Gds);
		sim.stampMatrix(getNodes()[plate], getNodes()[cath], -Gds - gm);
		sim.stampMatrix(getNodes()[plate], getNodes()[grid], gm);

		sim.stampMatrix(getNodes()[cath], getNodes()[plate], -Gds);
		sim.stampMatrix(getNodes()[cath], getNodes()[cath], Gds + gm);
		sim.stampMatrix(getNodes()[cath], getNodes()[grid], -gm);

		sim.stampRightSide(getNodes()[plate], rs);
		sim.stampRightSide(getNodes()[cath], -rs);
	}

	public void draw(Graphics g) {
		g.setColor(Color.gray);
		GraphicsUtil.drawThickCircle(g, getPoint2().x, getPoint2().y, circler);
		setBbox(getPoint1(), plate[0], 16);
		adjustBbox(cath[0].x, cath[1].y, getPoint2().x + circler, getPoint2().y + circler);
		setPowerColor(g, true);
		// draw plate
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, plate[0], plate[1]);
		GraphicsUtil.drawThickLine(g, plate[2], plate[3]);
		// draw grid
		setVoltageColor(g, getVolts()[1]);
		int i;
		for (i = 0; i != 8; i += 2)
			GraphicsUtil.drawThickLine(g, grid[i], grid[i + 1]);
		// draw cathode
		setVoltageColor(g, getVolts()[2]);
		for (i = 0; i != 3; i++)
			GraphicsUtil.drawThickLine(g, cath[i], cath[i + 1]);
		// draw dots
		curcountp = updateDotCount(currentp, curcountp);
		curcountc = updateDotCount(currentc, curcountc);
		curcountg = updateDotCount(currentg, curcountg);
		if (sim.dragElm != this) {
			drawDots(g, plate[0], midgrid, curcountp);
			drawDots(g, midgrid, midcath, curcountc);
			drawDots(g, midcath, cath[1], curcountc + 8);
			drawDots(g, cath[1], cath[0], curcountc + 8);
			drawDots(g, getPoint1(), midgrid, curcountg);
		}
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + mu + " " + kg1;
	}
	// grid not connected to other terminals
	public boolean getConnection(int n1, int n2) {
		return !(n1 == 1 || n2 == 1);
	}

	public int getDumpType() {
		return 173;
	}

	public void getInfo(String arr[]) {
		arr[0] = "triode";
		double vbc = getVolts()[0] - getVolts()[1];
		double vbe = getVolts()[0] - getVolts()[2];
		double vce = getVolts()[1] - getVolts()[2];
		arr[1] = "Vbe = " + getVoltageText(vbe);
		arr[2] = "Vbc = " + getVoltageText(vbc);
		arr[3] = "Vce = " + getVoltageText(vce);
	}

	public Point getPost(int n) {
		return (n == 0) ? plate[0] : (n == 1) ? grid[0] : cath[0];
	}

	public int getPostCount() {
		return 3;
	}

	public double getPower() {
		return (getVolts()[0] - getVolts()[2]) * getCurrent();
	}

	public boolean nonLinear() {
		return true;
	}

	public void reset() {
		getVolts()[0] = getVolts()[1] = getVolts()[2] = 0;
		setCurcount(0);
	}

	public void setPoints() {
		super.setPoints();
		plate = newPointArray(4);
		grid = newPointArray(8);
		cath = newPointArray(4);
		grid[0] = getPoint1();
		int nearw = 8;
		interpPoint(getPoint1(), getPoint2(), plate[1], 1, nearw);
		int farw = 32;
		interpPoint(getPoint1(), getPoint2(), plate[0], 1, farw);
		int platew = 18;
		interpPoint2(getPoint2(), plate[1], plate[2], plate[3], 1, platew);

		circler = 24;
		interpPoint(getPoint1(), getPoint2(), grid[1], (getDn() - circler) / getDn(), 0);
		int i;
		for (i = 0; i != 3; i++) {
			interpPoint(grid[1], getPoint2(), grid[2 + i * 2], (i * 3 + 1) / 4.5, 0);
			interpPoint(grid[1], getPoint2(), grid[3 + i * 2], (i * 3 + 2) / 4.5, 0);
		}
		midgrid = getPoint2();

		int cathw = 16;
		midcath = interpPoint(getPoint1(), getPoint2(), 1, -nearw);
		interpPoint2(getPoint2(), plate[1], cath[1], cath[2], -1, cathw);
		interpPoint(getPoint2(), plate[1], cath[3], -1.2, -cathw);
		interpPoint(getPoint2(), plate[1], cath[0], -farw / (double) nearw, cathw);
	}

	void setup() {
		setNoDiagonal(true);
	}

	public void stamp() {
		sim.stampNonLinear(getNodes()[0]);
		sim.stampNonLinear(getNodes()[1]);
		sim.stampNonLinear(getNodes()[2]);
	}
}
