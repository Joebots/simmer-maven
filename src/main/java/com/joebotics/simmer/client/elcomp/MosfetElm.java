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

import com.joebotics.simmer.client.gui.widget.Checkbox;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Polygon;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;

public class MosfetElm extends AbstractCircuitElement {
	Polygon arrowPoly;
	int FLAG_DIGITAL = 4;
	int FLAG_PNP = 1;
	int FLAG_SHOWVT = 2;
	double gm = 0;

	final int hs = 16;

	double ids;

	double lastv1, lastv2;

	int mode = 0;

	int pcircler;

	int pnp;

	Point src[], drn[], gate[], pcircle;

	double vt;

	MosfetElm(int xx, int yy, boolean pnpflag) {
		super(xx, yy);
		pnp = (pnpflag) ? -1 : 1;
		setFlags((pnpflag) ? FLAG_PNP : 0);
		setNoDiagonal(true);
		vt = getDefaultThreshold();
	}

	public MosfetElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		pnp = ((f & FLAG_PNP) != 0) ? -1 : 1;
		setNoDiagonal(true);
		vt = getDefaultThreshold();
		try {
			vt = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
		}
	}

	public boolean canViewInScope() {
		return true;
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
		int source = 1;
		int drain = 2;
		if (pnp * vs[1] > pnp * vs[2]) {
			source = 2;
			drain = 1;
		}
		int gate = 0;
		double vgs = vs[gate] - vs[source];
		double vds = vs[drain] - vs[source];
		if (Math.abs(lastv1 - vs[1]) > .01 || Math.abs(lastv2 - vs[2]) > .01)
			sim.setConverged(false);
		lastv1 = vs[1];
		lastv2 = vs[2];
		double realvgs = vgs;
		double realvds = vds;
		vgs *= pnp;
		vds *= pnp;
		ids = 0;
		gm = 0;
		double Gds = 0;
		double beta = getBeta();
		if (vgs > .5 && this instanceof JfetElm) {
			sim.stop("JFET is reverse biased!", this);
			return;
		}
		if (vgs < vt) {
			// should be all zero, but that causes a singular matrix,
			// so instead we treat it as a large resistor
			Gds = 1e-8;
			ids = vds * Gds;
			mode = 0;
		} else if (vds < vgs - vt) {
			// linear
			ids = beta * ((vgs - vt) * vds - vds * vds * .5);
			gm = beta * vds;
			Gds = beta * (vgs - vds - vt);
			mode = 1;
		} else {
			// saturation; Gds = 0
			gm = beta * (vgs - vt);
			// use very small Gds to avoid nonconvergence
			Gds = 1e-8;
			ids = .5 * beta * (vgs - vt) * (vgs - vt) + (vds - (vgs - vt))
					* Gds;
			mode = 2;
		}
		double rs = -pnp * ids + Gds * realvds + gm * realvgs;
		// System.out.println("M " + vds + " " + vgs + " " + ids + " " + gm +
		// " "+ Gds + " " + volts[0] + " " + volts[1] + " " + volts[2] + " " +
		// source + " " + rs + " " + this);
		sim.stampMatrix(getNodes()[drain], getNodes()[drain], Gds);
		sim.stampMatrix(getNodes()[drain], getNodes()[source], -Gds - gm);
		sim.stampMatrix(getNodes()[drain], getNodes()[gate], gm);

		sim.stampMatrix(getNodes()[source], getNodes()[drain], -Gds);
		sim.stampMatrix(getNodes()[source], getNodes()[source], Gds + gm);
		sim.stampMatrix(getNodes()[source], getNodes()[gate], -gm);

		sim.stampRightSide(getNodes()[drain], rs);
		sim.stampRightSide(getNodes()[source], -rs);
		if (source == 2 && pnp == 1 || source == 1 && pnp == -1)
			ids = -ids;
	}

	public void draw(Graphics g) {
		setBbox(getPoint1(), getPoint2(), hs);
		setVoltageColor(g, getVolts()[1]);
		GraphicsUtil.drawThickLine(g, src[0], src[1]);
		setVoltageColor(g, getVolts()[2]);
		GraphicsUtil.drawThickLine(g, drn[0], drn[1]);
		int segments = 6;
		int i;
		setPowerColor(g, true);
		double segf = 1. / segments;
		for (i = 0; i != segments; i++) {
			double v = getVolts()[1] + (getVolts()[2] - getVolts()[1]) * i / segments;
			setVoltageColor(g, v);
			interpPoint(src[1], drn[1], ps1, i * segf);
			interpPoint(src[1], drn[1], ps2, (i + 1) * segf);
			GraphicsUtil.drawThickLine(g, ps1, ps2);
		}
		setVoltageColor(g, getVolts()[1]);
		GraphicsUtil.drawThickLine(g, src[1], src[2]);
		setVoltageColor(g, getVolts()[2]);
		GraphicsUtil.drawThickLine(g, drn[1], drn[2]);
		if (!drawDigital()) {
			setVoltageColor(g, pnp == 1 ? getVolts()[1] : getVolts()[2]);
			g.fillPolygon(arrowPoly);
		}
		if (sim.getMainMenuBar().getOptionsMenuBar().getPowerCheckItem().getState())
			g.setColor(Color.gray);
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, getPoint1(), gate[1]);
		GraphicsUtil.drawThickLine(g, gate[0], gate[2]);
		if (drawDigital() && pnp == -1)
			GraphicsUtil.drawThickCircle(g, pcircle.getX(), pcircle.getY(), pcircler);
		if ((getFlags() & FLAG_SHOWVT) != 0) {
			String s = "" + (vt * pnp);
			g.setColor(whiteColor);
			g.setFont(unitsFont);
			drawCenteredText(g, s, getX2() + 2, getY2(), false);
		}
		if ((needsHighlight() || sim.getDragElm() == this) && getDy() == 0) {
			g.setColor(Color.white);
			g.setFont(unitsFont);
			int ds = sign(getDx());
			g.drawString("G", gate[1].getX() - 10 * ds, gate[1].getY() - 5);
			g.drawString(pnp == -1 ? "D" : "S", src[0].getX() - 3 + 9 * ds,
					src[0].getY() + 4); // x+6 if ds=1, -12 if -1
			g.drawString(pnp == -1 ? "S" : "D", drn[0].getX() - 3 + 9 * ds,
					drn[0].getY() + 4);
		}
		setCurcount(updateDotCount(-ids, getCurcount()));
		drawDots(g, src[0], src[1], getCurcount());
		drawDots(g, src[1], drn[1], getCurcount());
		drawDots(g, drn[1], drn[0], getCurcount());
		drawPosts(g);
	}

	boolean drawDigital() {
		return (getFlags() & FLAG_DIGITAL) != 0;
	}

	public String dump() {
		return super.dump() + " " + vt;
	}

	double getBeta() {
		return .02;
	}
	public boolean getConnection(int n1, int n2) {
		return !(n1 == 0 || n2 == 0);
	}
	public double getCurrent() {
		return ids;
	}

	double getDefaultThreshold() {
		return 1.5;
	}

	public int getDumpType() {
		return 'f';
	}
	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Threshold Voltage", pnp * vt, .01, 5);
		if (n == 1) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Digital Symbol", drawDigital());
			return ei;
		}

		return null;
	}
	void getFetInfo(String arr[], String n) {
		arr[0] = ((pnp == -1) ? "p-" : "n-") + n;
		arr[0] += " (Vt = " + getVoltageText(pnp * vt) + ")";
		arr[1] = ((pnp == 1) ? "Ids = " : "Isd = ") + getCurrentText(ids);
		arr[2] = "Vgs = " + getVoltageText(getVolts()[0] - getVolts()[pnp == -1 ? 2 : 1]);
		arr[3] = ((pnp == 1) ? "Vds = " : "Vsd = ")
				+ getVoltageText(getVolts()[2] - getVolts()[1]);
		arr[4] = (mode == 0) ? "off" : (mode == 1) ? "linear" : "saturation";
		arr[5] = "gm = " + getUnitText(gm, "A/V");
	}
	public void getInfo(String arr[]) {
		getFetInfo(arr, "MOSFET");
	}

	public Point getPost(int n) {
		return (n == 0) ? getPoint1() : (n == 1) ? src[0] : drn[0];
	}

	public int getPostCount() {
		return 3;
	}

	public double getPower() {
		return ids * (getVolts()[2] - getVolts()[1]);
	}

	public double getVoltageDiff() {
		return getVolts()[2] - getVolts()[1];
	}

	public boolean nonLinear() {
		return true;
	}

	public void reset() {
		lastv1 = lastv2 = getVolts()[0] = getVolts()[1] = getVolts()[2] = setCurcount(0);
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			vt = pnp * ei.value;
		if (n == 1) {
			setFlags((ei.checkbox.getState()) ? (getFlags() | FLAG_DIGITAL)
					: (getFlags() & ~FLAG_DIGITAL));
			setPoints();
		}
	}

	public void setPoints() {
		super.setPoints();

		// find the coordinates of the various points we need to draw
		// the MOSFET.
		int hs2 = hs * getDsign();
		src = newPointArray(3);
		drn = newPointArray(3);
		interpPoint2(getPoint1(), getPoint2(), src[0], drn[0], 1, -hs2);
		interpPoint2(getPoint1(), getPoint2(), src[1], drn[1], 1 - 22 / getDn(), -hs2);
		interpPoint2(getPoint1(), getPoint2(), src[2], drn[2], 1 - 22 / getDn(), -hs2 * 4 / 3);

		gate = newPointArray(3);
		interpPoint2(getPoint1(), getPoint2(), gate[0], gate[2], 1 - 28 / getDn(), hs2 / 2); // was
																				// 1-20/dn
		interpPoint(gate[0], gate[2], gate[1], .5);

		if (!drawDigital()) {
			if (pnp == 1)
				arrowPoly = calcArrow(src[1], src[0], 10, 4);
			else
				arrowPoly = calcArrow(drn[0], drn[1], 12, 5);
		} else if (pnp == -1) {
			interpPoint(getPoint1(), getPoint2(), gate[1], 1 - 36 / getDn());
			int dist = (getDsign() < 0) ? 32 : 31;
			pcircle = interpPoint(getPoint1(), getPoint2(), 1 - dist / getDn());
			pcircler = 3;
		}
	}

	public void stamp() {
		sim.stampNonLinear(getNodes()[1]);
		sim.stampNonLinear(getNodes()[2]);
	}
}
