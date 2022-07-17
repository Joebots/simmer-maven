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
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class AnalogSwitch2Elm extends AnalogSwitchElm {
	private final int openhs = 16;

	private Point swposts[], swpoles[], ctlPoint;

	public AnalogSwitch2Elm(int xx, int yy) {
		super(xx, yy);
	}
	public AnalogSwitch2Elm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	public void calculateCurrent() {
		if (isOpen())
			setCurrent((getVolts()[0] - getVolts()[2]) / getR_on());
		else
			setCurrent((getVolts()[0] - getVolts()[1]) / getR_on());
	}

	public void doStep() {
		setOpen((getVolts()[3] < Pin.VOLTAGE_THRESHOLD_LEVEL));
		if ((getFlags() & FLAG_INVERT) != 0)
			setOpen(!isOpen());
		if (isOpen()) {
			sim.stampResistor(getNodes()[0], getNodes()[2], getR_on());
			sim.stampResistor(getNodes()[0], getNodes()[1], getR_off());
		} else {
			sim.stampResistor(getNodes()[0], getNodes()[1], getR_on());
			sim.stampResistor(getNodes()[0], getNodes()[2], getR_off());
		}
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
		g.setColor(lightGrayColor);
		int position = (isOpen()) ? 1 : 0;
		GraphicsUtil.drawThickLine(g, getLead1(), swpoles[position]);

		updateDotCount();
		drawDots(g, getPoint1(), getLead1(), getCurcount());
		drawDots(g, swpoles[position], swposts[position], getCurcount());
		drawPosts(g);
	}

	public boolean getConnection(int n1, int n2) {
		if (n1 == 3 || n2 == 3)
			return false;
		return true;
	}

	public int getDumpType() {
		return 160;
	}

	public void getInfo(String arr[]) {
		arr[0] = "analog switch (SPDT)";
		arr[1] = "I = " + getCurrentDText(getCurrent());
	}

	public int getPostCount() {
		return 4;
	}

	public void setPoints() {
		super.setPoints();
		calcLeads(32);
		swposts = newPointArray(2);
		swpoles = newPointArray(2);
		interpPoint2(getLead1(), getLead2(), swpoles[0], swpoles[1], 1, openhs);
		interpPoint2(getPoint1(), getPoint2(), swposts[0], swposts[1], 1, openhs);
		ctlPoint = interpPoint(getPoint1(), getPoint2(), .5, openhs);
		
		getPins()[1].setPost(swpoles[0]);
		getPins()[2].setPost(swposts[0]);
		getPins()[3].setPost(ctlPoint);
	}

	public void stamp() {
		sim.stampNonLinear(getNodes()[0]);
		sim.stampNonLinear(getNodes()[1]);
		sim.stampNonLinear(getNodes()[2]);
	}
}
