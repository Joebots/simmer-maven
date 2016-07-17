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

import com.jobotics.simmer.client.gui.impl.EditInfo;
import com.jobotics.simmer.client.gui.util.Color;
import com.jobotics.simmer.client.gui.util.Font;
import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.gui.util.Point;
import com.jobotics.simmer.client.util.GraphicsUtil;
import com.jobotics.simmer.client.util.StringTokenizer;



//import java.awt.*;
//import java.util.StringTokenizer;

// contributed by Edward Calver

public class FMElm extends AbstractCircuitElement {
	static final int FLAG_COS = 2;
	double carrierfreq, signalfreq, maxVoltage, freqTimeZero, deviation;
	final int circleSize = 17;
	double funcx = 0;

	double lasttime = 0;

	public FMElm(int xx, int yy) {
		super(xx, yy);
		deviation = 200;
		maxVoltage = 5;
		carrierfreq = 800;
		signalfreq = 40;
		reset();
	}

	public FMElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		carrierfreq = new Double(st.nextToken()).doubleValue();
		signalfreq = new Double(st.nextToken()).doubleValue();
		maxVoltage = new Double(st.nextToken()).doubleValue();
		deviation = new Double(st.nextToken()).doubleValue();
		if ((getFlags() & FLAG_COS) != 0) {
			setFlags(getFlags() & ~FLAG_COS);
		}
		reset();
	}

	public void doStep() {
		sim.updateVoltageSource(0, getNodes()[0], getVoltSource(), getVoltage());
	}

	/*
	 * void setCurrent(double c) { current = c;
	 * System.out.print("v current set to " + c + "\n"); }
	 */

	public void draw(Graphics g) {
		setBbox(getPoint1(), getPoint2(), circleSize);
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, getPoint1(), getLead1());

		Font f = new Font("SansSerif", 0, 12);
		g.setFont(f);
		g.setColor(needsHighlight() ? selectColor : whiteColor);
		setPowerColor(g, false);
//		double v = getVoltage();
		String s = "FM";
		drawCenteredText(g, s, getX2(), getY2(), true);
		drawWaveform(g, getPoint2());
		drawPosts(g);
		setCurcount(updateDotCount(-getCurrent(), getCurcount()));
		if (sim.dragElm != this)
			drawDots(g, getPoint1(), getLead1(), getCurcount());
	}

	void drawWaveform(Graphics g, Point center) {
		g.setColor(needsHighlight() ? selectColor : Color.gray);
		setPowerColor(g, false);
		int xc = center.getX();
		int yc = center.getY();
		GraphicsUtil.drawThickCircle(g, xc, yc, circleSize);
//		int wl = 8;
		adjustBbox(xc - circleSize, yc - circleSize, xc + circleSize, yc
				+ circleSize);
	}

	public String dump() {
		return super.dump() + " " + carrierfreq + " " + signalfreq + " "
				+ maxVoltage + " " + deviation;
	}

	public int getDumpType() {
		return 201;
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Max Voltage", maxVoltage, -20, 20);
		if (n == 1)
			return new EditInfo("Carrier Frequency (Hz)", carrierfreq, 4, 500);
		if (n == 2)
			return new EditInfo("Signal Frequency (Hz)", signalfreq, 4, 500);
		if (n == 3)
			return new EditInfo("Deviation (Hz)", deviation, 4, 500);

		return null;
	}

	public void getInfo(String arr[]) {

		arr[0] = "FM Source";
		arr[1] = "I = " + getCurrentText(getCurrent());
		arr[2] = "V = " + getVoltageText(getVoltageDiff());
		arr[3] = "cf = " + getUnitText(carrierfreq, "Hz");
		arr[4] = "sf = " + getUnitText(signalfreq, "Hz");
		arr[5] = "dev =" + getUnitText(deviation, "Hz");
		arr[6] = "Vmax = " + getVoltageText(maxVoltage);
	}

	public int getPostCount() {
		return 1;
	}

	public double getPower() {
		return -getVoltageDiff() * getCurrent();
	}

	double getVoltage() {
		double deltaT = sim.getT() - lasttime;
		lasttime = sim.getT();
		double signalamplitude = Math.sin((2 * pi * (sim.getT() - freqTimeZero))
				* signalfreq);
		funcx += deltaT * (carrierfreq + (signalamplitude * deviation));
		double w = 2 * pi * funcx;
		return Math.sin(w) * maxVoltage;
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

	public void reset() {
		freqTimeZero = 0;
		setCurcount(0);
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			maxVoltage = ei.value;
		if (n == 1)
			carrierfreq = ei.value;
		if (n == 2)
			signalfreq = ei.value;
		if (n == 3)
			deviation = ei.value;
	}

	public void setPoints() {
		super.setPoints();
		setLead1(interpPoint(getPoint1(), getPoint2(), 1 - circleSize / getDn()));
	}

	public void stamp() {
		sim.stampVoltageSource(0, getNodes()[0], getVoltSource());
	}
}
