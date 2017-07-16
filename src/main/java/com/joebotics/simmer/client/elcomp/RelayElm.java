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
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.Inductor;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;

// 0 = switch
// 1 = switch end 1
// 2 = switch end 2
// ...
// 3n   = coil
// 3n+1 = coil
// 3n+2 = end of coil resistor

public class RelayElm extends AbstractCircuitElement {
	double a1, a2, a3, a4;
	double coilCurrent, switchCurrent[], coilCurCount, switchCurCount[];
	Point coilPosts[], coilLeads[], swposts[][], swpoles[][], ptSwitch[];
	double d_position, coilR;
	final int FLAG_SWAP_COIL = 1;
	int i_position;
	Inductor ind;
	double inductance;
	Point lines[];
	int nCoil1, nCoil2, nCoil3;
	final int nSwitch0 = 0;
	final int nSwitch1 = 1;
	final int nSwitch2 = 2;
	int openhs;
	int poleCount;

	double r_on, r_off, onCurrent;

	public RelayElm(int xx, int yy) {
		super(xx, yy);
		ind = new Inductor(sim);
		inductance = .2;
		ind.setup(inductance, 0, Inductor.FLAG_BACK_EULER);
		setNoDiagonal(true);
		onCurrent = .02;
		r_on = .05;
		r_off = 1e6;
		coilR = 20;
		coilCurrent = coilCurCount = 0;
		poleCount = 1;
		setupPoles();
		setupPins();
	}

