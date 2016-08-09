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

import com.joebotics.simmer.client.gui.widget.Checkbox;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class Switch2Elm extends SwitchElm {
	static final int FLAG_CENTER_OFF = 1;
	int link;

	final int openhs = 16;

	Point swposts[], swpoles[];

	public Switch2Elm(int xx, int yy) {
		super(xx, yy, false);
		setNoDiagonal(true);
	}

	public Switch2Elm(int xx, int yy, boolean mm) {
		super(xx, yy, mm);
		setNoDiagonal(true);
	}

	public Switch2Elm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		link = new Integer(st.nextToken()).intValue();
		setNoDiagonal(true);
	}

	public void calculateCurrent() {
		if (position == 2)
			setCurrent(0);
	}
	public void draw(Graphics g) {
		setBbox(getPoint1(), getPoint2(), openhs);

		// draw first lead
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, getPoint1(), getLead1());

		// draw second lead
		setVoltageColor(g, getVolts()[1]);
		GraphicsUtil.drawThickLine(g, swpoles[0], swposts[0]);

		// draw third lead
		setVoltageColor(g, getVolts()[2]);
		GraphicsUtil.drawThickLine(g, swpoles[1], swposts[1]);

		// draw switch
		if (!needsHighlight())
			g.setColor(whiteColor);
		GraphicsUtil.drawThickLine(g, getLead1(), swpoles[position]);

		updateDotCount();
		drawDots(g, getPoint1(), getLead1(), getCurcount());
		if (position != 2)
			drawDots(g, swpoles[position], swposts[position], getCurcount());
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + link;
	}

	public boolean getConnection(int n1, int n2) {
		if (position == 2)
			return false;
		return comparePair(n1, n2, 0, 1 + position);
	}

	public int getDumpType() {
		return 'S';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 1) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Center Off", hasCenterOff());
			return ei;
		}
		return super.getEditInfo(n);
	}

	public void getInfo(String arr[]) {
		arr[0] = (link == 0) ? "switch (SPDT)" : "switch (DPDT)";
		arr[1] = "I = " + getCurrentDText(getCurrent());
	}

	public Point getPost(int n) {
		return (n == 0) ? getPoint1() : swposts[n - 1];
	}

	public int getPostCount() {
		return 3;
	}

	public int getShortcut() {
		return 'S';
	}

	public int getVoltageSourceCount() {
		return (position == 2) ? 0 : 1;
	}

	boolean hasCenterOff() {
		return (getFlags() & FLAG_CENTER_OFF) != 0;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 1) {
			setFlags(getFlags() & ~FLAG_CENTER_OFF);
			if (ei.checkbox.getState())
				setFlags(getFlags() | FLAG_CENTER_OFF);
			if (hasCenterOff())
				setMomentary(false);
			setPoints();
		} else
			super.setEditValue(n, ei);
	}

	public void setPoints() {
		super.setPoints();
		calcLeads(32);
		swposts = newPointArray(2);
		swpoles = newPointArray(3);
		interpPoint2(getLead1(), getLead2(), swpoles[0], swpoles[1], 1, openhs);
		swpoles[2] = getLead2();
		interpPoint2(getPoint1(), getPoint2(), swposts[0], swposts[1], 1, openhs);
		posCount = hasCenterOff() ? 3 : 2;
	}

	public void stamp() {
		if (position == 2) // in center?
			return;
		sim.stampVoltageSource(getNodes()[0], getNodes()[position + 1], getVoltSource(), 0);
	}

	public void toggle() {
		super.toggle();
		if (link != 0) {
			int i;
			for (i = 0; i != sim.getElmList().size(); i++) {
				Object o = sim.getElmList().elementAt(i);
				if (o instanceof Switch2Elm) {
					Switch2Elm s2 = (Switch2Elm) o;
					if (s2.link == link)
						s2.position = position;
				}
			}
		}
	}
}
