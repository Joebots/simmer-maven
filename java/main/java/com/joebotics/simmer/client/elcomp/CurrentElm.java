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

import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Polygon;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.OptionKey;
import com.joebotics.simmer.client.util.StringTokenizer;




//import java.awt.*;
//import java.util.StringTokenizer;

public class CurrentElm extends AbstractCircuitElement {
	Polygon arrow;

	Point ashaft1, ashaft2, center;

	double currentValue;

	public CurrentElm(int xx, int yy) {
		super(xx, yy);
		currentValue = .01;
	}

	public CurrentElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		try {
			currentValue = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
			currentValue = .01;
		}
	}

	public void draw(Graphics g) {
		int cr = 12;
		draw2Leads(g);
		setVoltageColor(g, (getVolts()[0] + getVolts()[1]) / 2);
		setPowerColor(g, false);

		GraphicsUtil.drawThickCircle(g, center.getX(), center.getY(), cr);
		GraphicsUtil.drawThickLine(g, ashaft1, ashaft2);

		g.fillPolygon(arrow);
		setBbox(getPoint1(), getPoint2(), cr);
		doDots(g);
		if (sim.getOptions().getBoolean(OptionKey.SHOW_VALUES)) {
			String s = getShortUnitText(currentValue, "A");
			if (getDx() == 0 || getDy() == 0)
				drawValues(g, s, cr);
		}
		drawPosts(g);
	}
	public String dump() {
		return super.dump() + " " + currentValue;
	}

	public int getDumpType() {
		return 'i';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Current (A)", currentValue, 0, .1);
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "current source";
		getBasicInfo(arr);
	}

	public double getVoltageDiff() {
		return getVolts()[1] - getVolts()[0];
	}

	public void setEditValue(int n, EditInfo ei) {
		currentValue = ei.value;
	}

	public void setPoints() {
		super.setPoints();
		calcLeads(26);
		ashaft1 = interpPoint(getLead1(), getLead2(), .25);
		ashaft2 = interpPoint(getLead1(), getLead2(), .6);
		center = interpPoint(getLead1(), getLead2(), .5);
		Point p2 = interpPoint(getLead1(), getLead2(), .75);
		arrow = calcArrow(center, p2, 4, 4);
	}

	public void stamp() {
		setCurrent(currentValue);
		sim.stampCurrentSource(getNodes()[0], getNodes()[1], getCurrent());
	}
}
