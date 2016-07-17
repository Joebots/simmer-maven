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

import com.jobotics.simmer.client.gui.util.Point;
import com.jobotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class OrGateElm extends GateElm {
	public OrGateElm(int xx, int yy) {
		super(xx, yy);
	}

	public OrGateElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	boolean calcFunction() {
		int i;
		boolean f = false;
		for (i = 0; i != inputCount; i++)
			f |= getInput(i);
		return f;
	}

	public int getDumpType() {
		return 152;
	}

	String getGateName() {
		return "OR gate";
	}

	public int getShortcut() {
		return '3';
	}

	public void setPoints() {
		super.setPoints();

		// 0-15 = top curve, 16 = right, 17-32=bottom curve,
		// 33-37 = left curve
		Point triPoints[] = newPointArray(38);
		if (this instanceof XorGateElm)
			linePoints = new Point[5];
		int i;
		for (i = 0; i != 16; i++) {
			double a = i / 16.;
			double b = 1 - a * a;
			interpPoint2(getLead1(), getLead2(), triPoints[i], triPoints[32 - i],
					.5 + a / 2, b * hs2);
		}
		double ww2 = (ww == 0) ? getDn() * 2 : ww * 2;
		for (i = 0; i != 5; i++) {
			double a = (i - 2) / 2.;
			double b = 4 * (1 - a * a) - 2;
			interpPoint(getLead1(), getLead2(), triPoints[33 + i], b / (ww2), a * hs2);
			if (this instanceof XorGateElm)
				linePoints[i] = interpPoint(getLead1(), getLead2(), (b - 5) / (ww2), a
						* hs2);
		}
		triPoints[16] = new Point(getLead2());
		if (isInverting()) {
			pcircle = interpPoint(getPoint1(), getPoint2(), .5 + (ww + 4) / getDn());
			setLead2(interpPoint(getPoint1(), getPoint2(), .5 + (ww + 8) / getDn()));
		}
		gatePoly = createPolygon(triPoints);
	}
}
