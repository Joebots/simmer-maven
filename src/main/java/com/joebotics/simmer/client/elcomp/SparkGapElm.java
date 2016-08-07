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
import com.joebotics.simmer.client.gui.util.Polygon;
import com.joebotics.simmer.client.util.StringTokenizer;




//import java.awt.*;
//import java.util.StringTokenizer;

public class SparkGapElm extends AbstractCircuitElement {
	private Polygon arrow1, arrow2;
	private double resistance, onresistance, offresistance, breakdown, holdcurrent;

	private boolean state;

	public SparkGapElm(int xx, int yy) {
		super(xx, yy);
		offresistance = 1e9;
		onresistance = 1e3;
		breakdown = 1e3;
		holdcurrent = 0.001;
		state = false;
	}

	public SparkGapElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		onresistance = new Double(st.nextToken()).doubleValue();
		offresistance = new Double(st.nextToken()).doubleValue();
		breakdown = new Double(st.nextToken()).doubleValue();
		holdcurrent = new Double(st.nextToken()).doubleValue();
	}

	@Override
	public void calculateCurrent() {
		double vd = getVolts()[0] - getVolts()[1];
		setCurrent(vd / resistance);
	}

	public void doStep() {
		resistance = (state) ? onresistance : offresistance;
		sim.stampResistor(getNodes()[0], getNodes()[1], resistance);
	}

	public void draw(Graphics g) {
//		int i;
//		double v1 = getVolts()[0];
//		double v2 = getVolts()[1];
		setBbox(getPoint1(), getPoint2(), 8);
		draw2Leads(g);
		setPowerColor(g, true);
		setVoltageColor(g, getVolts()[0]);
		g.fillPolygon(arrow1);
		setVoltageColor(g, getVolts()[1]);
		g.fillPolygon(arrow2);
		if (state)
			doDots(g);
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + onresistance + " " + offresistance + " "
				+ breakdown + " " + holdcurrent;
	}

	public int getDumpType() {
		return 187;
	}
	
	public EditInfo getEditInfo(int n) {
		// ohmString doesn't work here on linux
		if (n == 0)
			return new EditInfo("On resistance (ohms)", onresistance, 0, 0);
		if (n == 1)
			return new EditInfo("Off resistance (ohms)", offresistance, 0, 0);
		if (n == 2)
			return new EditInfo("Breakdown voltage", breakdown, 0, 0);
		if (n == 3)
			return new EditInfo("Holding current (A)", holdcurrent, 0, 0);
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "spark gap";
		getBasicInfo(arr);
		arr[3] = state ? "on" : "off";
		arr[4] = "Ron = " + getUnitText(onresistance, Simmer.ohmString);
		arr[5] = "Roff = " + getUnitText(offresistance, Simmer.ohmString);
		arr[6] = "Vbreakdown = " + getUnitText(breakdown, "V");
	}

	public boolean nonLinear() {
		return true;
	}

	public void reset() {
		super.reset();
		state = false;
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
		int dist = 16;
		int alen = 8;
		calcLeads(dist + alen);
		Point p1 = interpPoint(getPoint1(), getPoint2(), (getDn() - alen) / (2 * getDn()));
		arrow1 = calcArrow(getPoint1(), p1, alen, alen);
		p1 = interpPoint(getPoint1(), getPoint2(), (getDn() + alen) / (2 * getDn()));
		arrow2 = calcArrow(getPoint2(), p1, alen, alen);
	}

	public void stamp() {
		sim.stampNonLinear(getNodes()[0]);
		sim.stampNonLinear(getNodes()[1]);
	}

	public void startIteration() {
		if (Math.abs(getCurrent()) < holdcurrent)
			state = false;
		double vd = getVolts()[0] - getVolts()[1];
		if (Math.abs(vd) > breakdown)
			state = true;
	}
}
