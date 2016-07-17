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

import com.google.gwt.canvas.dom.client.CanvasGradient;
import com.jobotics.simmer.client.Simmer;
import com.jobotics.simmer.client.gui.impl.EditInfo;
import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.gui.util.Point;
import com.jobotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class ResistorElm extends AbstractCircuitElement {
	Point ps3, ps4;

	private double resistance;

	public ResistorElm(int xx, int yy) {
		super(xx, yy);
		setResistance(100);
	}

	public ResistorElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		setResistance(new Double(st.nextToken()).doubleValue());
	}

	public void calculateCurrent() {
		setCurrent((getVolts()[0] - getVolts()[1]) / getResistance());
		// System.out.print(this + " res current set to " + current + "\n");
	}

	public void draw(Graphics g) {
//		int segments = 16;
		int i;
//		int ox = 0;
		// int hs = simmer.euroResistorCheckItem.getState() ? 6 : 8;
		int hs = 6;
		double v1 = getVolts()[0];
		double v2 = getVolts()[1];
		setBbox(getPoint1(), getPoint2(), hs);
		draw2Leads(g);
		setPowerColor(g, true);
		// double segf = 1./segments;
		double len = distance(getLead1(), getLead2());
		g.getContext().save();
		g.getContext().setLineWidth(3.0);
		g.getContext().setTransform(((double) (getLead2().getX() - getLead1().getX())) / len,
				((double) (getLead2().getY() - getLead1().getY())) / len,
				-((double) (getLead2().getY() - getLead1().getY())) / len,
				((double) (getLead2().getX() - getLead1().getX())) / len, getLead1().getX(), getLead1().getY());
		CanvasGradient grad = g.getContext().createLinearGradient(0, 0, len, 0);
		grad.addColorStop(0, getVoltageColor(g, v1).getHexValue());
		grad.addColorStop(1.0, getVoltageColor(g, v2).getHexValue());
		g.getContext().setStrokeStyle(grad);
		if (!sim.getEuroResistorCheckItem().getState()) {
			// // draw zigzag
			// for (i = 0; i != segments; i++) {
			// int nx = 0;
			// switch (i & 3) {
			// case 0: nx = 1; break;
			// case 2: nx = -1; break;
			// default: nx = 0; break;
			// }
			// double v = v1+(v2-v1)*i/segments;
			// setVoltageColor(g, v);
			// interpPoint(lead1, lead2, ps1, i*segf, hs*ox);
			// interpPoint(lead1, lead2, ps2, (i+1)*segf, hs*nx);
			// drawThickLine(g, ps1, ps2);
			// ox = nx;
			// }
			g.getContext().beginPath();
			g.getContext().moveTo(0, 0);
			for (i = 0; i < 4; i++) {
				g.getContext().lineTo((1 + 4 * i) * len / 16, hs);
				g.getContext().lineTo((3 + 4 * i) * len / 16, -hs);
			}
			g.getContext().lineTo(len, 0);
			g.getContext().stroke();

		} else {
			// draw rectangle
			// setVoltageColor(g, v1);
			// interpPoint2(lead1, lead2, ps1, ps2, 0, hs);
			// drawThickLine(g, ps1, ps2);
			// for (i = 0; i != segments; i++) {
			// double v = v1+(v2-v1)*i/segments;
			// setVoltageColor(g, v);
			// interpPoint2(lead1, lead2, ps1, ps2, i*segf, hs);
			// interpPoint2(lead1, lead2, ps3, ps4, (i+1)*segf, hs);
			// drawThickLine(g, ps1, ps3);
			// drawThickLine(g, ps2, ps4);
			// }
			// interpPoint2(lead1, lead2, ps1, ps2, 1, hs);
			// drawThickLine(g, ps1, ps2);

			g.getContext().strokeRect(0, -hs, len, 2.0 * hs);
		}
		g.getContext().restore();
		if (sim.getShowValuesCheckItem().getState()) {
			String s = getShortUnitText(getResistance(), "");
			drawValues(g, s, hs);
		}
		doDots(g);
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + getResistance();
	}

	public int getDumpType() {
		return 'r';
	}

	public EditInfo getEditInfo(int n) {
		// ohmString doesn't work here on linux
		if (n == 0)
			return new EditInfo("Resistance (ohms)", getResistance(), 0, 0);
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "resistor";
		getBasicInfo(arr);
		arr[3] = "R = " + getUnitText(getResistance(), Simmer.ohmString);
		arr[4] = "P = " + getUnitText(getPower(), "W");
	}

	public int getShortcut() {
		return 'r';
	}

	public void setEditValue(int n, EditInfo ei) {
		if (ei.value > 0)
			setResistance(ei.value);
	}

	public void setPoints() {
		super.setPoints();
		calcLeads(32);
		ps3 = new Point();
		ps4 = new Point();
	}

	public void stamp() {
		sim.stampResistor(getNodes()[0], getNodes()[1], getResistance());
	}

	public double getResistance() {
		return resistance;
	}

	public void setResistance(double resistance) {
		this.resistance = resistance;
	}
}
