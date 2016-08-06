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
import com.joebotics.simmer.client.gui.impl.Checkbox;
import com.joebotics.simmer.client.gui.impl.EditInfo;
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Font;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.StringTokenizer;


//import java.awt.*;
//import java.util.StringTokenizer;


public abstract class ChipElm extends AbstractCircuitElement {
	public class Pin {
		double curcount;

		private double current;

		private boolean lineOver;

		private boolean bubble;

		private boolean clock;

		private boolean output;

		private boolean value;

		private boolean state;
		int pos, side;

		private int voltSource;

		int bubbleX;

		int bubbleY;
		Point post, stub;
		String text;
		Point textloc;
		public Pin(int p, int s, String t) {
			pos = p;
			side = s;
			text = t;
		}

		void setPoint(int px, int py, int dx, int dy, int dax, int day, int sx,
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
			int xa = px + getCspc2() * dx * pos + sx;
			int ya = py + getCspc2() * dy * pos + sy;
			post = new Point(xa + dax * getCspc2(), ya + day * getCspc2());
			stub = new Point(xa + dax * getCspc(), ya + day * getCspc());
			textloc = new Point(xa, ya);
			if (bubble) {
				bubbleX = xa + dax * 10 * csize;
				bubbleY = ya + day * 10 * csize;
			}
			if (clock) {
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

		public boolean isBubble() {
			return bubble;
		}

		public void setBubble(boolean bubble) {
			this.bubble = bubble;
		}

		public boolean isClock() {
			return clock;
		}

		public void setClock(boolean clock) {
			this.clock = clock;
		}

		public double getCurrent() {
			return current;
		}

		public void setCurrent(double current) {
			this.current = current;
		}

		public boolean isLineOver() {
			return lineOver;
		}

		public void setLineOver(boolean lineOver) {
			this.lineOver = lineOver;
		}

		public boolean isOutput() {
			return output;
		}

		public void setOutput(boolean output) {
			this.output = output;
		}

		public boolean isState() {
			return state;
		}

		public boolean setState(boolean state) {
			this.state = state;
			return state;
		}

		public boolean isValue() {
			return value;
		}

		public void setValue(boolean value) {
			this.value = value;
		}

		public int getVoltSource() {
			return voltSource;
		}

		public void setVoltSource(int voltSource) {
			this.voltSource = voltSource;
		}
	}
	private int bits;
	int clockPointsX[], clockPointsY[];
	int csize;
	private int cspc;
	private int cspc2;
	public static final int FLAG_FLIP_X = 1024;

	public static final int FLAG_FLIP_Y = 2048;

	public static final int FLAG_SMALL = 1;

	private boolean lastClock;

	private Pin pins[];

	int rectPointsX[], rectPointsY[];

	public static final int SIDE_E = 3;

	public static final int SIDE_N = 0;

	public static final int SIDE_S = 1;
	public static final int SIDE_W = 2;
	private int sizeX;
	private int sizeY;
	public ChipElm(int xx, int yy) {
		super(xx, yy);
		if (needsBits())
			bits = (this instanceof DecadeElm) ? 10 : 4;
		setNoDiagonal(true);
		setupPins();
		setSize(sim.getSmallGridCheckItem() != null && sim.getSmallGridCheckItem().getState() ? 1 : 2);
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
			if (pins[i].isState()) {
				getVolts()[i] = new Double(st.nextToken()).doubleValue();
				pins[i].setValue(getVolts()[i] > 2.5);
			}
		}
	}

	public void doStep() {
		int i;
		for (i = 0; i != getPostCount(); i++) {
			Pin p = pins[i];
			if (!p.isOutput())
				p.setValue(getVolts()[i] > 2.5);
		}
		execute();
		for (i = 0; i != getPostCount(); i++) {
			Pin p = pins[i];
			if (p.isOutput())
				sim.updateVoltageSource(0, getNodes()[i], p.getVoltSource(), p.isValue() ? 5
						: 0);
		}
	}

	public void drag(int xx, int yy) {
		yy = sim.getSimmerController().snapGrid(yy);
		if (xx < getX1()) {
			xx = getX1();
			yy = getY1();
		} else {
			setY1(setY2(yy));
			setX2(sim.getSimmerController().snapGrid(xx));
		}
		setPoints();
	}

	public void draw(Graphics g) {
		drawChip(g);
	}

