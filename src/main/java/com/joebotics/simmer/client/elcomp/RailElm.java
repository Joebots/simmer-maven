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

import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


public class RailElm extends VoltageElm {
	final int FLAG_CLOCK = 1;

	public RailElm(int xx, int yy) {
		super(xx, yy, WF_DC);
	}

	RailElm(int xx, int yy, int wf) {
		super(xx, yy, wf);
	}

	public RailElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	public void doStep() {
		if (waveform != WF_DC)
			sim.updateVoltageSource(0, getNodes()[0], getVoltSource(), getVoltage());
	}

	public void draw(Graphics g) {
		String s;
		setBbox(getPoint1(), getPoint2(), circleSize);
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, getPoint1(), getLead1());
		boolean clock = waveform == WF_SQUARE && (getFlags() & FLAG_CLOCK) != 0;
		if (waveform == WF_DC || waveform == WF_VAR || clock) {
			// IES
			// Font f = new Font("SansSerif", 0, 12);
			// g.setFont(f);
			g.setColor(needsHighlight() ? selectColor : whiteColor);
			setPowerColor(g, false);
			double v = getVoltage();
			// String s = getShortUnitText(v, "V");
			if (Math.abs(v) < 1)
				s = showFormat.format(v) + " V";
			else
				s = getShortUnitText(v, "V");
			if (getVoltage() > 0)
				s = "+" + s;
			// ies
			if (this instanceof AntennaElm)
				s = "Ant";
			if (clock)
				s = "CLK";
			drawCenteredText(g, s, getX2(), getY2(), true);
		} else {
			drawWaveform(g, getPoint2());
		}
		drawPosts(g);
		setCurcount(updateDotCount(-getCurrent(), getCurcount()));
		if (sim.dragElm != this)
			drawDots(g, getPoint1(), getLead1(), getCurcount());
	}

	public void drawHandles(Graphics g, Color c) {
		g.setColor(c);
		g.fillRect(getX1() - 3, getY1() - 3, 7, 7);
	}

	public int getDumpType() {
		return 'R';
	}

	public int getPostCount() {
		return 1;
	}

	public int getShortcut() {
		return 'V';
	}

	public double getVoltageDiff() {
		return getVolts()[0];
	}

	public boolean hasGroundConnection(int n1) {
		return true;
	}

	public void setPoints() {
		super.setPoints();
		setLead1(interpPoint(getPoint1(), getPoint2(), 1 - circleSize / getDn()));
	}

	public void stamp() {
		if (waveform == WF_DC)
			sim.stampVoltageSource(0, getNodes()[0], getVoltSource(), getVoltage());
		else
			sim.stampVoltageSource(0, getNodes()[0], getVoltSource());
	}

}
