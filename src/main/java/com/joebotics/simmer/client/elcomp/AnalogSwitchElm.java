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
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;

public class AnalogSwitchElm extends AbstractCircuitElement {
	public static final int FLAG_INVERT = 1;
	private Point lead3;
	private boolean open;
	private Point point3;
	private Point ps;
	private double r_off;
	private double r_on;
	private double resistance;

	public AnalogSwitchElm(int xx, int yy) {
		super(xx, yy);
		r_on = 20;
		r_off = 1e10;
	}

	public AnalogSwitchElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		r_on = 20;
		r_off = 1e10;
		try {
			r_on = new Double(st.nextToken()).doubleValue();
			r_off = new Double(st.nextToken()).doubleValue();
		} catch (Exception e) {
		}

	}

	public void calculateCurrent() {
		setCurrent((getVolts()[0] - getVolts()[1]) / resistance);
	}

	public void doStep() {
		open = (getVolts()[2] < 2.5);
		if ((getFlags() & FLAG_INVERT) != 0)
			open = !open;
		resistance = (open) ? r_off : r_on;
		sim.stampResistor(getNodes()[0], getNodes()[1], resistance);
	}

	public void drag(int xx, int yy) {
		xx = sim.getSimmerController().snapGrid(xx);
		yy = sim.getSimmerController().snapGrid(yy);
		if (abs(getX1() - xx) < abs(getY1() - yy))
			xx = getX1();
		else
			yy = getY1();
		int q1 = abs(getX1() - xx) + abs(getY1() - yy);
		int q2 = (q1 / 2) % sim.getGridSize();
		if (q2 != 0)
			return;
		setX2(xx);
		setY2(yy);
		setPoints();
	}

	public void draw(Graphics g) {
		int openhs = 16;
		int hs = (open) ? openhs : 0;
		setBbox(getPoint1(), getPoint2(), openhs);

		draw2Leads(g);

		g.setColor(lightGrayColor);
		interpPoint(getLead1(), getLead2(), ps, 1, hs);
		GraphicsUtil.drawThickLine(g, getLead1(), ps);

		setVoltageColor(g, getVolts()[2]);
		GraphicsUtil.drawThickLine(g, point3, lead3);

		if (!open)
			doDots(g);
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + r_on + " " + r_off;
	}

	// we have to just assume current will flow either way, even though that
	// might cause singular matrix errors
	public boolean getConnection(int n1, int n2) {
		if (n1 == 2 || n2 == 2)
			return false;
		return true;
	}

	public int getDumpType() {
		return 159;
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Normally closed",
					(getFlags() & FLAG_INVERT) != 0);
			return ei;
		}
		if (n == 1)
			return new EditInfo("On Resistance (ohms)", r_on, 0, 0);
		if (n == 2)
			return new EditInfo("Off Resistance (ohms)", r_off, 0, 0);
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "analog switch";
		arr[1] = open ? "open" : "closed";
		arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
		arr[3] = "I = " + getCurrentDText(getCurrent());
		arr[4] = "Vc = " + getVoltageText(getVolts()[2]);
	}

	public Point getLead3() {
		return lead3;
	}

	public Point getPoint3() {
		return point3;
	}

	public int getPostCount() {
		return 3;
	}

	public Point getPs() {
		return ps;
	}

	public double getR_off() {
		return r_off;
	}

	public double getR_on() {
		return r_on;
	}

	public double getResistance() {
		return resistance;
	}

	public boolean isOpen() {
		return open;
	}

	// we need this to be able to change the matrix for each step
	public boolean nonLinear() {
		return true;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			setFlags((ei.checkbox.getState()) ? (getFlags() | FLAG_INVERT)
					: (getFlags() & ~FLAG_INVERT));
		if (n == 1 && ei.value > 0)
			r_on = ei.value;
		if (n == 2 && ei.value > 0)
			r_off = ei.value;
	}

	public void setLead3(Point lead3) {
		this.lead3 = lead3;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public void setPoint3(Point point3) {
		this.point3 = point3;
	}

	public void setPoints() {
		super.setPoints();
		calcLeads(32);
		ps = new Point();
		int openhs = 16;
		point3 = interpPoint(getPoint1(), getPoint2(), .5, -openhs);
		lead3 = interpPoint(getPoint1(), getPoint2(), .5, -openhs / 2);
		getPins()[2].setPost(point3);
	}

	public void setPs(Point ps) {
		this.ps = ps;
	}

	public void setR_off(double r_off) {
		this.r_off = r_off;
	}

	public void setR_on(double r_on) {
		this.r_on = r_on;
	}

	public void setResistance(double resistance) {
		this.resistance = resistance;
	}

	public void stamp() {
		sim.stampNonLinear(getNodes()[0]);
		sim.stampNonLinear(getNodes()[1]);
	}
}
