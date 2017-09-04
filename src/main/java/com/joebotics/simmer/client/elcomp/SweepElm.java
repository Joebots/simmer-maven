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
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.OptionKey;
import com.joebotics.simmer.client.util.StringTokenizer;

import gwt.material.design.client.ui.MaterialCheckBox;


//import java.awt.*;
//import java.util.StringTokenizer;

public class SweepElm extends AbstractCircuitElement {
	final int circleSize = 17;
	int dir = 1;
	double fadd, fmul, freqTime, savedTimeStep;

	final int FLAG_BIDIR = 2;

	final int FLAG_LOG = 1;

	double maxV, maxF, minF, sweepTime, frequency;

	double v;

	public SweepElm(int xx, int yy) {
		super(xx, yy);
		minF = 20;
		maxF = 4000;
		maxV = 5;
		sweepTime = .1;
		setFlags(FLAG_BIDIR);
		reset();
	}

	public SweepElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		minF = new Double(st.nextToken()).doubleValue();
		maxF = new Double(st.nextToken()).doubleValue();
		maxV = new Double(st.nextToken()).doubleValue();
		sweepTime = new Double(st.nextToken()).doubleValue();
		reset();
	}

	public void doStep() {
		sim.updateVoltageSource(0, getNodes()[0], getVoltSource(), v);
	}

	public void draw(Graphics g) {
		setBbox(getPoint1(), getPoint2(), circleSize);
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, getPoint1(), getLead1());
		g.setColor(needsHighlight() ? selectColor : Color.gray);
		setPowerColor(g, false);
		int xc = getPoint2().getX();
		int yc = getPoint2().getY();
		GraphicsUtil.drawThickCircle(g, xc, yc, circleSize);
		int wl = 8;
		adjustBbox(xc - circleSize, yc - circleSize, xc + circleSize, yc
				+ circleSize);
		int i;
		int xl = 10;
		int ox = -1, oy = -1;
		long tm = System.currentTimeMillis();
		// double w = (this == mouseElm ? 3 : 2);
		tm %= 2000;
		if (tm > 1000)
			tm = 2000 - tm;
		double w = 1 + tm * .002;
		if (!sim.getSidePanel().getStoppedCheck().getState())
			w = 1 + 2 * (frequency - minF) / (maxF - minF);
		for (i = -xl; i <= xl; i++) {
			int yy = yc + (int) (.95 * Math.sin(i * pi * w / xl) * wl);
			if (ox != -1)
				GraphicsUtil.drawThickLine(g, ox, oy, xc + i, yy);
			ox = xc + i;
			oy = yy;
		}
		if (sim.getOptions().getBoolean(OptionKey.SHOW_VALUES)) {
			String s = getShortUnitText(frequency, "Hz");
			if (getDx() == 0 || getDy() == 0)
				drawValues(g, s, circleSize);
		}

		drawPosts(g);
		setCurcount(updateDotCount(-getCurrent(), getCurcount()));
		if (sim.getDragElm() != this)
			drawDots(g, getPoint1(), getLead1(), getCurcount());
	}

	public String dump() {
		return super.dump() + " " + minF + " " + maxF + " " + maxV + " "
				+ sweepTime;
	}

	public int getDumpType() {
		return 170;
	}
	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Min Frequency (Hz)", minF, 0, 0);
		if (n == 1)
			return new EditInfo("Max Frequency (Hz)", maxF, 0, 0);
		if (n == 2)
			return new EditInfo("Sweep Time (s)", sweepTime, 0, 0);
		if (n == 3) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Logarithmic");
            ei.checkbox.setValue((getFlags() & FLAG_LOG) != 0);
			return ei;
		}
		if (n == 4)
			return new EditInfo("Max Voltage", maxV, 0, 0);
		if (n == 5) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Bidirectional");
            ei.checkbox.setValue((getFlags() & FLAG_BIDIR) != 0);
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "sweep " + (((getFlags() & FLAG_LOG) == 0) ? "(linear)" : "(log)");
		arr[1] = "I = " + getCurrentDText(getCurrent());
		arr[2] = "V = " + getVoltageText(getVolts()[0]);
		arr[3] = "f = " + getUnitText(frequency, "Hz");
		arr[4] = "range = " + getUnitText(minF, "Hz") + " .. "
				+ getUnitText(maxF, "Hz");
		arr[5] = "time = " + getUnitText(sweepTime, "s");
	}

	public int getPostCount() {
		return 1;
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
		frequency = minF;
		freqTime = 0;
		dir = 1;
		setParams();
	}

	public void setEditValue(int n, EditInfo ei) {
		double maxfreq = 1 / (8 * sim.getTimeStep());
		if (n == 0) {
			minF = ei.value;
			if (minF > maxfreq)
				minF = maxfreq;
		}
		if (n == 1) {
			maxF = ei.value;
			if (maxF > maxfreq)
				maxF = maxfreq;
		}
		if (n == 2)
			sweepTime = ei.value;
		if (n == 3) {
			setFlags(getFlags() & ~FLAG_LOG);
			if (ei.checkbox.getValue())
				setFlags(getFlags() | FLAG_LOG);
		}
		if (n == 4)
			maxV = ei.value;
		if (n == 5) {
			setFlags(getFlags() & ~FLAG_BIDIR);
			if (ei.checkbox.getValue())
				setFlags(getFlags() | FLAG_BIDIR);
		}
		setParams();
	}

	void setParams() {
		if (frequency < minF || frequency > maxF) {
			frequency = minF;
			freqTime = 0;
			dir = 1;
		}
		if ((getFlags() & FLAG_LOG) == 0) {
			fadd = dir * sim.getTimeStep() * (maxF - minF) / sweepTime;
			fmul = 1;
		} else {
			fadd = 0;
			fmul = Math.pow(maxF / minF, dir * sim.getTimeStep() / sweepTime);
		}
		savedTimeStep = sim.getTimeStep();
	}

	public void setPoints() {
		super.setPoints();
		setLead1(interpPoint(getPoint1(), getPoint2(), 1 - circleSize / getDn()));
	}

	public void stamp() {
		sim.stampVoltageSource(0, getNodes()[0], getVoltSource());
	}

	public void startIteration() {
		// has timestep been changed?
		if (sim.getTimeStep() != savedTimeStep)
			setParams();
		v = Math.sin(freqTime) * maxV;
		freqTime += frequency * 2 * pi * sim.getTimeStep();
		frequency = frequency * fmul + fadd;
		if (frequency >= maxF && dir == 1) {
			if ((getFlags() & FLAG_BIDIR) != 0) {
				fadd = -fadd;
				fmul = 1 / fmul;
				dir = -1;
			} else
				frequency = minF;
		}
		if (frequency <= minF && dir == -1) {
			fadd = -fadd;
			fmul = 1 / fmul;
			dir = 1;
		}
	}
}
