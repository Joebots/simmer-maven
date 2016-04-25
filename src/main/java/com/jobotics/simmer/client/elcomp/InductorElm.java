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
import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.util.Inductor;
import com.jobotics.simmer.client.util.StringTokenizer;



//import java.awt.*;
//import java.util.StringTokenizer;

public class InductorElm extends AbstractCircuitElement {
	Inductor ind;
	private double inductance;

	public InductorElm(int xx, int yy) {
		super(xx, yy);
		ind = new Inductor(sim);
		inductance = 1;
		ind.setup(inductance, getCurrent(), getFlags());
	}

	public InductorElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		ind = new Inductor(sim);
		inductance = new Double(st.nextToken()).doubleValue();
		setCurrent(new Double(st.nextToken()).doubleValue());
		ind.setup(inductance, getCurrent(), getFlags());
	}

	public void calculateCurrent() {
		double voltdiff = getVolts()[0] - getVolts()[1];
		setCurrent(ind.calculateCurrent(voltdiff));
	}

	public void doStep() {
		double voltdiff = getVolts()[0] - getVolts()[1];
		ind.doStep(voltdiff);
	}

	public void draw(Graphics g) {
		double v1 = getVolts()[0];
		double v2 = getVolts()[1];
//		int i;
		int hs = 8;
		setBbox(getPoint1(), getPoint2(), hs);
		draw2Leads(g);
		setPowerColor(g, false);
		drawCoil(g, 8, getLead1(), getLead2(), v1, v2);
		if (sim.getShowValuesCheckItem().getState()) {
			String s = getShortUnitText(inductance, "H");
			drawValues(g, s, hs);
		}
		doDots(g);
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + inductance + " " + getCurrent();
	}

	public int getDumpType() {
		return 'l';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Inductance (H)", inductance, 0, 0);
		if (n == 1) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Trapezoidal Approximation",
					ind.isTrapezoidal());
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "inductor";
		getBasicInfo(arr);
		arr[3] = "L = " + getUnitText(inductance, "H");
		arr[4] = "P = " + getUnitText(getPower(), "W");
	}

	public int getShortcut() {
		return 'L';
	}

	public boolean nonLinear() {
		return ind.nonLinear();
	}

	public void reset() {
		setCurrent(getVolts()[0] = getVolts()[1] = setCurcount(0));
		ind.reset();
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			inductance = ei.value;
		if (n == 1) {
			if (ei.checkbox.getState())
				setFlags(getFlags() & ~Inductor.FLAG_BACK_EULER);
			else
				setFlags(getFlags() | Inductor.FLAG_BACK_EULER);
		}
		ind.setup(inductance, getCurrent(), getFlags());
	}

	public void setPoints() {
		super.setPoints();
		calcLeads(32);
	}

	public void stamp() {
		ind.stamp(getNodes()[0], getNodes()[1]);
	}

	public void startIteration() {
		ind.startIteration(getVolts()[0] - getVolts()[1]);
	}

	public double getInductance() {
		return inductance;
	}

	public void setInductance(double inductance) {
		this.inductance = inductance;
	}

}
