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
import com.joebotics.simmer.client.gui.util.Font;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;

public class ProbeElm extends AbstractCircuitElement {
	static final int FLAG_SHOWVOLTAGE = 1;

	Point center;

	public ProbeElm(int xx, int yy) {
		super(xx, yy);
	}

	public ProbeElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
	}

	public void draw(Graphics g) {
		int hs = 8;
		setBbox(getPoint1(), getPoint2(), hs);
		boolean selected = (needsHighlight() || sim.getPlotYElm() == this);
		double len = (selected || sim.getDragElm() == this) ? 16 : getDn() - 32;
		calcLeads((int) len);
		setVoltageColor(g, getVolts()[0]);
		if (selected)
			g.setColor(selectColor);
		GraphicsUtil.drawThickLine(g, getPoint1(), getLead1());
		setVoltageColor(g, getVolts()[1]);
		if (selected)
			g.setColor(selectColor);
		GraphicsUtil.drawThickLine(g, getLead2(), getPoint2());
		Font f = new Font("SansSerif", Font.BOLD, 14);
		g.setFont(f);
		if (this == sim.getPlotXElm())
			drawCenteredText(g, "X", center.getX(), center.getY(), true);
		if (this == sim.getPlotYElm())
			drawCenteredText(g, "Y", center.getX(), center.getY(), true);
		if (mustShowVoltage()) {
			String s = getShortUnitText(getVolts()[0], "V");
			drawValues(g, s, 4);
		}
		drawPosts(g);
	}

	public boolean getConnection(int n1, int n2) {
		return false;
	}

	public int getDumpType() {
		return 'p';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Show Voltage", mustShowVoltage());
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "scope probe";
		arr[1] = "Vd = " + getVoltageText(getVoltageDiff());
	}

	boolean mustShowVoltage() {
		return (getFlags() & FLAG_SHOWVOLTAGE) != 0;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			if (ei.checkbox.getState())
				setFlags(FLAG_SHOWVOLTAGE);
			else
				setFlags(getFlags() & ~FLAG_SHOWVOLTAGE);
		}
	}

	public void setPoints() {
		super.setPoints();
		// swap points so that we subtract higher from lower
		if (getPoint2().getY() < getPoint1().getY()) {
			Point x = getPoint1();
			setPoint1(getPoint2());
			setPoint2(x);
		}
		center = interpPoint(getPoint1(), getPoint2(), .5);
	}
}
