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

import com.joebotics.simmer.client.elcomp.chips.DecadeElm;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Font;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.OptionKey;
import com.joebotics.simmer.client.util.StringTokenizer;

import gwt.material.design.client.ui.MaterialCheckBox;

public abstract class ChipElm extends AbstractCircuitElement {
	private int bits;
	int clockPointsX[], clockPointsY[];
	int csize;
	private int cspc;
	private int cspc2;

	private boolean lastClock;

	protected int rectPointsX[], rectPointsY[];

	private int sizeX;
	private int sizeY;
	public ChipElm(int xx, int yy) {
		super(xx, yy);
		if (needsBits())
			bits = (this instanceof DecadeElm) ? 10 : 4;
		setNoDiagonal(true);
		setupPins();
		setSize(sim.getOptions().getBoolean(OptionKey.SMALL_GRID) ? 1 : 2);
	}
	
	public ChipElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		if (needsBits())
			bits = new Integer(st.nextToken()).intValue();
		setNoDiagonal(true);
		setupPins();
		setSize((f & FLAG_SMALL) != 0 ? 1 : 2);
		int i;
		for (i = 0; i != getPostCount(); i++) {
			if (getPins()[i].isState()) {
				getVolts()[i] = new Double(st.nextToken()).doubleValue();
				getPins()[i].setValue(getVolts()[i] > 2.5);
			}
		}
	}

	public void doStep() {
		int i;
		for (i = 0; i != getPostCount(); i++) {
			Pin p = getPins()[i];
			if (!p.isOutput()) {
				p.setVoltage(getVolts()[i]);
			}
		}
		execute();
		for (i = 0; i != getPostCount(); i++) {
			Pin p = getPins()[i];
			if (p.isOutput()) {
				sim.updateVoltageSource(0, getNodes()[i], p.getVoltageSource(), p.getVoltage());
			}
		}
	}

	public void drag(int xx, int yy) {
		yy = sim.snapGrid(yy);
		if (xx < getX1()) {
			xx = getX1();
			yy = getY1();
		} else {
			setY1(setY2(yy));
			setX2(sim.snapGrid(xx));
		}
		setPoints();
	}

	public void draw(Graphics g) {
		drawChip(g);
	}

	public void drawChip(Graphics g) {
		int i;
		Font oldfont = g.getFont();
		Font f = new Font("SansSerif", 0, 6 * csize);
		g.setFont(f);
		// FontMetrics fm = g.getFontMetrics();
		for (i = 0; i != getPostCount(); i++) {
			Pin p = getPins()[i];
			setVoltageColor(g, getVolts()[i]);
			Point a = p.getPost();
			Point b = p.getStub();
			GraphicsUtil.drawThickLine(g, a, b);
			p.setCurcount(updateDotCount(p.getCurrent(), p.getCurcount()));
			drawDots(g, b, a, p.getCurcount());
			if (p.isBubble()) {
				g.setColor(sim.getOptions().getBoolean(OptionKey.WHITE_BACKGROUND) ? Color.white : Color.black);
				GraphicsUtil.drawThickCircle(g, p.getBubbleX(), p.getBubbleY(), 1);
				g.setColor(lightGrayColor);
				GraphicsUtil.drawThickCircle(g, p.getBubbleX(), p.getBubbleY(), 3);
			}
			g.setColor(whiteColor);
			// int sw = fm.stringWidth(p.text);
			int sw = (int) g.getContext().measureText(p.getText()).getWidth();
			int asc = g.getCurrentFontSize();
			g.drawString(p.getText(), p.getTextloc().getX() - sw / 2, p.getTextloc().getY() + asc / 2);
			if (p.isLineOver()) {
				int ya = p.getTextloc().getY() - asc / 2;
				g.drawLine(p.getTextloc().getX() - sw / 2, ya, p.getTextloc().getX() + sw / 2, ya);
			}
		}
		g.setColor(needsHighlight() ? selectColor : lightGrayColor);
		GraphicsUtil.drawThickPolygon(g, rectPointsX, rectPointsY, 4);
		if (clockPointsX != null)
			g.drawPolyline(clockPointsX, clockPointsY, 3);
		for (i = 0; i != getPostCount(); i++)
			drawPost(g, getPins()[i].getPost().getX(), getPins()[i].getPost().getY(), getNodes()[i]);
		g.setFont(oldfont);
		drawChipName(g);
	}

	public String dump() {
//		int t = getDumpType();
		String s = super.dump();
		if (needsBits())
			s += " " + bits;
		int i;
		for (i = 0; i != getPostCount(); i++) {
			if (getPins()[i].isState())
				s += " " + getVolts()[i];
		}
		return s;
	}

	public void execute() {
	}

	public String getChipName() {
		return "chip";
	}

	@Override
	public String getComponentName() {
		return getChipName();
	}

	public boolean getConnection(int n1, int n2) {
		return false;
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Flip X");
            ei.checkbox.setValue((getFlags() & FLAG_FLIP_X) != 0);
			return ei;
		}
		if (n == 1) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Flip Y");
            ei.checkbox.setValue((getFlags() & FLAG_FLIP_Y) != 0);
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = getChipName();
		int i, a = 1;
		for (i = 0; i != getPostCount(); i++) {
			Pin p = getPins()[i];
			if (arr[a] != null)
				arr[a] += "; ";
			else
				arr[a] = "";
			String t = p.getText();
			if (p.isLineOver())
				t += '\'';
			if (p.isClock())
				t = "Clk";
			arr[a] += t + " = " + getVoltageText(getVolts()[i]);
			if (i % 2 == 1)
				a++;
		}
	}

	abstract public int getVoltageSourceCount(); // output count

	public boolean needsBits() {
		return false;
	}

	public void reset() {
		int i;
		for (i = 0; i != getPostCount(); i++) {
			getPins()[i].setValue(false);
			getPins()[i].setCurcount(0);
			getVolts()[i] = 0;
		}
		lastClock = false;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			if (ei.checkbox.getValue())
				setFlags(getFlags() | FLAG_FLIP_X);
			else
				setFlags(getFlags() & ~FLAG_FLIP_X);
			setPoints();
		}
		if (n == 1) {
			if (ei.checkbox.getValue())
				setFlags(getFlags() | FLAG_FLIP_Y);
			else
				setFlags(getFlags() & ~FLAG_FLIP_Y);
			setPoints();
		}
	}

	public void setPoints() {
		if (getX2() - getX1() > sizeX * cspc2 && this == sim.getDragElm())
			setSize(2);
//		int hs = cspc;
		int x0 = getX1() + cspc2;
		int y0 = getY1();
		int xr = x0 - cspc;
		int yr = y0 - cspc;
		int xs = sizeX * cspc2;
		int ys = sizeY * cspc2;
		rectPointsX = new int[] { xr, xr + xs, xr + xs, xr };
		rectPointsY = new int[] { yr, yr, yr + ys, yr + ys };
		setBbox(xr, yr, rectPointsX[2], rectPointsY[2]);
		int i;
		for (i = 0; i != getPostCount(); i++) {
			Pin p = getPins()[i];
			switch (p.getSide()) {
			case NORTH:
				setPoint(p, x0, y0, 1, 0, 0, -1, 0, 0);
				break;
			case SOUTH:
				setPoint(p, x0, y0, 1, 0, 0, 1, 0, ys - cspc2);
				break;
			case WEST:
				setPoint(p, x0, y0, 0, 1, -1, 0, 0, 0);
				break;
			case EAST:
				setPoint(p, x0, y0, 0, 1, 1, 0, xs - cspc2, 0);
				break;
			}
		}
	}
	
	private void setPoint(Pin pin, int px, int py, int dx, int dy, int dax, int day, int sx,
			int sy) {
		if ((getFlags() & FLAG_FLIP_X) != 0) {
			dx = -dx;
			dax = -dax;
			px += getCspc2() * (getSizeX() - 1);
			sx = -sx;
		}
		if ((getFlags() & FLAG_FLIP_Y) != 0) {
			dy = -dy;
			day = -day;
			py += getCspc2() * (getSizeY() - 1);
			sy = -sy;
		}
		int xa = px + getCspc2() * dx * pin.getNumber() + sx;
		int ya = py + getCspc2() * dy * pin.getNumber() + sy;
		pin.setPost(new Point(xa + dax * getCspc2(), ya + day * getCspc2()));
		pin.setStub(new Point(xa + dax * getCspc(), ya + day * getCspc()));
		pin.setTextloc(new Point(xa + dax * getCspc() / 2, ya));
		if (pin.isBubble()) {
			pin.setBubbleX(xa + dax * 10 * csize);
			pin.setBubbleY(ya + day * 10 * csize);
		}
		if (pin.isClock()) {
			clockPointsX = new int[3];
			clockPointsY = new int[3];
			clockPointsX[0] = xa + dax * getCspc() - dx * getCspc() / 2;
			clockPointsY[0] = ya + day * getCspc() - dy * getCspc() / 2;
			clockPointsX[1] = xa;
			clockPointsY[1] = ya;
			clockPointsX[2] = xa + dax * getCspc() + dx * getCspc() / 2;
			clockPointsY[2] = ya + day * getCspc() + dy * getCspc() / 2;
		}
	}

	void setSize(int s) {
		csize = s;
		cspc = 8 * s;
		cspc2 = cspc * 2;
		setFlags(getFlags() & ~FLAG_SMALL);
		setFlags(getFlags() | ((s == 1) ? FLAG_SMALL : 0));
	}

	private void drawChipName(Graphics g) {
		String s = getChipName();
		if (s == null)
			return;
		g.setFont(unitsFont);
		int w = (int) g.getContext().measureText(s).getWidth();
		g.setColor(whiteColor);
		int ya = (int) g.getCurrentFontSize() / 2;
		int xc = rectPointsX[0] + (rectPointsX[1] - rectPointsX[0]) / 2, yc = rectPointsY[0];
		g.drawString(s, xc - w / 2, yc - ya - 2);
	}

	public Point getCenterPoint() {
		Point center = new Point();
		center.setX(rectPointsX[0] + (rectPointsX[1] - rectPointsX[0]) / 2);
		center.setY(rectPointsY[0] + (rectPointsY[2] - rectPointsY[0]) / 2);
		return center;
	}

	public int getBits() {
		return bits;
	}
	public void setBits(int bits) {
		this.bits = bits;
	}
	public int getCspc() {
		return cspc;
	}
	public void setCspc(int cspc) {
		this.cspc = cspc;
	}
	public int getCspc2() {
		return cspc2;
	}
	public void setCspc2(int cspc2) {
		this.cspc2 = cspc2;
	}
	public boolean isLastClock() {
		return lastClock;
	}
	public void setLastClock(boolean lastClock) {
		this.lastClock = lastClock;
	}
	public int getSizeX() {
		return sizeX;
	}
	public void setSizeX(int sizeX) {
		this.sizeX = sizeX;
	}
	public int getSizeY() {
		return sizeY;
	}
	public void setSizeY(int sizeY) {
		this.sizeY = sizeY;
	}
}
