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
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;



public class GroundElm extends AbstractCircuitElement {
	public GroundElm(int xx, int yy) {
		super(xx, yy);
	}

	public GroundElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
	}

	public void draw(Graphics g) {
		setVoltageColor(g, 0);
		GraphicsUtil.drawThickLine(g, getPoint1(), getPoint2());
		int i;
		for (i = 0; i != 3; i++) {
			int a = 10 - i * 4;
			int b = i * 5; // -10;
			interpPoint2(getPoint1(), getPoint2(), ps1, ps2, 1 + b / getDn(), a);
			GraphicsUtil.drawThickLine(g, ps1, ps2);
		}
		doDots(g);
		interpPoint(getPoint1(), getPoint2(), ps2, 1 + 11. / getDn());
		setBbox(getPoint1(), ps2, 11);
		drawPost(g, getX1(), getY1(), getNodes()[0]);
	}

	public int getDumpType() {
		return 'g';
	}

	public void getInfo(String arr[]) {
		arr[0] = "ground";
		arr[1] = "I = " + getCurrentText(getCurrent());
	}

	public int getPostCount() {
		return 1;
	}

	public int getShortcut() {
		return 'g';
	}

	public double getVoltageDiff() {
		return 0;
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	public boolean hasGroundConnection(int n1) {
		return true;
	}

	public void setCurrent(int x, double c) {
		current = -c;
	}

	public void stamp() {
		sim.stampVoltageSource(0, getNodes()[0], getVoltSource(), 0);
	}
}
