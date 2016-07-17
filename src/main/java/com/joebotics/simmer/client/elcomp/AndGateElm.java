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

public class AndGateElm extends GateElm {
	public AndGateElm(int xx, int yy) {
		super(xx, yy);
	}

	public AndGateElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	boolean calcFunction() {
		int i;
		boolean f = true;
		for (i = 0; i != inputCount; i++)
			f &= getInput(i);
		return f;
	}

	public int getDumpType() {
		return 150;
	}

	public String getGateName() {
		return "AND gate";
	}

	public int getShortcut() {
		return '2';
	}

	public void setPoints() {
		super.setPoints();

		// 0=topleft, 1-10 = top curve, 11 = right, 12-21=bottom curve,
		// 22 = bottom left
		Point triPoints[] = newPointArray(23);
		interpPoint2(getLead1(), getLead2(), triPoints[0], triPoints[22], 0, hs2);
		int i;
		for (i = 0; i != 10; i++) {
			double a = i * .1;
			double b = Math.sqrt(1 - a * a);
			interpPoint2(getLead1(), getLead2(), triPoints[i + 1], triPoints[21 - i],
					.5 + a / 2, b * hs2);
		}
		triPoints[11] = new Point(getLead2());
		if (isInverting()) {
			pcircle = interpPoint(getPoint1(), getPoint2(), .5 + (ww + 4) / getDn());
			setLead2(interpPoint(getPoint1(), getPoint2(), .5 + (ww + 8) / getDn()));
		}
		gatePoly = createPolygon(triPoints);
	}
}
