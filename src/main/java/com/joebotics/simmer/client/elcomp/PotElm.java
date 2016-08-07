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

//import java.awt.*;
//import java.awt.event.*;
//import java.util.StringTokenizer;

import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.gui.impl.EditInfo;
import com.joebotics.simmer.client.gui.impl.Scrollbar;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;

public class PotElm extends AbstractCircuitElement implements Command, MouseWheelHandler {
	int bodyLen;
	double curcount1, curcount2, curcount3;
	double current1, current2, current3;
	Label label;
	double position, maxResistance, resistance1, resistance2;
	Point post3, corner2, arrowPoint, midpoint, arrow1, arrow2;

	Point ps3, ps4;

	Scrollbar slider;

	String sliderText;

	public PotElm(int xx, int yy) {
		super(xx, yy);
		setup();
		maxResistance = 1000;
		position = .5;
		sliderText = "Resistance";
		createSlider();
	}

	public PotElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		maxResistance = new Double(st.nextToken()).doubleValue();
		position = new Double(st.nextToken()).doubleValue();
		sliderText = st.nextToken();
		while (st.hasMoreTokens())
			sliderText += ' ' + st.nextToken();
		createSlider();
	}

	public void calculateCurrent() {
		current1 = (getVolts()[0] - getVolts()[2]) / resistance1;
		current2 = (getVolts()[1] - getVolts()[2]) / resistance2;
		current3 = -current1 - current2;
	}

	void createSlider() {
		sim.getSidePanel().addWidgetToVerticalPanel(label = new Label(sliderText));
		label.addStyleName("topSpace");
		int value = (int) (position * 100);
		sim.getSidePanel().addWidgetToVerticalPanel(slider = new Scrollbar(
				Scrollbar.HORIZONTAL, value, 1, 0, 101, this, this));
		// simmer.verticalPanel.validate();
		// slider.addAdjustmentListener(this);
	}

	public void delete() {
		sim.getSidePanel().removeWidgetFromVerticalPanel(label);
		sim.getSidePanel().removeWidgetFromVerticalPanel(slider);
	}

	public void draw(Graphics g) {
		int segments = 16;
		int i;
		int ox = 0;
		int hs = sim.getMainMenuBar().getOptionsMenuBar().getEuroResistorCheckItem().getState() ? 6 : 8;
		double v1 = getVolts()[0];
		double v2 = getVolts()[1];
		double v3 = getVolts()[2];
		setBbox(getPoint1(), getPoint2(), hs);
		draw2Leads(g);
		setPowerColor(g, true);
		double segf = 1. / segments;
		int divide = (int) (segments * position);
		if (!sim.getMainMenuBar().getOptionsMenuBar().getEuroResistorCheckItem().getState()) {
			// draw zigzag
			for (i = 0; i != segments; i++) {
				int nx = 0;
				switch (i & 3) {
				case 0:
					nx = 1;
					break;
				case 2:
					nx = -1;
					break;
				default:
					nx = 0;
					break;
				}
				double v = v1 + (v3 - v1) * i / divide;
				if (i >= divide)
					v = v3 + (v2 - v3) * (i - divide) / (segments - divide);
				setVoltageColor(g, v);
				interpPoint(getLead1(), getLead2(), ps1, i * segf, hs * ox);
				interpPoint(getLead1(), getLead2(), ps2, (i + 1) * segf, hs * nx);
				GraphicsUtil.drawThickLine(g, ps1, ps2);
				ox = nx;
			}
		} else {
			// draw rectangle
			setVoltageColor(g, v1);
			interpPoint2(getLead1(), getLead2(), ps1, ps2, 0, hs);
			GraphicsUtil.drawThickLine(g, ps1, ps2);
			for (i = 0; i != segments; i++) {
				double v = v1 + (v3 - v1) * i / divide;
				if (i >= divide)
					v = v3 + (v2 - v3) * (i - divide) / (segments - divide);
				setVoltageColor(g, v);
				interpPoint2(getLead1(), getLead2(), ps1, ps2, i * segf, hs);
				interpPoint2(getLead1(), getLead2(), ps3, ps4, (i + 1) * segf, hs);
				GraphicsUtil.drawThickLine(g, ps1, ps3);
				GraphicsUtil.drawThickLine(g, ps2, ps4);
			}
			interpPoint2(getLead1(), getLead2(), ps1, ps2, 1, hs);
			GraphicsUtil.drawThickLine(g, ps1, ps2);
		}
		setVoltageColor(g, v3);
		GraphicsUtil.drawThickLine(g, post3, corner2);
		GraphicsUtil.drawThickLine(g, corner2, arrowPoint);
		GraphicsUtil.drawThickLine(g, arrow1, arrowPoint);
		GraphicsUtil.drawThickLine(g, arrow2, arrowPoint);
		curcount1 = updateDotCount(current1, curcount1);
		curcount2 = updateDotCount(current2, curcount2);
		curcount3 = updateDotCount(current3, curcount3);
		if (sim.getDragElm() != this) {
			drawDots(g, getPoint1(), midpoint, curcount1);
			drawDots(g, getPoint2(), midpoint, curcount2);
			drawDots(g, post3, corner2, curcount3);
			drawDots(g, corner2, midpoint, curcount3 + distance(post3, corner2));
		}
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + maxResistance + " " + position + " "
				+ sliderText;
	}

	public void execute() {
		sim.setAnalyzeFlag(true);
		setPoints();
	}
	public int getDumpType() {
		return 174;
	}
	public EditInfo getEditInfo(int n) {
		// ohmString doesn't work here on linux
		if (n == 0)
			return new EditInfo("Resistance (ohms)", maxResistance, 0, 0);
		if (n == 1) {
			EditInfo ei = new EditInfo("Slider Text", 0, -1, -1);
			ei.text = sliderText;
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = "potentiometer";
		arr[1] = "Vd = " + getVoltageDText(getVoltageDiff());
		arr[2] = "R1 = " + getUnitText(resistance1, Simmer.ohmString);
		arr[3] = "R2 = " + getUnitText(resistance2, Simmer.ohmString);
		arr[4] = "I1 = " + getCurrentDText(current1);
		arr[5] = "I2 = " + getCurrentDText(current2);
	}

	public Point getPost(int n) {
		return (n == 0) ? getPoint1() : (n == 1) ? getPoint2() : post3;
	}

	public int getPostCount() {
		return 3;
	}

	public void onMouseWheel(MouseWheelEvent e) {
		if (slider != null)
			slider.onMouseWheel(e);
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			maxResistance = ei.value;
		if (n == 1) {
			sliderText = ei.textf.getText();
			label.setText(sliderText);
			sim.getSidePanel().setiFrameHeight();
		}
	}

	public void setMouseElm(boolean v) {
		super.setMouseElm(v);
		if (slider != null)
			slider.draw();
	}

	public void setPoints() {
		super.setPoints();
		int offset = 0;
		if (abs(getDx()) > abs(getDy())) {
			setDx(sim.getSimmerController().snapGrid(getDx() / 2) * 2);
			setX2(getPoint1().getX() + getDx());
			getPoint2().setX(getX2());
//			getPoint2().x = setX2(getPoint1().getX() + getDx());
			offset = (getDx() < 0) ? getDy() : -getDy();
			getPoint2().setY(getPoint1().getY());
		} else {
			setDy(sim.getSimmerController().snapGrid(getDy() / 2) * 2);
			setY2(getPoint1().getY() + getDy());
			getPoint2().setY(getY2());
//			getPoint2().y = setY2(getPoint1().getY() + getDy());
			offset = (getDy() > 0) ? getDx() : -getDx();
			getPoint2().setX(getPoint1().getX());
		}
		if (offset == 0)
			offset = sim.getGridSize();
		setDn(distance(getPoint1(), getPoint2()));
		int bodyLen = 32;
		calcLeads(bodyLen);
		position = slider.getValue() * .0099 + .005;
		int soff = (int) ((position - .5) * bodyLen);
		// int offset2 = offset - sign(offset)*4;
		post3 = interpPoint(getPoint1(), getPoint2(), .5, offset);
		corner2 = interpPoint(getPoint1(), getPoint2(), soff / getDn() + .5, offset);
		arrowPoint = interpPoint(getPoint1(), getPoint2(), soff / getDn() + .5,
				8 * sign(offset));
		midpoint = interpPoint(getPoint1(), getPoint2(), soff / getDn() + .5);
		arrow1 = new Point();
		arrow2 = new Point();
		double clen = abs(offset) - 8;
		interpPoint2(corner2, arrowPoint, arrow1, arrow2, (clen - 8) / clen, 8);
		ps3 = new Point();
		ps4 = new Point();
	}

	void setup() {
	}

	public void stamp() {
		resistance1 = maxResistance * position;
		resistance2 = maxResistance * (1 - position);
		sim.stampResistor(getNodes()[0], getNodes()[2], resistance1);
		sim.stampResistor(getNodes()[2], getNodes()[1], resistance2);
	}
}
