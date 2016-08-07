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

import com.joebotics.simmer.client.elcomp.GraphicElm;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class BoxElm extends GraphicElm {

	public BoxElm(int xx, int yy) {
		super(xx, yy);
		setX2(xx + 16);
		setY2(yy + 16);
		setBbox(getX1(), getY1(), getX2(), getY2());
	}

	public BoxElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		setX2(xb);
		setY2(yb);
		/*
		 * if ( st.hasMoreTokens() ) x = new Integer(st.nextToken()).intValue();
		 * if ( st.hasMoreTokens() ) y = new Integer(st.nextToken()).intValue();
		 * if ( st.hasMoreTokens() ) x2 = new
		 * Integer(st.nextToken()).intValue(); if ( st.hasMoreTokens() ) y2 =
		 * new Integer(st.nextToken()).intValue();
		 */
		setBbox(getX1(), getY1(), getX2(), getY2());
	}

	public void drag(int xx, int yy) {
		setX1(xx);
		setY1(yy);
	}

	public void draw(Graphics g) {
		// g.setColor(needsHighlight() ? selectColor : lightGrayColor);
		g.setColor(needsHighlight() ? selectColor : Color.GRAY);
		setBbox(getX1(), getY1(), getX2(), getY2());
		if (getX1() < getX2() && getY1() < getY2())
			g.fillRect(getX1(), getY1(), getX2() - getX1(), getY2() - getY1());
		else if (getX1() > getX2() && getY1() < getY2())
			g.fillRect(getX2(), getY1(), getX1() - getX2(), getY2() - getY1());
		else if (getX1() < getX2() && getY1() > getY2())
			g.fillRect(getX1(), getY2(), getX2() - getX1(), getY1() - getY2());
		else
			g.fillRect(getX2(), getY2(), getX1() - getX2(), getY1() - getY2());
	}

	public String dump() {
		return super.dump();
	}

	public int getDumpType() {
		return 'b';
	}

	public EditInfo getEditInfo(int n) {
		return null;
	}

	public void getInfo(String arr[]) {
	}

	//	@Override
	public int getShortcut() {
		return 0;
	}

public void setEditValue(int n, EditInfo ei) {
	}
}
