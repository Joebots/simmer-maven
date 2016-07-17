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

import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.gui.util.Point;
import com.jobotics.simmer.client.gui.util.Polygon;
import com.jobotics.simmer.client.util.GraphicsUtil;
import com.jobotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class JfetElm extends MosfetElm {
	Polygon arrowPoly;

	Polygon gatePoly;

	Point gatePt;
	JfetElm(int xx, int yy, boolean pnpflag) {
		super(xx, yy, pnpflag);
		setNoDiagonal(true);
	}
	public JfetElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		setNoDiagonal(true);
	}

	public void draw(Graphics g) {
		setBbox(getPoint1(), getPoint2(), hs);
		setVoltageColor(g, getVolts()[1]);
		GraphicsUtil.drawThickLine(g, src[0], src[1]);
		GraphicsUtil.drawThickLine(g, src[1], src[2]);
		setVoltageColor(g, getVolts()[2]);
		GraphicsUtil.drawThickLine(g, drn[0], drn[1]);
		GraphicsUtil.drawThickLine(g, drn[1], drn[2]);
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, getPoint1(), gatePt);
		g.fillPolygon(arrowPoly);
		setPowerColor(g, true);
		g.fillPolygon(gatePoly);
		setCurcount(updateDotCount(-ids, getCurcount()));
		if (getCurcount() != 0) {
			drawDots(g, src[0], src[1], getCurcount());
			drawDots(g, src[1], src[2], getCurcount() + 8);
			drawDots(g, drn[0], drn[1], -getCurcount());
			drawDots(g, drn[1], drn[2], -(getCurcount() + 8));
		}
		drawPosts(g);
	}

	double getBeta() {
		return .00125;
	}

	// these values are taken from Hayes+Horowitz p155
	double getDefaultThreshold() {
		return -4;
	}

	public int getDumpType() {
		return 'j';
	}

	public void getInfo(String arr[]) {
		getFetInfo(arr, "JFET");
	}

	public void setPoints() {
		super.setPoints();

		// find the coordinates of the various points we need to draw
		// the JFET.
		int hs2 = hs * getDsign();
		src = newPointArray(3);
		drn = newPointArray(3);
		interpPoint2(getPoint1(), getPoint2(), src[0], drn[0], 1, hs2);
		interpPoint2(getPoint1(), getPoint2(), src[1], drn[1], 1, hs2 / 2);
		interpPoint2(getPoint1(), getPoint2(), src[2], drn[2], 1 - 10 / getDn(), hs2 / 2);

		gatePt = interpPoint(getPoint1(), getPoint2(), 1 - 14 / getDn());

		Point ra[] = newPointArray(4);
		interpPoint2(getPoint1(), getPoint2(), ra[0], ra[1], 1 - 13 / getDn(), hs);
		interpPoint2(getPoint1(), getPoint2(), ra[2], ra[3], 1 - 10 / getDn(), hs);
		gatePoly = createPolygon(ra[0], ra[1], ra[3], ra[2]);
		if (pnp == -1) {
			Point x = interpPoint(gatePt, getPoint1(), 18 / getDn());
			arrowPoly = calcArrow(gatePt, x, 8, 3);
		} else
			arrowPoly = calcArrow(getPoint1(), gatePt, 8, 3);
	}
}
