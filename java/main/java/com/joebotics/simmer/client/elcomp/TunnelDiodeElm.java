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

import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Polygon;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;

public class TunnelDiodeElm extends AbstractCircuitElement {
	static final double pip = 4.7e-3;

	static final double piv = 370e-6;

	static final double pvp = .1;

	static final double pvpp = .525;

	static final double pvt = .026;

	static final double pvv = .37;
	Point cathode[];
	final int hs = 8;

	double lastvoltdiff;

	Polygon poly;

	public TunnelDiodeElm(int xx, int yy) {
		super(xx, yy);
		setup();
	}

	public TunnelDiodeElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		setup();
	}

	public void calculateCurrent() {
		double voltdiff = getVolts()[0] - getVolts()[1];
		setCurrent(pip * Math.exp(-pvpp / pvt) * (Math.exp(voltdiff / pvt) - 1)
				+ pip * (voltdiff / pvp) * Math.exp(1 - voltdiff / pvp) + piv
				* Math.exp(voltdiff - pvv));
	}

	public void doStep() {
		double voltdiff = getVolts()[0] - getVolts()[1];
		if (Math.abs(voltdiff - lastvoltdiff) > .01)
			sim.setConverged(false);
		// System.out.println(voltdiff + " " + lastvoltdiff + " " +
		// Math.abs(voltdiff-lastvoltdiff));
		voltdiff = limitStep(voltdiff, lastvoltdiff);
		lastvoltdiff = voltdiff;

		double i = pip * Math.exp(-pvpp / pvt) * (Math.exp(voltdiff / pvt) - 1)
				+ pip * (voltdiff / pvp) * Math.exp(1 - voltdiff / pvp) + piv
				* Math.exp(voltdiff - pvv);

		double geq = pip * Math.exp(-pvpp / pvt) * Math.exp(voltdiff / pvt)
				/ pvt + pip * Math.exp(1 - voltdiff / pvp) / pvp
				- Math.exp(1 - voltdiff / pvp) * pip * voltdiff / (pvp * pvp)
				+ Math.exp(voltdiff - pvv) * piv;
		double nc = i - geq * voltdiff;
		sim.stampConductance(getNodes()[0], getNodes()[1], geq);
		sim.stampCurrentSource(getNodes()[0], getNodes()[1], nc);
	}

	public void draw(Graphics g) {
		setBbox(getPoint1(), getPoint2(), hs);

		double v1 = getVolts()[0];
		double v2 = getVolts()[1];

		draw2Leads(g);

		// draw arrow thingy
		setPowerColor(g, true);
		setVoltageColor(g, v1);
		g.fillPolygon(poly);

		// draw thing arrow is pointing to
		setVoltageColor(g, v2);
		GraphicsUtil.drawThickLine(g, cathode[0], cathode[1]);
		GraphicsUtil.drawThickLine(g, cathode[2], cathode[0]);
		GraphicsUtil.drawThickLine(g, cathode[3], cathode[1]);

		doDots(g);
		drawPosts(g);
	}
	public int getDumpType() {
		return 175;
	}
	public void getInfo(String arr[]) {
		arr[0] = "tunnel diode";
		arr[1] = "I = " + getCurrentText(getCurrent());
		arr[2] = "Vd = " + getVoltageText(getVoltageDiff());
		arr[3] = "P = " + getUnitText(getPower(), "W");
	}
	double limitStep(double vnew, double vold) {
		// Prevent voltage changes of more than 1V when iterating. Wow, I
		// thought it would be
		// much harder than this to prevent convergence problems.
		if (vnew > vold + 1)
			return vold + 1;
		if (vnew < vold - 1)
			return vold - 1;
		return vnew;
	}
	public boolean nonLinear() {
		return true;
	}
	public void reset() {
		lastvoltdiff = getVolts()[0] = getVolts()[1] = setCurcount(0);
	}

	public void setPoints() {
		super.setPoints();
		calcLeads(16);
		cathode = newPointArray(4);
		Point pa[] = newPointArray(2);
		interpPoint2(getLead1(), getLead2(), pa[0], pa[1], 0, hs);
		interpPoint2(getLead1(), getLead2(), cathode[0], cathode[1], 1, hs);
		interpPoint2(getLead1(), getLead2(), cathode[2], cathode[3], .8, hs);
		poly = createPolygon(pa[0], pa[1], getLead2());
	}

	void setup() {
	}

	public void stamp() {
		sim.stampNonLinear(getNodes()[0]);
		sim.stampNonLinear(getNodes()[1]);
	}
}
