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
import com.joebotics.simmer.client.gui.impl.Scope;
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Polygon;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;

public class TransistorElm extends AbstractCircuitElement {
	private static final double leakage = 1e-13; // 1e-6;
	private static final double rgain = .5;
	private static final double vt = .025;
	private static final double vdcoef = 1 / vt;
	private double beta;
	private double fgain;
	private final int FLAG_FLIP = 1;
	private double gmin;
	private double ic, ie, ib, curcount_c, curcount_e, curcount_b;
	private double lastvbc, lastvbe;
	private int pnp;
	private Point rect[], coll[], emit[], base;
	private Polygon rectPoly, arrowPoly;
	private double vcrit;

	public TransistorElm(int xx, int yy, boolean pnpflag) {
		super(xx, yy);
		pnp = (pnpflag) ? -1 : 1;
		beta = 100;
		setup();
	}

	public TransistorElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		pnp = new Integer(st.nextToken()).intValue();
		beta = 100;
		try {
			lastvbe = new Double(st.nextToken()).doubleValue();
			lastvbc = new Double(st.nextToken()).doubleValue();
			getVolts()[0] = 0;
			getVolts()[1] = -lastvbe;
			getVolts()[2] = -lastvbc;
			beta = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
		}
		setup();
	}

	public boolean canViewInScope() {
		return true;
	}

	public void doStep() {
		double vbc = getVolts()[0] - getVolts()[1]; // typically negative
		double vbe = getVolts()[0] - getVolts()[2]; // typically positive
		if (Math.abs(vbc - lastvbc) > .01 || // .01
				Math.abs(vbe - lastvbe) > .01)
			sim.setConverged(false);
		gmin = 0;
		if (sim.getSubIterations() > 100) {
			// if we have trouble converging, put a conductance in parallel with
			// all P-N junctions.
			// Gradually increase the conductance value for each iteration.
			gmin = Math
					.exp(-9 * Math.log(10) * (1 - sim.getSubIterations() / 3000.));
			if (gmin > .1)
				gmin = .1;
		}
		// System.out.print("T " + vbc + " " + vbe + "\n");
		vbc = pnp * limitStep(pnp * vbc, pnp * lastvbc);
		vbe = pnp * limitStep(pnp * vbe, pnp * lastvbe);
		lastvbc = vbc;
		lastvbe = vbe;
		double pcoef = vdcoef * pnp;
		double expbc = Math.exp(vbc * pcoef);
		/*
		 * if (expbc > 1e13 || Double.isInfinite(expbc)) expbc = 1e13;
		 */
		double expbe = Math.exp(vbe * pcoef);
		if (expbe < 1)
			expbe = 1;
		/*
		 * if (expbe > 1e13 || Double.isInfinite(expbe)) expbe = 1e13;
		 */
		ie = pnp * leakage * (-(expbe - 1) + rgain * (expbc - 1));
		ic = pnp * leakage * (fgain * (expbe - 1) - (expbc - 1));
		ib = -(ie + ic);
		// System.out.println("gain " + ic/ib);
		// System.out.print("T " + vbc + " " + vbe + " " + ie + " " + ic +
		// "\n");
		double gee = -leakage * vdcoef * expbe;
		double gec = rgain * leakage * vdcoef * expbc;
		double gce = -gee * fgain;
		double gcc = -gec * (1 / rgain);

		/*
		 * System.out.print("gee = " + gee + "\n"); System.out.print("gec = " +
		 * gec + "\n"); System.out.print("gce = " + gce + "\n");
		 * System.out.print("gcc = " + gcc + "\n");
		 * System.out.print("gce+gcc = " + (gce+gcc) + "\n");
		 * System.out.print("gee+gec = " + (gee+gec) + "\n");
		 */

		// stamps from page 302 of Pillage. Node 0 is the base,
		// node 1 the collector, node 2 the emitter. Also stamp
		// minimum conductance (gmin) between b,e and b,c
		sim.stampMatrix(getNodes()[0], getNodes()[0], -gee - gec - gce - gcc + gmin * 2);
		sim.stampMatrix(getNodes()[0], getNodes()[1], gec + gcc - gmin);
		sim.stampMatrix(getNodes()[0], getNodes()[2], gee + gce - gmin);
		sim.stampMatrix(getNodes()[1], getNodes()[0], gce + gcc - gmin);
		sim.stampMatrix(getNodes()[1], getNodes()[1], -gcc + gmin);
		sim.stampMatrix(getNodes()[1], getNodes()[2], -gce);
		sim.stampMatrix(getNodes()[2], getNodes()[0], gee + gec - gmin);
		sim.stampMatrix(getNodes()[2], getNodes()[1], -gec);
		sim.stampMatrix(getNodes()[2], getNodes()[2], -gee + gmin);

		// we are solving for v(k+1), not delta v, so we use formula
		// 10.5.13, multiplying J by v(k)
		sim.stampRightSide(getNodes()[0], -ib - (gec + gcc) * vbc - (gee + gce)
				* vbe);
		sim.stampRightSide(getNodes()[1], -ic + gce * vbe + gcc * vbc);
		sim.stampRightSide(getNodes()[2], -ie + gee * vbe + gec * vbc);
	}

	public void draw(Graphics g) {
		setBbox(getPoint1(), getPoint2(), 16);
		setPowerColor(g, true);
		// draw collector
		setVoltageColor(g, getVolts()[1]);
		GraphicsUtil.drawThickLine(g, coll[0], coll[1]);
		// draw emitter
		setVoltageColor(g, getVolts()[2]);
		GraphicsUtil.drawThickLine(g, emit[0], emit[1]);
		// draw arrow
		g.setColor(lightGrayColor);
		g.fillPolygon(arrowPoly);
		// draw base
		setVoltageColor(g, getVolts()[0]);
		if (sim.getMainMenuBar().getOptionsMenuBar().getPowerCheckItem().getState())
			g.setColor(Color.gray);
		GraphicsUtil.drawThickLine(g, getPoint1(), base);
		// draw dots
		curcount_b = updateDotCount(-ib, curcount_b);
		drawDots(g, base, getPoint1(), curcount_b);
		curcount_c = updateDotCount(-ic, curcount_c);
		drawDots(g, coll[1], coll[0], curcount_c);
		curcount_e = updateDotCount(-ie, curcount_e);
		drawDots(g, emit[1], emit[0], curcount_e);
		// draw base rectangle
		setVoltageColor(g, getVolts()[0]);
		setPowerColor(g, true);
		g.fillPolygon(rectPoly);

		if ((needsHighlight() || sim.getDragElm() == this) && getDy() == 0) {
			g.setColor(Color.white);
			// IES
			// g.setFont(unitsFont);
			int ds = sign(getDx());
			g.drawString("B", base.getX() - 10 * ds, base.getY() - 5);
			g.drawString("C", coll[0].getX() - 3 + 9 * ds, coll[0].getY() + 4); // x+6 if
																		// ds=1,
																		// -12
																		// if -1
			g.drawString("E", emit[0].getX() - 3 + 9 * ds, emit[0].getY() + 4);
		}
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + pnp + " " + (getVolts()[0] - getVolts()[1]) + " "
				+ (getVolts()[0] - getVolts()[2]) + " " + beta;
	}

	public int getDumpType() {
		return 't';
	}
	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Beta/hFE", beta, 10, 1000).setDimensionless();
		if (n == 1) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Swap E/C", (getFlags() & FLAG_FLIP) != 0);
			return ei;
		}
		return null;
	}
	public void getInfo(String arr[]) {
		arr[0] = "transistor (" + ((pnp == -1) ? "PNP)" : "NPN)") + " beta="
				+ showFormat.format(beta);
		double vbc = getVolts()[0] - getVolts()[1];
		double vbe = getVolts()[0] - getVolts()[2];
		double vce = getVolts()[1] - getVolts()[2];
		if (vbc * pnp > .2)
			arr[1] = vbe * pnp > .2 ? "saturation" : "reverse active";
		else
			arr[1] = vbe * pnp > .2 ? "fwd active" : "cutoff";
		arr[2] = "Ic = " + getCurrentText(ic);
		arr[3] = "Ib = " + getCurrentText(ib);
		arr[4] = "Vbe = " + getVoltageText(vbe);
		arr[5] = "Vbc = " + getVoltageText(vbc);
		arr[6] = "Vce = " + getVoltageText(vce);
	}
	public Point getPost(int n) {
		return (n == 0) ? getPoint1() : (n == 1) ? coll[0] : emit[0];
	}
	public int getPostCount() {
		return 3;
	}
	public double getPower() {
		return (getVolts()[0] - getVolts()[2]) * ib + (getVolts()[1] - getVolts()[2]) * ic;
	}

	public String getScopeUnits(int x) {
		switch (x) {
		case Scope.VAL_IB:
		case Scope.VAL_IC:
		case Scope.VAL_IE:
			return "A";
		default:
			return "V";
		}
	}

	public double getScopeValue(int x) {
		switch (x) {
		case Scope.VAL_IB:
			return ib;
		case Scope.VAL_IC:
			return ic;
		case Scope.VAL_IE:
			return ie;
		case Scope.VAL_VBE:
			return getVolts()[0] - getVolts()[2];
		case Scope.VAL_VBC:
			return getVolts()[0] - getVolts()[1];
		case Scope.VAL_VCE:
			return getVolts()[1] - getVolts()[2];
		}
		return 0;
	}

	private double limitStep(double vnew, double vold) {
		double arg;
//		double oo = vnew;

		if (vnew > vcrit && Math.abs(vnew - vold) > (vt + vt)) {
			if (vold > 0) {
				arg = 1 + (vnew - vold) / vt;
				if (arg > 0) {
					vnew = vold + vt * Math.log(arg);
				} else {
					vnew = vcrit;
				}
			} else {
				vnew = vt * Math.log(vnew / vt);
			}
			sim.setConverged(false);
			// System.out.println(vnew + " " + oo + " " + vold);
		}
		return (vnew);
	}

	public boolean nonLinear() {
		return true;
	}

	public void reset() {
		getVolts()[0] = getVolts()[1] = getVolts()[2] = 0;
		lastvbc = lastvbe = curcount_c = curcount_e = curcount_b = 0;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			beta = ei.value;
			setup();
		}
		if (n == 1) {
			if (ei.checkbox.getState())
				setFlags(getFlags() | FLAG_FLIP);
			else
				setFlags(getFlags() & ~FLAG_FLIP);
			setPoints();
		}
	}

	public void setPoints() {
		super.setPoints();
		int hs = 16;
		if ((getFlags() & FLAG_FLIP) != 0)
			setDsign(-getDsign());
		int hs2 = hs * getDsign() * pnp;
		// calc collector, emitter posts
		coll = newPointArray(2);
		emit = newPointArray(2);
		interpPoint2(getPoint1(), getPoint2(), coll[0], emit[0], 1, hs2);
		// calc rectangle edges
		rect = newPointArray(4);
		interpPoint2(getPoint1(), getPoint2(), rect[0], rect[1], 1 - 16 / getDn(), hs);
		interpPoint2(getPoint1(), getPoint2(), rect[2], rect[3], 1 - 13 / getDn(), hs);
		// calc points where collector/emitter leads contact rectangle
		interpPoint2(getPoint1(), getPoint2(), coll[1], emit[1], 1 - 13 / getDn(), 6 * getDsign()
				* pnp);
		// calc point where base lead contacts rectangle
		base = new Point();
		interpPoint(getPoint1(), getPoint2(), base, 1 - 16 / getDn());
		// rectangle
		rectPoly = createPolygon(rect[0], rect[2], rect[3], rect[1]);

		// arrow
		if (pnp == 1)
			arrowPoly = calcArrow(emit[1], emit[0], 8, 4);
		else {
			Point pt = interpPoint(getPoint1(), getPoint2(), 1 - 11 / getDn(), -5 * getDsign()
					* pnp);
			arrowPoly = calcArrow(emit[0], pt, 8, 4);
		}
	}

	void setup() {
		vcrit = vt * Math.log(vt / (Math.sqrt(2) * leakage));
		fgain = beta / (beta + 1);
		setNoDiagonal(true);
	}

	public void stamp() {
		sim.stampNonLinear(getNodes()[0]);
		sim.stampNonLinear(getNodes()[1]);
		sim.stampNonLinear(getNodes()[2]);
	}
}