	public void drawChip(Graphics g) {
		int i;
		Font oldfont = g.getFont();
		Font f = new Font("SansSerif", 0, 10 * csize);
		g.setFont(f);
		// FontMetrics fm = g.getFontMetrics();
		for (i = 0; i != getPostCount(); i++) {
			Pin p = pins[i];
			setVoltageColor(g, getVolts()[i]);
			Point a = p.post;
			Point b = p.stub;
			GraphicsUtil.drawThickLine(g, a, b);
			p.curcount = updateDotCount(p.getCurrent(), p.curcount);
			drawDots(g, b, a, p.curcount);
			if (p.isBubble()) {
				g.setColor(sim.getPrintableCheckItem().getState() ? Color.white
						: Color.black);
				GraphicsUtil.drawThickCircle(g, p.bubbleX, p.bubbleY, 1);
				g.setColor(lightGrayColor);
				GraphicsUtil.drawThickCircle(g, p.bubbleX, p.bubbleY, 3);
			}
			g.setColor(whiteColor);
			// int sw = fm.stringWidth(p.text);
			int sw = (int) g.getContext().measureText(p.text).getWidth();
			int asc = (int) g.getCurrentFontSize();
			g.drawString(p.text, p.textloc.getX() - sw / 2, p.textloc.getY() + asc / 2);
			if (p.isLineOver()) {
				int ya = p.textloc.getY() - asc / 2;
				g.drawLine(p.textloc.getX() - sw / 2, ya, p.textloc.getX() + sw / 2, ya);
			}
		}
		g.setColor(needsHighlight() ? selectColor : lightGrayColor);
		GraphicsUtil.drawThickPolygon(g, rectPointsX, rectPointsY, 4);
		if (clockPointsX != null)
			g.drawPolyline(clockPointsX, clockPointsY, 3);
		for (i = 0; i != getPostCount(); i++)
			drawPost(g, pins[i].post.getX(), pins[i].post.getY(), getNodes()[i]);
		g.setFont(oldfont);
	}

	public String dump() {
//		int t = getDumpType();
		String s = super.dump();
		if (needsBits())
			s += " " + bits;
		int i;
		for (i = 0; i != getPostCount(); i++) {
			if (pins[i].isState())
				s += " " + getVolts()[i];
		}
		return s;
	}

	public void execute() {
	}

	public String getChipName() {
		return "chip";
	}

	public boolean getConnection(int n1, int n2) {
		return false;
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Flip X", (getFlags() & FLAG_FLIP_X) != 0);
			return ei;
		}
		if (n == 1) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Flip Y", (getFlags() & FLAG_FLIP_Y) != 0);
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = getChipName();
		int i, a = 1;
		for (i = 0; i != getPostCount(); i++) {
			Pin p = pins[i];
			if (arr[a] != null)
				arr[a] += "; ";
			else
				arr[a] = "";
			String t = p.text;
			if (p.isLineOver())
				t += '\'';
			if (p.isClock())
				t = "Clk";
			arr[a] += t + " = " + getVoltageText(getVolts()[i]);
			if (i % 2 == 1)
				a++;
		}
	}

	public Point getPost(int n) {
		return pins[n].post;
	}

	abstract public int getVoltageSourceCount(); // output count

	public boolean hasGroundConnection(int n1) {
		return pins[n1].isOutput();
	}

	public boolean needsBits() {
		return false;
	}

	public void reset() {
		int i;
		for (i = 0; i != getPostCount(); i++) {
			pins[i].setValue(false);
			pins[i].curcount = 0;
			getVolts()[i] = 0;
		}
		lastClock = false;
	}

	public void setCurrent(int x, double c) {
		int i;
		for (i = 0; i != getPostCount(); i++)
			if (pins[i].isOutput() && pins[i].getVoltSource() == x)
				pins[i].current = c;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			if (ei.checkbox.getState())
				setFlags(getFlags() | FLAG_FLIP_X);
			else
				setFlags(getFlags() & ~FLAG_FLIP_X);
			setPoints();
		}
		if (n == 1) {
			if (ei.checkbox.getState())
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
			Pin p = pins[i];
			switch (p.side) {
			case SIDE_N:
				p.setPoint(x0, y0, 1, 0, 0, -1, 0, 0);
				break;
			case SIDE_S:
				p.setPoint(x0, y0, 1, 0, 0, 1, 0, ys - cspc2);
				break;
			case SIDE_W:
				p.setPoint(x0, y0, 0, 1, -1, 0, 0, 0);
				break;
			case SIDE_E:
				p.setPoint(x0, y0, 0, 1, 1, 0, xs - cspc2, 0);
				break;
			}
		}
	}
	void setSize(int s) {
		csize = s;
		cspc = 8 * s;
		cspc2 = cspc * 2;
		setFlags(getFlags() & ~FLAG_SMALL);
		setFlags(getFlags() | ((s == 1) ? FLAG_SMALL : 0));
	}
	public abstract void setupPins();
	public void setVoltageSource(int j, int vs) {
		int i;
		for (i = 0; i != getPostCount(); i++) {
			Pin p = pins[i];
			if (p.isOutput() && j-- == 0) {
				p.setVoltSource(vs);
				return;
			}
		}
		System.out.println("setVoltageSource failed for " + this);
	}

	public void stamp() {
		int i;
		for (i = 0; i != getPostCount(); i++) {
			Pin p = pins[i];
			if (p.isOutput())
				sim.stampVoltageSource(0, getNodes()[i], p.getVoltSource());
		}
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
	public Pin[] getPins() {
		return pins;
	}
	public void setPins(Pin pins[]) {
		this.pins = pins;
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
