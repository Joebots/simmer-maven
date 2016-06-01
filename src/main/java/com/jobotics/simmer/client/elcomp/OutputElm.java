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

import com.jobotics.simmer.client.gui.impl.Checkbox;
import com.jobotics.simmer.client.gui.impl.EditInfo;
import com.jobotics.simmer.client.gui.util.Color;
import com.jobotics.simmer.client.gui.util.Font;
import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.gui.util.Point;
import com.jobotics.simmer.client.util.GraphicsUtil;
import com.jobotics.simmer.client.util.StringTokenizer;



//import java.awt.*;
//import java.util.StringTokenizer;

public class OutputElm extends AbstractCircuitElement {
	final int FLAG_VALUE = 1;

	public OutputElm(int xx, int yy) {
		super(xx, yy);
	}

	public OutputElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
	}

	public void draw(Graphics g) {
		boolean selected = (needsHighlight() || sim.getPlotYElm() == this);
		Font f = new Font("SansSerif", selected ? Font.BOLD : 0, 14);
		g.setFont(f);
		g.setColor(selected ? selectColor : whiteColor);
		String s = (getFlags() & FLAG_VALUE) != 0 ? getVoltageText(getVolts()[0]) : "out";
		// FontMetrics fm = g.getFontMetrics();
		if (this == sim.getPlotXElm())
			s = "X";
		if (this == sim.getPlotYElm())
			s = "Y";
		interpPoint(getPoint1(), getPoint2(), getLead1(), 1
				- ((int) g.getContext().measureText(s).getWidth() / 2 + 8) / getDn());
		setBbox(getPoint1(), getLead1(), 0);
		drawCenteredText(g, s, getX2(), getY2(), true);
		setVoltageColor(g, getVolts()[0]);
		if (selected)
			g.setColor(selectColor);
		GraphicsUtil.drawThickLine(g, getPoint1(), getLead1());
		drawPosts(g);
	}

	public void drawHandles(Graphics g, Color c) {
		g.setColor(c);
		g.fillRect(getX1() - 3, getY1() - 3, 7, 7);
	}

	public int getDumpType() {
		return 'O';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Show Voltage",
					(getFlags() & FLAG_VALUE) != 0);
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "output";
		arr[1] = "V = " + getVoltageText(getVolts()[0]);
	}

	public int getPostCount() {
		return 1;
	}

	public double getVoltageDiff() {
		return getVolts()[0];
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			setFlags((ei.checkbox.getState()) ? (getFlags() | FLAG_VALUE)
					: (getFlags() & ~FLAG_VALUE));
	}

	public void setPoints() {
		super.setPoints();
		setLead1(new Point());
	}

}
