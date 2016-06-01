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

import com.jobotics.simmer.client.Simmer;
import com.jobotics.simmer.client.gui.impl.EditInfo;
import com.jobotics.simmer.client.gui.util.Color;
import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.gui.util.Point;
import com.jobotics.simmer.client.util.GraphicsUtil;
import com.jobotics.simmer.client.util.StringTokenizer;




//import java.awt.*;
//import java.util.StringTokenizer;

public class LampElm extends AbstractCircuitElement {
	Point bulbLead[], filament[], bulb;
	int bulbR;
	final int filament_len = 24;

	double resistance;

	final double roomTemp = 300;

	double temp, nom_pow, nom_v, warmTime, coolTime;

	public LampElm(int xx, int yy) {
		super(xx, yy);
		temp = roomTemp;
		nom_pow = 100;
		nom_v = 120;
		warmTime = .4;
		coolTime = .4;
	}

	public LampElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		temp = new Double(st.nextToken()).doubleValue();
		nom_pow = new Double(st.nextToken()).doubleValue();
		nom_v = new Double(st.nextToken()).doubleValue();
		warmTime = new Double(st.nextToken()).doubleValue();
		coolTime = new Double(st.nextToken()).doubleValue();
	}
	public void calculateCurrent() {
		setCurrent((getVolts()[0] - getVolts()[1]) / resistance);
		// System.out.print(this + " res current set to " + current + "\n");
	}

	public void doStep() {
		sim.stampResistor(getNodes()[0], getNodes()[1], resistance);
	}

	public void draw(Graphics g) {
		double v1 = getVolts()[0];
		double v2 = getVolts()[1];
		setBbox(getPoint1(), getPoint2(), 4);
		adjustBbox(bulb.getX() - bulbR, bulb.getY() - bulbR, bulb.getX() + bulbR, bulb.getY() + bulbR);
		// adjustbbox
		draw2Leads(g);
		setPowerColor(g, true);
		g.setColor(getTempColor());
		g.fillOval(bulb.getX() - bulbR, bulb.getY() - bulbR, bulbR * 2, bulbR * 2);
		g.setColor(Color.white);
		GraphicsUtil.drawThickCircle(g, bulb.getX(), bulb.getY(), bulbR);
		setVoltageColor(g, v1);
		GraphicsUtil.drawThickLine(g, getLead1(), filament[0]);
		setVoltageColor(g, v2);
		GraphicsUtil.drawThickLine(g, getLead2(), filament[1]);
		setVoltageColor(g, (v1 + v2) * .5);
		GraphicsUtil.drawThickLine(g, filament[0], filament[1]);
		updateDotCount();
		if (sim.dragElm != this) {
			drawDots(g, getPoint1(), getLead1(), getCurcount());
			double cc = getCurcount() + (getDn() - 16) / 2;
			drawDots(g, getLead1(), filament[0], cc);
			cc += filament_len;
			drawDots(g, filament[0], filament[1], cc);
			cc += 16;
			drawDots(g, filament[1], getLead2(), cc);
			cc += filament_len;
			drawDots(g, getLead2(), getPoint2(), getCurcount());
		}
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + temp + " " + nom_pow + " " + nom_v + " "
				+ warmTime + " " + coolTime;
	}

	public int getDumpType() {
		return 181;
	}

	public EditInfo getEditInfo(int n) {
		// ohmString doesn't work here on linux
		if (n == 0)
			return new EditInfo("Nominal Power", nom_pow, 0, 0);
		if (n == 1)
			return new EditInfo("Nominal Voltage", nom_v, 0, 0);
		if (n == 2)
			return new EditInfo("Warmup Time (s)", warmTime, 0, 0);
		if (n == 3)
			return new EditInfo("Cooldown Time (s)", coolTime, 0, 0);
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "lamp";
		getBasicInfo(arr);
		arr[3] = "R = " + getUnitText(resistance, Simmer.ohmString);
		arr[4] = "P = " + getUnitText(getPower(), "W");
		arr[5] = "T = " + ((int) temp) + " K";
	}

	Color getTempColor() {
		if (temp < 1200) {
			int x = (int) (255 * (temp - 800) / 400);
			if (x < 0)
				x = 0;
			return new Color(x, 0, 0);
		}
		if (temp < 1700) {
			int x = (int) (255 * (temp - 1200) / 500);
			if (x < 0)
				x = 0;
			return new Color(255, x, 0);
		}
		if (temp < 2400) {
			int x = (int) (255 * (temp - 1700) / 700);
			if (x < 0)
				x = 0;
			return new Color(255, 255, x);
		}
		return Color.white;
	}

	public boolean nonLinear() {
		return true;
	}

	public void reset() {
		super.reset();
		temp = roomTemp;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0 && ei.value > 0)
			nom_pow = ei.value;
		if (n == 1 && ei.value > 0)
			nom_v = ei.value;
		if (n == 2 && ei.value > 0)
			warmTime = ei.value;
		if (n == 3 && ei.value > 0)
			coolTime = ei.value;
	}

	public void setPoints() {
		super.setPoints();
		int llen = 16;
		calcLeads(llen);
		bulbLead = newPointArray(2);
		filament = newPointArray(2);
		bulbR = 20;
		filament[0] = interpPoint(getLead1(), getLead2(), 0, filament_len);
		filament[1] = interpPoint(getLead1(), getLead2(), 1, filament_len);
		double br = filament_len - Math.sqrt(bulbR * bulbR - llen * llen);
		bulbLead[0] = interpPoint(getLead1(), getLead2(), 0, br);
		bulbLead[1] = interpPoint(getLead1(), getLead2(), 1, br);
		bulb = interpPoint(filament[0], filament[1], .5);
	}

	public void stamp() {
		sim.stampNonLinear(getNodes()[0]);
		sim.stampNonLinear(getNodes()[1]);
	}

	public void startIteration() {
		// based on http://www.intusoft.com/nlpdf/nl11.pdf
		double nom_r = nom_v * nom_v / nom_pow;
		// this formula doesn't work for values over 5390
		double tp = (temp > 5390) ? 5390 : temp;
		resistance = nom_r
				* (1.26104 - 4.90662 * Math.sqrt(17.1839 / tp - 0.00318794) - 7.8569 / (tp - 187.56));
		double cap = 1.57e-4 * nom_pow;
		double capw = cap * warmTime / .4;
		double capc = cap * coolTime / .4;
		// System.out.println(nom_r + " " + (resistance/nom_r));
		temp += getPower() * sim.getTimeStep() / capw;
		double cr = 2600 / nom_pow;
		temp -= sim.getTimeStep() * (temp - roomTemp) / (capc * cr);
		// System.out.println(capw + " " + capc + " " + temp + " " +resistance);
	}
}
