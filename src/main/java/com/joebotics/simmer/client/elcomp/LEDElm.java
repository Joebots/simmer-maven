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

import com.joebotics.simmer.client.gui.impl.EditInfo;
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;

public class LEDElm extends DiodeElm {
	double colorR, colorG, colorB;

	Point ledLead1, ledLead2, ledCenter;

	public LEDElm(int xx, int yy) {
		super(xx, yy);
		fwdrop = 2.1024259;
		setup();
		colorR = 1;
		colorG = colorB = 0;
	}

	public LEDElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		if ((f & FLAG_FWDROP) == 0)
			fwdrop = 2.1024259;
		setup();
		colorR = new Double(st.nextToken()).doubleValue();
		colorG = new Double(st.nextToken()).doubleValue();
		colorB = new Double(st.nextToken()).doubleValue();
	}

	public void draw(Graphics g) {
		if (needsHighlight() || this == sim.getDragElm()) {
			super.draw(g);
			return;
		}
		setVoltageColor(g, getVolts()[0]);
		GraphicsUtil.drawThickLine(g, getPoint1(), ledLead1);
		setVoltageColor(g, getVolts()[1]);
		GraphicsUtil.drawThickLine(g, ledLead2, getPoint2());

		g.setColor(Color.gray);
		int cr = 12;
		GraphicsUtil.drawThickCircle(g, ledCenter.getX(), ledCenter.getY(), cr);
		cr -= 4;
		double w = 255 * getCurrent() / .01;
		if (w > 255)
			w = 255;
		Color cc = new Color((int) (colorR * w), (int) (colorG * w),
				(int) (colorB * w));
		g.setColor(cc);
		g.fillOval(ledCenter.getX() - cr, ledCenter.getY() - cr, cr * 2, cr * 2);
		setBbox(getPoint1(), getPoint2(), cr);
		updateDotCount();
		drawDots(g, getPoint1(), ledLead1, getCurcount());
		drawDots(g, getPoint2(), ledLead2, -getCurcount());
		drawPosts(g);
	}

	public String dump() {
		return super.dump() + " " + colorR + " " + colorG + " " + colorB;
	}

	public int getDumpType() {
		return 162;
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0)
			return super.getEditInfo(n);
		if (n == 1)
			return new EditInfo("Red Value (0-1)", colorR, 0, 1)
					.setDimensionless();
		if (n == 2)
			return new EditInfo("Green Value (0-1)", colorG, 0, 1)
					.setDimensionless();
		if (n == 3)
			return new EditInfo("Blue Value (0-1)", colorB, 0, 1)
					.setDimensionless();
		return null;
	}

	public void getInfo(String arr[]) {
		super.getInfo(arr);
		arr[0] = "LED";
	}

	public int getShortcut() {
		return 'l';
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0)
			super.setEditValue(0, ei);
		if (n == 1)
			colorR = ei.value;
		if (n == 2)
			colorG = ei.value;
		if (n == 3)
			colorB = ei.value;
	}

	public void setPoints() {
		super.setPoints();
		int cr = 12;
		ledLead1 = interpPoint(getPoint1(), getPoint2(), .5 - cr / getDn());
		ledLead2 = interpPoint(getPoint1(), getPoint2(), .5 + cr / getDn());
		ledCenter = interpPoint(getPoint1(), getPoint2(), .5);
	}
}
