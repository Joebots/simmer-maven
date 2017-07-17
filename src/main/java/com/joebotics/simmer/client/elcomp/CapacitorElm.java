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
import com.joebotics.simmer.client.gui.widget.Choice;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;

public class CapacitorElm extends AbstractCircuitElement {
	public static final int FLAG_BACK_EULER = 2;
	private double capacitance;
	double compResistance, voltdiff;
	double curSourceValue;

	Point plate1[], plate2[];
	private CapacitorType type = CapacitorType.FIXED;

	public CapacitorElm(int xx, int yy) {
		super(xx, yy);
		capacitance = 1e-5;
	}

	public CapacitorElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		capacitance = new Double(st.nextToken()).doubleValue();
		voltdiff = new Double(st.nextToken()).doubleValue();
		if (st.hasMoreElements()) {
			type = CapacitorType.values()[(Integer.valueOf(st.nextToken()))];
		}
	}

	public void calculateCurrent() {
		double voltdiff = getVolts()[0] - getVolts()[1];
		// we check compResistance because this might get called
		// before stamp(), which sets compResistance, causing
		// infinite current
		if (compResistance > 0)
			setCurrent(voltdiff / compResistance + curSourceValue);
	}

	public void doStep() {
		sim.stampCurrentSource(getNodes()[0], getNodes()[1], curSourceValue);
	}

	public void draw(Graphics g) {
		int hs = 12;
		setBbox(getPoint1(), getPoint2(), hs);

		// draw first lead and plate
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, getPoint1(), getLead1());
		setPowerColor(g, false);
		GraphicsUtil.drawThickLine(g, plate1[0], plate1[1]);
		if (sim.getMainMenuBar().getOptionsMenuBar().getPowerCheckItem().getState())
			g.setColor(Color.gray);

		// draw second lead and plate
		setVoltageColor(g, getVolts()[1]);
		GraphicsUtil.drawThickLine(g, getPoint2(), getLead2());
		setPowerColor(g, false);
		if (type == CapacitorType.POLARIZED) {
			GraphicsUtil.drawThickArc(g, plate2[0], plate2[1], plate2[2], 22);
		} else {
			GraphicsUtil.drawThickLine(g, plate2[0], plate2[2]);
		}

		updateDotCount();
		if (sim.getDragElm() != this) {
			drawDots(g, getPoint1(), getLead1(), getCurcount());
			drawDots(g, getPoint2(), getLead2(), -getCurcount());
		}
		drawPosts(g);
		if (sim.getMainMenuBar().getOptionsMenuBar().getShowValuesCheckItem().getState()) {
			String s = getShortUnitText(capacitance, "F");
			drawValues(g, s, hs);
		}
	}

	public String dump() {
		return super.dump() + " " + capacitance + " " + voltdiff + " " + type.ordinal();
	}

	public int getDumpType() {
		return 'c';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Capacitance (F)", capacitance, 0, 0);
		if (n == 1) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.choice = new Choice();
			for (CapacitorType value : CapacitorType.values()) {
				ei.choice.add(value.getTitle());
			}
			ei.choice.select(type.ordinal());
			
			ei.checkbox = new Checkbox("Trapezoidal Approximation",
					isTrapezoidal());
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = type.getTitle();
		getBasicInfo(arr);
		arr[3] = "C = " + getUnitText(capacitance, "F");
		arr[4] = "P = " + getUnitText(getPower(), "W");
		// double v = getVoltageDiff();
		// arr[4] = "U = " + getUnitText(.5*capacitance*v*v, "J");
	}

	public int getShortcut() {
		return 'c';
	}

	boolean isTrapezoidal() {
		return (getFlags() & FLAG_BACK_EULER) == 0;
	}

	public void reset() {
		setCurrent(setCurcount(0));
		// put small charge on caps when reset to start oscillators
		voltdiff = 1e-3;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0 && ei.value > 0)
			capacitance = ei.value;
		if (n == 1) {
			type = CapacitorType.values()[ei.choice.getSelectedIndex()];
			if (ei.checkbox.getState())
				setFlags(getFlags() & ~FLAG_BACK_EULER);
			else
				setFlags(getFlags() | FLAG_BACK_EULER);
		}
		setPoints();
	}

	public void setNodeVoltage(int n, double c) {
		super.setNodeVoltage(n, c);
		voltdiff = getVolts()[0] - getVolts()[1];
	}

	public void setPoints() {
		super.setPoints();
		double f = (getDn() / 2 - 4) / getDn();
		// calc leads
		Point lead1 = interpPoint(getPoint1(), getPoint2(), f);
		setLead1(lead1);
		Point lead2 = interpPoint(getPoint1(), getPoint2(), 1 - f);
		setLead2(lead2);
		// calc plates
		plate1 = newPointArray(2);
		plate2 = newPointArray(3);
		interpPoint2(getPoint1(), getPoint2(), plate1[0], plate1[1], f, 12);
		interpPoint(getPoint1(), getPoint2(), plate2[1], f, 0);
		interpPoint2(getPoint1(), getPoint2(), plate2[0], plate2[2], 1 - f, 12);
		if (type == CapacitorType.POLARIZED) {
			getPins()[0].setText("+");
			getPins()[0].setDescription("anode");
			getPins()[0].setText("-");
			getPins()[1].setDescription("cathode");
		}
	}

	public void stamp() {
		// capacitor companion model using trapezoidal approximation
		// (Norton equivalent) consists of a current source in
		// parallel with a resistor. Trapezoidal is more accurate
		// than backward euler but can cause oscillatory behavior
		// if RC is small relative to the timestep.
		if (isTrapezoidal())
			compResistance = sim.getTimeStep() / (2 * capacitance);
		else
			compResistance = sim.getTimeStep() / capacitance;
		sim.stampResistor(getNodes()[0], getNodes()[1], compResistance);
		sim.stampRightSide(getNodes()[0]);
		sim.stampRightSide(getNodes()[1]);
	}

	public void startIteration() {
		if (isTrapezoidal())
			curSourceValue = -voltdiff / compResistance - getCurrent();
		else
			curSourceValue = -voltdiff / compResistance;
		// System.out.println("cap " + compResistance + " " + curSourceValue +
		// " " + current + " " + voltdiff);
	}

	public double getCapacitance() {
		return capacitance;
	}

	public void setCapacitance(double capacitance) {
		this.capacitance = capacitance;
	}
	
	private static enum CapacitorType {
		FIXED ("Fixed Capacitor"),
		POLARIZED ("Polarized Capacitor");
		// TODO be supported later
		//VARIABLE ("Variable Capacitor")
		
		private String title;
		
		private CapacitorType(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}
	}
}