	public RelayElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		poleCount = new Integer(st.nextToken()).intValue();
		inductance = new Double(st.nextToken()).doubleValue();
		coilCurrent = new Double(st.nextToken()).doubleValue();
		r_on = new Double(st.nextToken()).doubleValue();
		r_off = new Double(st.nextToken()).doubleValue();
		onCurrent = new Double(st.nextToken()).doubleValue();
		coilR = new Double(st.nextToken()).doubleValue();
		setNoDiagonal(true);
		ind = new Inductor(sim);
		ind.setup(inductance, coilCurrent, Inductor.FLAG_BACK_EULER);
		setupPoles();
		setupPins();
	}

	public void calculateCurrent() {
		double voltdiff = getVolts()[nCoil1] - getVolts()[nCoil3];
		coilCurrent = ind.calculateCurrent(voltdiff);

		// actually this isn't correct, since there is a small amount
		// of current through the switch when off
		int p;
		for (p = 0; p != poleCount; p++) {
			if (i_position == 2)
				switchCurrent[p] = 0;
			else
				switchCurrent[p] = (getVolts()[nSwitch0 + p * 3] - getVolts()[nSwitch1
						+ p * 3 + i_position])
						/ r_on;
		}
	}

	public void doStep() {
		double voltdiff = getVolts()[nCoil1] - getVolts()[nCoil3];
		ind.doStep(voltdiff);
		int p;
		for (p = 0; p != poleCount * 3; p += 3) {
			sim.stampResistor(getNodes()[nSwitch0 + p], getNodes()[nSwitch1 + p],
					i_position == 0 ? r_on : r_off);
			sim.stampResistor(getNodes()[nSwitch0 + p], getNodes()[nSwitch2 + p],
					i_position == 1 ? r_on : r_off);
		}
	}

	public void draw(Graphics g) {
		int i, p;
		for (i = 0; i != 2; i++) {
			setVoltageColor(g, getVolts()[nCoil1 + i]);
			GraphicsUtil.drawThickLine(g, coilLeads[i], coilPosts[i]);
		}
		int x = ((getFlags() & FLAG_SWAP_COIL) != 0) ? 1 : 0;
		drawCoil(g, getDsign() * 6, coilLeads[x], coilLeads[1 - x],
				getVolts()[nCoil1 + x], getVolts()[nCoil2 - x]);

		// draw lines
		g.setColor(Color.darkGray);
		for (i = 0; i != poleCount; i++) {
			if (i == 0)
				interpPoint(getPoint1(), getPoint2(), lines[i * 2], .5, openhs * 2 + 5
						* getDsign() - i * openhs * 3);
			else
				interpPoint(getPoint1(), getPoint2(), lines[i * 2], .5,
						(int) (openhs * (-i * 3 + 3 - .5 + d_position)) + 5
								* getDsign());
			interpPoint(getPoint1(), getPoint2(), lines[i * 2 + 1], .5,
					(int) (openhs * (-i * 3 - .5 + d_position)) - 5 * getDsign());
			g.drawLine(lines[i * 2].getX(), lines[i * 2].getY(), lines[i * 2 + 1].getX(),
					lines[i * 2 + 1].getY());
		}

		for (p = 0; p != poleCount; p++) {
			int po = p * 3;
			for (i = 0; i != 3; i++) {
				// draw lead
				setVoltageColor(g, getVolts()[nSwitch0 + po + i]);
				GraphicsUtil.drawThickLine(g, swposts[p][i], swpoles[p][i]);
			}

			interpPoint(swpoles[p][1], swpoles[p][2], ptSwitch[p], d_position);
			// setVoltageColor(g, volts[nSwitch0]);
			g.setColor(Color.lightGray);
			GraphicsUtil.drawThickLine(g, swpoles[p][0], ptSwitch[p]);
			switchCurCount[p] = updateDotCount(switchCurrent[p],
					switchCurCount[p]);
			drawDots(g, swposts[p][0], swpoles[p][0], switchCurCount[p]);

			if (i_position != 2)
				drawDots(g, swpoles[p][i_position + 1],
						swposts[p][i_position + 1], switchCurCount[p]);
		}

		coilCurCount = updateDotCount(coilCurrent, coilCurCount);

		drawDots(g, coilPosts[0], coilLeads[0], coilCurCount);
		drawDots(g, coilLeads[0], coilLeads[1], coilCurCount);
		drawDots(g, coilLeads[1], coilPosts[1], coilCurCount);

		drawPosts(g);
		setBbox(coilPosts[0], coilLeads[1], 0);
		adjustBbox(swpoles[poleCount - 1][0], swposts[poleCount - 1][1]); // XXX
	}

	public String dump() {
		return super.dump() + " " + poleCount + " " + inductance + " "
				+ coilCurrent + " " + r_on + " " + r_off + " " + onCurrent
				+ " " + coilR;
	}

	public boolean getConnection(int n1, int n2) {
		return (n1 / 3 == n2 / 3);
	}

	public int getDumpType() {
		return 178;
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return new EditInfo("Inductance (H)", inductance, 0, 0);
		if (n == 1)
			return new EditInfo("On Resistance (ohms)", r_on, 0, 0);
		if (n == 2)
			return new EditInfo("Off Resistance (ohms)", r_off, 0, 0);
		if (n == 3)
			return new EditInfo("On Current (A)", onCurrent, 0, 0);
		if (n == 4)
			return new EditInfo("Number of Poles", poleCount, 1, 4)
					.setDimensionless();
		if (n == 5)
			return new EditInfo("Coil Resistance (ohms)", coilR, 0, 0);
		if (n == 6) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Swap Coil Direction",
					(getFlags() & FLAG_SWAP_COIL) != 0);
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = i_position == 0 ? "relay (off)"
				: i_position == 1 ? "relay (on)" : "relay";
		int i;
		int ln = 1;
		for (i = 0; i != poleCount; i++)
			arr[ln++] = "I" + (i + 1) + " = "
					+ getCurrentDText(switchCurrent[i]);
		arr[ln++] = "coil I = " + getCurrentDText(coilCurrent);
		arr[ln++] = "coil Vd = "
				+ getVoltageDText(getVolts()[nCoil1] - getVolts()[nCoil2]);
	}

	public int getInternalNodeCount() {
		return 1;
	}

	public int getPostCount() {
		return 2 + poleCount * 3;
	}

	public int getShortcut() {
		return 'R';
	}

	// we need this to be able to change the matrix for each step
	public boolean nonLinear() {
		return true;
	}

	public void reset() {
		super.reset();
		ind.reset();
		coilCurrent = coilCurCount = 0;
		int i;
		for (i = 0; i != poleCount; i++)
			switchCurrent[i] = switchCurCount[i] = 0;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0 && ei.value > 0) {
			inductance = ei.value;
			ind.setup(inductance, coilCurrent, Inductor.FLAG_BACK_EULER);
		}
		if (n == 1 && ei.value > 0)
			r_on = ei.value;
		if (n == 2 && ei.value > 0)
			r_off = ei.value;
		if (n == 3 && ei.value > 0)
			onCurrent = ei.value;
		if (n == 4 && ei.value >= 1) {
			poleCount = (int) ei.value;
			setPoints();
		}
		if (n == 5 && ei.value > 0)
			coilR = ei.value;
		if (n == 6) {
			if (ei.checkbox.getState())
				setFlags(getFlags() | FLAG_SWAP_COIL);
			else
				setFlags(getFlags() & ~FLAG_SWAP_COIL);
			setPoints();
		}
	}

	public void setPoints() {
		super.setPoints();
		setupPoles();
		allocNodes();
		openhs = -getDsign() * 16;

		// switch
		calcLeads(32);
		swposts = new Point[poleCount][3];
		swpoles = new Point[poleCount][3];
		int i, j;
		for (i = 0; i != poleCount; i++) {
			for (j = 0; j != 3; j++) {
				swposts[i][j] = new Point();
				swpoles[i][j] = new Point();
			}
			interpPoint(getLead1(), getLead2(), swpoles[i][0], 0, -openhs * 3 * i);
			interpPoint(getLead1(), getLead2(), swpoles[i][1], 1, -openhs * 3 * i
					- openhs);
			interpPoint(getLead1(), getLead2(), swpoles[i][2], 1, -openhs * 3 * i
					+ openhs);
			interpPoint(getPoint1(), getPoint2(), swposts[i][0], 0, -openhs * 3 * i);
			interpPoint(getPoint1(), getPoint2(), swposts[i][1], 1, -openhs * 3 * i
					- openhs);
			interpPoint(getPoint1(), getPoint2(), swposts[i][2], 1, -openhs * 3 * i
					+ openhs);
			for (int k = 0; k < 3; k++) {
				getPins()[i * 3 + k].setPost(swposts[i][k]);
			}
		}

		// coil
		coilPosts = newPointArray(2);
		coilLeads = newPointArray(2);
		ptSwitch = newPointArray(poleCount);

		int x = ((getFlags() & FLAG_SWAP_COIL) != 0) ? 1 : 0;
		interpPoint(getPoint1(), getPoint2(), coilPosts[0], x, openhs * 2);
		interpPoint(getPoint1(), getPoint2(), coilPosts[1], x, openhs * 3);
		interpPoint(getPoint1(), getPoint2(), coilLeads[0], .5, openhs * 2);
		interpPoint(getPoint1(), getPoint2(), coilLeads[1], .5, openhs * 3);
		
		for (int k = poleCount * 3; k < getPostCount(); k++) {
			getPins()[k].setPost(coilPosts[k - poleCount * 3]);	
		}

		// lines
		lines = newPointArray(poleCount * 2);
	}
	
	public void setupPins() {
		setPins(new Pin[getPostCount()]);
		for (int i = 0; i < poleCount; i++) {
			for (int j = 0; j < 3; j++) {
				getPins()[i * 3 + j] = new Pin(i * 3 + j, Side.UNKNOWN, "Pole " + (i + 1) + "." + (j + 1));				
			}
		}
		for (int i = poleCount * 3; i < getPostCount(); i++) {
			getPins()[i] = new Pin(i, Side.UNKNOWN, "Coil " + ((i - poleCount * 3) + 1));				
		}
	}

	void setupPoles() {
		nCoil1 = 3 * poleCount;
		nCoil2 = nCoil1 + 1;
		nCoil3 = nCoil1 + 2;
		if (switchCurrent == null || switchCurrent.length != poleCount) {
			switchCurrent = new double[poleCount];
			switchCurCount = new double[poleCount];
		}
	}

	public void stamp() {
		// inductor from coil post 1 to internal node
		ind.stamp(getNodes()[nCoil1], getNodes()[nCoil3]);
		// resistor from internal node to coil post 2
		sim.stampResistor(getNodes()[nCoil3], getNodes()[nCoil2], coilR);

		int i;
		for (i = 0; i != poleCount * 3; i++)
			sim.stampNonLinear(getNodes()[nSwitch0 + i]);
	}

	public void startIteration() {
		ind.startIteration(getVolts()[nCoil1] - getVolts()[nCoil3]);

		// magic value to balance operate speed with reset speed
		// semi-realistically
		double magic = 1.3;
		double pmult = Math.sqrt(magic + 1);
		double p = coilCurrent * pmult / onCurrent;
		d_position = Math.abs(p * p) - 1.3;
		if (d_position < 0)
			d_position = 0;
		if (d_position > 1)
			d_position = 1;
		if (d_position < .1)
			i_position = 0;
		else if (d_position > .9)
			i_position = 1;
		else
			i_position = 2;
		// System.out.println("ind " + this + " " + current + " " + voltdiff);
	}
}
