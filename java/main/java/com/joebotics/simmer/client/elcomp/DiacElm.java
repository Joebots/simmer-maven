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

import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.StringTokenizer;


// stub implementation of DiacElm, based on SparkGapElm
// FIXME need to add DiacElm.java to srclist
// FIXME need to uncomment DiacElm line from Simmer.java

//import java.awt.*;
//import java.util.StringTokenizer;

public class DiacElm extends AbstractCircuitElement {
	double onresistance, offresistance, breakdown, holdcurrent;
	Point ps3, ps4;

	boolean state;

	public DiacElm(int xx, int yy) {
		super(xx, yy);
		// FIXME need to adjust defaults to make sense for diac
		offresistance = 1e9;
		onresistance = 1e3;
		breakdown = 1e3;
		holdcurrent = 0.001;
		state = false;
	}

	public DiacElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		onresistance = new Double(st.nextToken()).doubleValue();
		offresistance = new Double(st.nextToken()).doubleValue();
		breakdown = new Double(st.nextToken()).doubleValue();
		holdcurrent = new Double(st.nextToken()).doubleValue();
	}

	public void calculateCurrent() {
		double vd = getVolts()[0] - getVolts()[1];
		if (state)
			setCurrent(vd / onresistance);
		else
			setCurrent(vd / offresistance);
	}

	public void doStep() {
		if (state)
			sim.stampResistor(getNodes()[0], getNodes()[1], onresistance);
		else
			sim.stampResistor(getNodes()[0], getNodes()[1], offresistance);
	}

	public void draw(Graphics g) {
		// FIXME need to draw Diac
//		int i;
//		double v1 = getVolts()[0];
//		double v2 = getVolts()[1];
		setBbox(getPoint1(), getPoint2(), 6);
		draw2Leads(g);
		setPowerColor(g, true);
		doDots(g);
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + onresistance + " " + offresistance + " "
				+ breakdown + " " + holdcurrent;
	}

	public int getDumpType() {
		return 203;
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("On resistance (ohms)", onresistance, 0, 0);
		if (n == 1)
			return new EditInfo("Off resistance (ohms)", offresistance, 0, 0);
		if (n == 2)
			return new EditInfo("Breakdown voltage (volts)", breakdown, 0, 0);
		if (n == 3)
			return new EditInfo("Hold current (amps)", holdcurrent, 0, 0);
		return null;
	}

	public void getInfo(String arr[]) {
		// FIXME
		arr[0] = "spark gap";
		getBasicInfo(arr);
		arr[3] = state ? "on" : "off";
		arr[4] = "Ron = " + getUnitText(onresistance, Simmer.ohmString);
		arr[5] = "Roff = " + getUnitText(offresistance, Simmer.ohmString);
		arr[6] = "Vbrkdn = " + getUnitText(breakdown, "V");
		arr[7] = "Ihold = " + getUnitText(holdcurrent, "A");
	}

	public boolean nonLinear() {
		return true;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (ei.value > 0 && n == 0)
			onresistance = ei.value;
		if (ei.value > 0 && n == 1)
			offresistance = ei.value;
		if (ei.value > 0 && n == 2)
			breakdown = ei.value;
		if (ei.value > 0 && n == 3)
			holdcurrent = ei.value;
	}

	public void setPoints() {
		super.setPoints();
		calcLeads(32);
		ps3 = new Point();
		ps4 = new Point();
	}

	public void stamp() {
		sim.stampNonLinear(getNodes()[0]);
		sim.stampNonLinear(getNodes()[1]);
	}

	public void startIteration() {
		double vd = getVolts()[0] - getVolts()[1];
		if (Math.abs(getCurrent()) < holdcurrent)
			state = false;
		if (Math.abs(vd) > breakdown)
			state = true;
		// System.out.print(this + " res current set to " + current + "\n");
	}
}
