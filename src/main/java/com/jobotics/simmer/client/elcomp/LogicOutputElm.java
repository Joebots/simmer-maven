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
import com.jobotics.simmer.client.util.GraphicsUtil;
import com.jobotics.simmer.client.util.StringTokenizer;



//import java.awt.*;
//import java.util.StringTokenizer;

public class LogicOutputElm extends AbstractCircuitElement {
	final int FLAG_NUMERIC = 2;
	final int FLAG_PULLDOWN = 4;
	final int FLAG_TERNARY = 1;
	double threshold;
	String value;

	public LogicOutputElm(int xx, int yy) {
		super(xx, yy);
		threshold = 2.5;
	}

	public LogicOutputElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		try {
			threshold = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
			threshold = 2.5;
		}
	}

	public void draw(Graphics g) {
		Font oldf = g.getFont();
		Font f = new Font("SansSerif", Font.BOLD, 20);
		g.setFont(f);
		// g.setColor(needsHighlight() ? selectColor : lightGrayColor);
		g.setColor(lightGrayColor);
		String s = (getVolts()[0] < threshold) ? "L" : "H";
		if (isTernary()) {
			if (getVolts()[0] > 3.75)
				s = "2";
			else if (getVolts()[0] > 1.25)
				s = "1";
			else
				s = "0";
		} else if (isNumeric())
			s = (getVolts()[0] < threshold) ? "0" : "1";
		value = s;
		setBbox(getPoint1(), getLead1(), 0);
		drawCenteredText(g, s, getX2(), getY2(), true);
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, getPoint1(), getLead1());
		drawPosts(g);
		g.setFont(oldf);
	}

	public void drawHandles(Graphics g, Color c) {
		g.setColor(c);
		g.fillRect(getX1() - 3, getY1() - 3, 7, 7);
	}

	public String dump() {
		return super.dump() + " " + threshold;
	}

	public int getDumpType() {
		return 'M';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Threshold", threshold, 10, -10);
		if (n == 1) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Current Required", needsPullDown());
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "logic output";
		arr[1] = (getVolts()[0] < threshold) ? "low" : "high";
		if (isNumeric())
			arr[1] = value;
		arr[2] = "V = " + getVoltageText(getVolts()[0]);
	}

	public int getPostCount() {
		return 1;
	}

	public int getShortcut() {
		return 'o';
	}

	public double getVoltageDiff() {
		return getVolts()[0];
	}

	boolean isNumeric() {
		return (getFlags() & (FLAG_TERNARY | FLAG_NUMERIC)) != 0;
	}

	boolean isTernary() {
		return (getFlags() & FLAG_TERNARY) != 0;
	}

	boolean needsPullDown() {
		return (getFlags() & FLAG_PULLDOWN) != 0;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			threshold = ei.value;
		if (n == 1) {
			if (ei.checkbox.getState())
				setFlags(FLAG_PULLDOWN);
			else
				setFlags(getFlags() & ~FLAG_PULLDOWN);
		}
	}

	public void setPoints() {
		super.setPoints();
		setLead1(interpPoint(getPoint1(), getPoint2(), 1 - 12 / getDn()));
	}

	public void stamp() {
		if (needsPullDown())
			sim.stampResistor(getNodes()[0], 0, 1e6);
	}

}
