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

public class LogicInputElm extends SwitchElm {
	final int FLAG_NUMERIC = 2;
	final int FLAG_TERNARY = 1;
	double hiV, loV;

	public LogicInputElm(int xx, int yy) {
		super(xx, yy, false);
		hiV = 5;
		loV = 0;
	}

	public LogicInputElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		try {
			hiV = new Double(st.nextToken()).doubleValue();
			loV = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
			hiV = 5;
			loV = 0;
		}
		if (isTernary())
			posCount = 3;
	}

	public void draw(Graphics g) {
		Font oldf = g.getFont();
		Font f = new Font("SansSerif", Font.BOLD, 20);
		g.setFont(f);
		g.setColor(needsHighlight() ? selectColor : whiteColor);
		String s = position == 0 ? "L" : "H";
		if (isNumeric())
			s = "" + position;
		setBbox(getPoint1(), getLead1(), 0);
		drawCenteredText(g, s, getX2(), getY2(), true);
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, getPoint1(), getLead1());
		updateDotCount();
		drawDots(g, getPoint1(), getLead1(), getCurcount());
		drawPosts(g);
		g.setFont(oldf);
	}

	public void drawHandles(Graphics g, Color c) {
		g.setColor(c);
		g.fillRect(getX1() - 3, getY1() - 3, 7, 7);
	}

	public String dump() {
		return super.dump() + " " + hiV + " " + loV;
	}

	public int getDumpType() {
		return 'L';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("", 0, 0, 0);
			ei.checkbox = new Checkbox("Momentary Switch", isMomentary());
			return ei;
		}
		if (n == 1)
			return new EditInfo("High Voltage", hiV, 10, -10);
		if (n == 2)
			return new EditInfo("Low Voltage", loV, 10, -10);
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "logic input";
		arr[1] = (position == 0) ? "low" : "high";
		if (isNumeric())
			arr[1] = "" + position;
		arr[1] += " (" + getVoltageText(getVolts()[0]) + ")";
		arr[2] = "I = " + getCurrentText(getCurrent());
	}

	public int getPostCount() {
		return 1;
	}

	public int getShortcut() {
		return 'i';
	}

	public double getVoltageDiff() {
		return getVolts()[0];
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	public boolean hasGroundConnection(int n1) {
		return true;
	}

	boolean isNumeric() {
		return (getFlags() & (FLAG_TERNARY | FLAG_NUMERIC)) != 0;
	}

	boolean isTernary() {
		return (getFlags() & FLAG_TERNARY) != 0;
	}

	public void setCurrent(int vs, double c) {
		current = -c;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			setMomentary(ei.checkbox.getState());
		if (n == 1)
			hiV = ei.value;
		if (n == 2)
			loV = ei.value;
	}

	public void setPoints() {
		super.setPoints();
		setLead1(interpPoint(getPoint1(), getPoint2(), 1 - 12 / getDn()));
	}

	public void stamp() {
		double v = (position == 0) ? loV : hiV;
		if (isTernary())
			v = position * 2.5;
		sim.stampVoltageSource(0, getNodes()[0], getVoltSource(), v);
	}

}
