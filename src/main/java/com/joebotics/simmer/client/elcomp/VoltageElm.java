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

import com.joebotics.simmer.client.gui.widget.Choice;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;

public class VoltageElm extends AbstractCircuitElement {
	static final int FLAG_COS = 2;
	static final int WF_AC = 1;
	static final int WF_DC = 0;
	static final int WF_PULSE = 5;
	static final int WF_SAWTOOTH = 4;
	static final int WF_SQUARE = 2;
	static final int WF_TRIANGLE = 3;
	static final int WF_VAR = 6;
	final int circleSize = 17;
	double frequency, maxVoltage, freqTimeZero, bias, phaseShift, dutyCycle;

	int waveform;

	VoltageElm(int xx, int yy, int wf) {
		super(xx, yy);
		waveform = wf;
		maxVoltage = 5;
		frequency = 40;
		dutyCycle = .5;
		reset();
	}

	public VoltageElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		maxVoltage = 5;
		frequency = 40;
		waveform = WF_DC;
		dutyCycle = .5;
		try {
			waveform = new Integer(st.nextToken()).intValue();
			frequency = new Double(st.nextToken()).doubleValue();
			maxVoltage = new Double(st.nextToken()).doubleValue();
			bias = new Double(st.nextToken()).doubleValue();
			phaseShift = new Double(st.nextToken()).doubleValue();
			dutyCycle = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
		}
		if ((getFlags() & FLAG_COS) != 0) {
			setFlags(getFlags() & ~FLAG_COS);
			phaseShift = pi / 2;
		}
		reset();
	}

	public void doStep() {
		if (waveform != WF_DC)
			sim.updateVoltageSource(getNodes()[0], getNodes()[1], getVoltSource(),
					getVoltage());
	}

	/*
	 * void setCurrent(double c) { current = c;
	 * System.out.print("v current set to " + c + "\n"); }
	 */

	public void draw(Graphics g) {
		setBbox(getX1(), getY1(), getX2(), getY2());
		draw2Leads(g);
		if (waveform == WF_DC) {
			setPowerColor(g, false);
			setVoltageColor(g, getVolts()[0]);
			interpPoint2(getLead1(), getLead2(), ps1, ps2, 0, 10);
			GraphicsUtil.drawThickLine(g, ps1, ps2);
			setVoltageColor(g, getVolts()[1]);
			int hs = 16;
			setBbox(getPoint1(), getPoint2(), hs);
			interpPoint2(getLead1(), getLead2(), ps1, ps2, 1, hs);
			GraphicsUtil.drawThickLine(g, ps1, ps2);
		} else {
			setBbox(getPoint1(), getPoint2(), circleSize);
			interpPoint(getLead1(), getLead2(), ps1, .5);
			drawWaveform(g, ps1);
		}
		updateDotCount();
		if (sim.getDragElm() != this) {
			if (waveform == WF_DC)
				drawDots(g, getPoint1(), getPoint2(), getCurcount());
			else {
				drawDots(g, getPoint1(), getLead1(), getCurcount());
				drawDots(g, getPoint2(), getLead2(), -getCurcount());
			}
		}
		drawPosts(g);
	}

	void drawWaveform(Graphics g, Point center) {
		g.setColor(needsHighlight() ? selectColor : Color.gray);
		setPowerColor(g, false);
		int xc = center.getX();
		int yc = center.getY();
		GraphicsUtil.drawThickCircle(g, xc, yc, circleSize);
		int wl = 8;
		adjustBbox(xc - circleSize, yc - circleSize, xc + circleSize, yc
				+ circleSize);
		int xc2;
		switch (waveform) {
		case WF_DC: {
			break;
		}
		case WF_SQUARE:
			xc2 = (int) (wl * 2 * dutyCycle - wl + xc);
			xc2 = max(xc - wl + 3, min(xc + wl - 3, xc2));
			GraphicsUtil.drawThickLine(g, xc - wl, yc - wl, xc - wl, yc);
			GraphicsUtil.drawThickLine(g, xc - wl, yc - wl, xc2, yc - wl);
			GraphicsUtil.drawThickLine(g, xc2, yc - wl, xc2, yc + wl);
			GraphicsUtil.drawThickLine(g, xc + wl, yc + wl, xc2, yc + wl);
			GraphicsUtil.drawThickLine(g, xc + wl, yc, xc + wl, yc + wl);
			break;
		case WF_PULSE:
			yc += wl / 2;
			GraphicsUtil.drawThickLine(g, xc - wl, yc - wl, xc - wl, yc);
			GraphicsUtil.drawThickLine(g, xc - wl, yc - wl, xc - wl / 2, yc - wl);
			GraphicsUtil.drawThickLine(g, xc - wl / 2, yc - wl, xc - wl / 2, yc);
			GraphicsUtil.drawThickLine(g, xc - wl / 2, yc, xc + wl, yc);
			break;
		case WF_SAWTOOTH:
			GraphicsUtil.drawThickLine(g, xc, yc - wl, xc - wl, yc);
			GraphicsUtil.drawThickLine(g, xc, yc - wl, xc, yc + wl);
			GraphicsUtil.drawThickLine(g, xc, yc + wl, xc + wl, yc);
			break;
		case WF_TRIANGLE: {
			int xl = 5;
			GraphicsUtil.drawThickLine(g, xc - xl * 2, yc, xc - xl, yc - wl);
			GraphicsUtil.drawThickLine(g, xc - xl, yc - wl, xc, yc);
			GraphicsUtil.drawThickLine(g, xc, yc, xc + xl, yc + wl);
			GraphicsUtil.drawThickLine(g, xc + xl, yc + wl, xc + xl * 2, yc);
			break;
		}
		case WF_AC: {
			int i;
			int xl = 10;
			int ox = -1, oy = -1;
			for (i = -xl; i <= xl; i++) {
				int yy = yc + (int) (.95 * Math.sin(i * pi / xl) * wl);
				if (ox != -1)
					GraphicsUtil.drawThickLine(g, ox, oy, xc + i, yy);
				ox = xc + i;
				oy = yy;
			}
			break;
		}
		}
		if (sim.getMainMenuBar().getOptionsMenuBar().getShowValuesCheckItem().getState()) {
			String s = getShortUnitText(frequency, "Hz");
			if (getDx() == 0 || getDy() == 0)
				drawValues(g, s, circleSize);
		}
	}

	public String dump() {
		return super.dump() + " " + waveform + " " + frequency + " "
				+ maxVoltage + " " + bias + " " + phaseShift + " " + dutyCycle;
	}

	public int getDumpType() {
		return 'v';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo(waveform == WF_DC ? "Voltage" : "Max Voltage",
					maxVoltage, -20, 20);
		if (n == 1) {
			EditInfo ei = new EditInfo("Waveform", waveform, -1, -1);
			ei.choice = new Choice();
			ei.choice.add("D/C");
			ei.choice.add("A/C");
			ei.choice.add("Square Wave");
			ei.choice.add("Triangle");
			ei.choice.add("Sawtooth");
			ei.choice.add("Pulse");
			ei.choice.select(waveform);
			return ei;
		}
		if (waveform == WF_DC)
			return null;
		if (n == 2)
			return new EditInfo("Frequency (Hz)", frequency, 4, 500);
		if (n == 3)
			return new EditInfo("DC Offset (V)", bias, -20, 20);
		if (n == 4)
			return new EditInfo("Phase Offset (degrees)",
					phaseShift * 180 / pi, -180, 180).setDimensionless();
		if (n == 5 && waveform == WF_SQUARE)
			return new EditInfo("Duty Cycle", dutyCycle * 100, 0, 100)
					.setDimensionless();
		return null;
	}

	public void getInfo(String arr[]) {
		switch (waveform) {
		case WF_DC:
		case WF_VAR:
			arr[0] = "voltage source";
			break;
		case WF_AC:
			arr[0] = "A/C source";
			break;
		case WF_SQUARE:
			arr[0] = "square wave gen";
			break;
		case WF_PULSE:
			arr[0] = "pulse gen";
			break;
		case WF_SAWTOOTH:
			arr[0] = "sawtooth gen";
			break;
		case WF_TRIANGLE:
			arr[0] = "triangle gen";
			break;
		}
		arr[1] = "I = " + getCurrentText(getCurrent());
		// arr[2] = ((this instanceof RailElm) ? "V = " : "Vd = ") +
		// getVoltageText(getVoltageDiff());
		if (waveform != WF_DC && waveform != WF_VAR) {
			arr[3] = "f = " + getUnitText(frequency, "Hz");
			arr[4] = "Vmax = " + getVoltageText(maxVoltage);
			int i = 5;
			if (bias != 0)
				arr[i++] = "Voff = " + getVoltageText(bias);
			else if (frequency > 500)
				arr[i++] = "wavelength = "
						+ getUnitText(2.9979e8 / frequency, "m");
			arr[i++] = "P = " + getUnitText(getPower(), "W");
		}
	}

	public double getPower() {
		return -getVoltageDiff() * getCurrent();
	}

	double getVoltage() {
		double w = 2 * pi * (sim.getT() - freqTimeZero) * frequency + phaseShift;
		switch (waveform) {
		case WF_DC:
			return maxVoltage + bias;
		case WF_AC:
			return Math.sin(w) * maxVoltage + bias;
		case WF_SQUARE:
			return bias
					+ ((w % (2 * pi) > (2 * pi * dutyCycle)) ? -maxVoltage
							: maxVoltage);
		case WF_TRIANGLE:
			return bias + triangleFunc(w % (2 * pi)) * maxVoltage;
		case WF_SAWTOOTH:
			return bias + (w % (2 * pi)) * (maxVoltage / pi) - maxVoltage;
		case WF_PULSE:
			return ((w % (2 * pi)) < 1) ? maxVoltage + bias : bias;
		default:
			return 0;
		}
	}

	public double getVoltageDiff() {
		return getVolts()[1] - getVolts()[0];
	}

	public int getVoltageSourceCount() {
		return 1;
	}

	public void reset() {
		freqTimeZero = 0;
		setCurcount(0);
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			maxVoltage = ei.value;
		if (n == 3)
			bias = ei.value;
		if (n == 2) {
			// adjust time zero to maintain continuity ind the waveform
			// even though the frequency has changed.
			double oldfreq = frequency;
			frequency = ei.value;
			double maxfreq = 1 / (8 * sim.getTimeStep());
			if (frequency > maxfreq)
				frequency = maxfreq;
//			double adj = frequency - oldfreq;
			freqTimeZero = sim.getT() - oldfreq * (sim.getT() - freqTimeZero) / frequency;
		}
		if (n == 1) {
			int ow = waveform;
			waveform = ei.choice.getSelectedIndex();
			if (waveform == WF_DC && ow != WF_DC) {
				ei.newDialog = true;
				bias = 0;
			} else if (waveform != WF_DC && ow == WF_DC) {
				ei.newDialog = true;
			}
			if ((waveform == WF_SQUARE || ow == WF_SQUARE) && waveform != ow)
				ei.newDialog = true;
			setPoints();
		}
		if (n == 4)
			phaseShift = ei.value * pi / 180;
		if (n == 5)
			dutyCycle = ei.value * .01;
	}

	public void setPoints() {
		super.setPoints();
		calcLeads((waveform == WF_DC || waveform == WF_VAR) ? 8
				: circleSize * 2);
	}

	public void stamp() {
		if (waveform == WF_DC)
			sim.stampVoltageSource(getNodes()[0], getNodes()[1], getVoltSource(), getVoltage());
		else
			sim.stampVoltageSource(getNodes()[0], getNodes()[1], getVoltSource());
	}

	double triangleFunc(double x) {
		if (x < pi)
			return x * (2 / pi) - 1;
		return 1 - (x - pi) * (2 / pi);
	}
}
