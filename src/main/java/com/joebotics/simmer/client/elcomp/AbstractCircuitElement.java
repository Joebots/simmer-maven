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

// import java.awt.*;
//import java.text.DecimalFormat;
//import java.text.NumberFormat;

import com.google.gwt.i18n.client.NumberFormat;
import com.joebotics.simmer.client.Simmer;
import com.joebotics.simmer.client.gui.impl.EditInfo;
import com.joebotics.simmer.client.gui.impl.Editable;
import com.joebotics.simmer.client.gui.util.Color;
import com.joebotics.simmer.client.gui.util.Font;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Polygon;
import com.joebotics.simmer.client.gui.util.Rectangle;
import com.joebotics.simmer.client.util.GraphicsUtil;
import com.joebotics.simmer.client.util.MouseModeEnum.MouseMode;

import java.util.UUID;

public abstract class AbstractCircuitElement implements Editable {

	private double uuid = Math.random();

	protected static Color colorScale[];
	protected static int colorScaleCount = 32;
	public static double currentMult, powerMult;
	protected static final double pi = Math.PI;
	public static Point ps1, ps2;
	protected static NumberFormat showFormat, shortFormat;// , noCommaFormat;
	public static Simmer sim;
	public static Font unitsFont;

	public static double voltageRange = 5;
	public static Color whiteColor, selectColor, lightGrayColor;
	
	protected Rectangle boundingBox;
	protected double curcount;

	protected double current;

	protected double dn;

	protected double dpx1;

	protected double dpy1;

	protected int dsign;

	protected int dx;

	protected int dy;

	protected int flags;

	protected boolean iAmMouseElm = false;

	protected Point lead1;

	protected Point lead2;

	protected int nodes[];

	protected boolean noDiagonal;

	protected Point point1;

	protected Point point2;

	protected boolean selected;

	protected double volts[];

	protected int voltSource;

	protected int x1;

	protected int x2;

	protected int y1;

	protected int y2;

	protected static int abs(int x) {
		return x < 0 ? -x : x;
	}
	protected static double distance(Point p1, Point p2) {
		double x = p1.getX() - p2.getX();
		double y = p1.getY() - p2.getY();
		return Math.sqrt(x * x + y * y);
	}

	
	//*/
	// TODO: Badger: utils
	protected static String getCurrentDText(double i) {
		return getUnitText(Math.abs(i), "A");
	}
	/*
	 * static String getUnitText(double v, String u) { double va = Math.abs(v);
	 * if (va < 1e-14) return "0 " + u; if (va < 1e-9) return
	 * showFormat.format(v*1e12) + " p" + u; if (va < 1e-6) return
	 * showFormat.format(v*1e9) + " n" + u; if (va < 1e-3) return
	 * showFormat.format(v*1e6) + " " + Simmer.muString + u; if (va < 1) return
	 * showFormat.format(v*1e3) + " m" + u; if (va < 1e3) return
	 * showFormat.format(v) + " " + u; if (va < 1e6) return
	 * showFormat.format(v*1e-3) + " k" + u; if (va < 1e9) return
	 * showFormat.format(v*1e-6) + " M" + u; return showFormat.format(v*1e-9) +
	 * " G" + u; } static String getShortUnitText(double v, String u) { double
	 * va = Math.abs(v); if (va < 1e-13) return null; if (va < 1e-9) return
	 * shortFormat.format(v*1e12) + "p" + u; if (va < 1e-6) return
	 * shortFormat.format(v*1e9) + "n" + u; if (va < 1e-3) return
	 * shortFormat.format(v*1e6) + Simmer.muString + u; if (va < 1) return
	 * shortFormat.format(v*1e3) + "m" + u; if (va < 1e3) return
	 * shortFormat.format(v) + u; if (va < 1e6) return
	 * shortFormat.format(v*1e-3) + "k" + u; if (va < 1e9) return
	 * shortFormat.format(v*1e-6) + "M" + u; return shortFormat.format(v*1e-9) +
	 * "G" + u; }
	 */
	public static String getCurrentText(double i) {
		return getUnitText(i, "A");
	}

	protected static NumberFormat getShortFormat() {
		return shortFormat;
	}

	public static String getShortUnitText(double v, String u) {
		return myGetUnitText(v, u, true);
	}

	protected static NumberFormat getShowFormat() {
		return showFormat;
	}

	// IES - hacking
	public static String getUnitText(double v, String u) {
		return myGetUnitText(v, u, false);
	}

	protected static String getVoltageDText(double v) {
		return getUnitText(Math.abs(v), "V");
	}

	public static String getVoltageText(double v) {
		return getUnitText(v, "V");
	}

	public static void initClass(Simmer s) {
		unitsFont = new Font("SansSerif", 0, 12);
		sim = s;

		colorScale = new Color[colorScaleCount];
		int i;
		for (i = 0; i != colorScaleCount; i++) {
			double v = i * 2. / colorScaleCount - 1;
			if (v < 0) {
				int n1 = (int) (128 * -v) + 127;
				int n2 = (int) (127 * (1 + v));
				colorScale[i] = new Color(n1, n2, n2);
			} else {
				int n1 = (int) (128 * v) + 127;
				int n2 = (int) (127 * (1 - v));
				colorScale[i] = new Color(n2, n1, n2);
			}
		}

		ps1 = new Point();
		ps2 = new Point();

		// showFormat = DecimalFormat.getInstance();
		// showFormat.setMaximumFractionDigits(2);
		showFormat = NumberFormat.getFormat("####.##");
		// shortFormat = DecimalFormat.getInstance();
		// shortFormat.setMaximumFractionDigits(1);
		shortFormat = NumberFormat.getFormat("####.#");
		// noCommaFormat = DecimalFormat.getInstance();
		// noCommaFormat.setMaximumFractionDigits(10);
		// noCommaFormat.setGroupingUsed(false);
	}

	protected static int max(int a, int b) {
		return (a > b) ? a : b;
	}

	protected static int min(int a, int b) {
		return (a < b) ? a : b;
	}

	private static String myGetUnitText(double v, String u, boolean sf) {
		NumberFormat s;
		if (sf)
			s = shortFormat;
		else
			s = showFormat;
		double va = Math.abs(v);
		if (va < 1e-14)
			return "0 " + u;
		if (va < 1e-9)
			return s.format(v * 1e12) + " p" + u;
		if (va < 1e-6)
			return s.format(v * 1e9) + " n" + u;
		if (va < 1e-3)
			return s.format(v * 1e6) + " " + Simmer.getMuString() + u;
		if (va < 1)
			return s.format(v * 1e3) + " m" + u;
		if (va < 1e3)
			return s.format(v) + " " + u;
		if (va < 1e6)
			return s.format(v * 1e-3) + " k" + u;
		if (va < 1e9)
			return s.format(v * 1e-6) + " M" + u;
		return s.format(v * 1e-9) + " G" + u;
	}

	protected static void setShortFormat(NumberFormat shortFormat) {
		AbstractCircuitElement.shortFormat = shortFormat;
	}

	protected static void setShowFormat(NumberFormat showFormat) {
		AbstractCircuitElement.showFormat = showFormat;
	}

	protected static int sign(int x) {
		return (x < 0) ? -1 : (x == 0) ? 0 : 1;
	}

	protected AbstractCircuitElement(int xx, int yy) {
		x1 = x2 = xx;
		y1 = y2 = yy;
		flags = getDefaultFlags();
		allocNodes();
		initBoundingBox();
	}

	protected AbstractCircuitElement(int xa, int ya, int xb, int yb, int f) {
		x1 = xa;
		y1 = ya;
		x2 = xb;
		y2 = yb;
		flags = f;
		allocNodes();
		initBoundingBox();
	}

	protected void adjustBbox(int x1, int y1, int x2, int y2) {
		if (x1 > x2) {
			int q = x1;
			x1 = x2;
			x2 = q;
		}
		if (y1 > y2) {
			int q = y1;
			y1 = y2;
			y2 = q;
		}
		x1 = min(boundingBox.x, x1);
		y1 = min(boundingBox.y, y1);
		x2 = max(boundingBox.x + boundingBox.width - 1, x2);
		y2 = max(boundingBox.y + boundingBox.height - 1, y2);
		boundingBox.setBounds(x1, y1, x2 - x1, y2 - y1);
	}

	protected void adjustBbox(Point p1, Point p2) {
		adjustBbox(p1.getX(), p1.getY(), p2.getX(), p2.getY());
	}

	protected void allocNodes() {
		nodes = new int[getPostCount() + getInternalNodeCount()];
		volts = new double[getPostCount() + getInternalNodeCount()];
	}

	// determine if moving this element by (dx,dy) will put it on top of another
	// element
	public boolean allowMove(int dx, int dy) {
		int nx = x1 + dx;
		int ny = y1 + dy;
		int nx2 = x2 + dx;
		int ny2 = y2 + dy;
		
		int i;
		for (i = 0; i != sim.getElmList().size(); i++) {
			AbstractCircuitElement ce = sim.getElm(i);
			if (ce.x1 == nx && ce.y1 == ny && ce.x2 == nx2 && ce.y2 == ny2)
				return false;
			
			if (ce.x1 == nx2 && ce.y1 == ny2 && ce.x2 == nx && ce.y2 == ny)
				return false;
		}
		return true;
	}
	
	// TODO: Badger: utils
	protected Polygon calcArrow(Point a, Point b, double al, double aw) {
		Polygon poly = new Polygon();
		Point p1 = new Point();
		Point p2 = new Point();
		int adx = b.getX() - a.getX();
		int ady = b.getY() - a.getY();
		double l = Math.sqrt(adx * adx + ady * ady);
		poly.addPoint(b.getX(), b.getY());
		interpPoint2(a, b, p1, p2, 1 - al / l, aw);
		poly.addPoint(p1.getX(), p1.getY());
		poly.addPoint(p2.getX(), p2.getY());
		return poly;
	}

	protected void calcLeads(int len) {
		if (dn < len || len == 0) {
			lead1 = point1;
			lead2 = point2;
			return;
		}
		lead1 = interpPoint(point1, point2, (dn - len) / (2 * dn));
		lead2 = interpPoint(point1, point2, (dn + len) / (2 * dn));
	}

	// TODO: badger: abstraction
	public void reset() {
		int i;
		for (i = 0; i != getPostCount() + getInternalNodeCount(); i++)
			volts[i] = 0;
		curcount = 0;
	}
	
	// TODO: badger: abstraction
	public void stamp() {
	}

	// TODO: badger: abstraction
	public void startIteration() {
	}
	
	// TODO: badger: abstraction
	public void calculateCurrent() {
	}
	
	// TODO: badger: abstraction
	public void delete() {
	}

	// TODO: badger: abstraction
	public void doStep() {
	}
	
	// TODO: badger: abstraction
	public void draw(Graphics g) {
	}
	
	// TODO: badger: abstraction
	public String dump() {
		int t = getDumpType();
		return (t < 127 ? ((char) t) + " " : t + " ") + x1 + " " + y1 + " " + x2
				+ " " + y2 + " " + flags;
	}

	// TODO: badger: abstraction
	protected int getBasicInfo(String arr[]) {
		arr[1] = "I = " + getCurrentDText(getCurrent());
		arr[2] = "Vd = " + getVoltageDText(getVoltageDiff());
		return 3;
	}
	
	// TODO: badger: abstraction
	public boolean hasGroundConnection(int n1) {
		return false;
	}

	// TODO: badger: abstraction
	public boolean nonLinear() {
		return false;
	}
	
	public boolean canViewInScope() {
		return getPostCount() <= 2;
	}

	// TODO: Badger: utils
	protected boolean comparePair(int x1, int x2, int y1, int y2) {
		return ((x1 == y1 && x2 == y2) || (x1 == y2 && x2 == y1));
	}

	// TODO: Badger: utils
	protected Polygon createPolygon(Point a[]) {
		Polygon p = new Polygon();
		int i;
		for (i = 0; i != a.length; i++)
			p.addPoint(a[i].getX(), a[i].getY());
		return p;
	}

	// TODO: Badger: utils
	protected Polygon createPolygon(Point a, Point b, Point c) {
		Polygon p = new Polygon();
		p.addPoint(a.getX(), a.getY());
		p.addPoint(b.getX(), b.getY());
		p.addPoint(c.getX(), c.getY());
		return p;
	}

	// TODO: Badger: utils
	protected Polygon createPolygon(Point a, Point b, Point c, Point d) {
		Polygon p = new Polygon();
		p.addPoint(a.getX(), a.getY());
		p.addPoint(b.getX(), b.getY());
		p.addPoint(c.getX(), c.getY());
		p.addPoint(d.getX(), d.getY());
		return p;
	}
	
	// TODO: Badger: utils
	protected void doDots(Graphics g) {
		updateDotCount();
		if (sim.getDragElm() != this)
			drawDots(g, point1, point2, curcount);
	}
	
	// TODO: Badger: utils
	public void drag(int xx, int yy) {
		xx = sim.getSimmerController().snapGrid(xx);
		yy = sim.getSimmerController().snapGrid(yy);
		if (noDiagonal) {
			if (Math.abs(x1 - xx) < Math.abs(y1 - yy)) {
				xx = x1;
			} else {
				yy = y1;
			}
		}
		x2 = xx;
		y2 = yy;
		setPoints();
	}

	// TODO: Badger: utils
	protected void draw2Leads(Graphics g) {
		// draw first lead
		setVoltageColor(g, volts[0]);
		GraphicsUtil.drawThickLine(g, point1, lead1);

		// draw second lead
		setVoltageColor(g, volts[1]);
		GraphicsUtil.drawThickLine(g, lead2, point2);
	}

	// TODO: Badger: utils
	protected void drawCenteredText(Graphics g, String s, int x, int y, boolean cx) {
		// FontMetrics fm = g.getFontMetrics();
		// int w = fm.stringWidth(s);
		// int w=0;
		// if (cx)
		// x -= w/2;
		// g.drawString(s, x, y+fm.getAscent()/2);
		// adjustBbox(x, y-fm.getAscent()/2,
		// x+w, y+fm.getAscent()/2+fm.getDescent());
		int w = (int) g.getContext().measureText(s).getWidth();
		int h2 = (int) g.getCurrentFontSize() / 2;
		g.getContext().save();
		g.getContext().setTextBaseline("middle");
		if (cx) {
			g.getContext().setTextAlign("center");
			adjustBbox(x - w / 2, y - h2, x + w / 2, y + h2);
		} else {
			adjustBbox(x, y - h2, x + w, y + h2);
		}

		if (cx)
			g.getContext().setTextAlign("center");
		g.drawString(s, x, y);
		g.getContext().restore();
	}

	// TODO: Badger: utils
	protected void drawCoil(Graphics g, int hs, Point p1, Point p2, double v1, double v2) {
//		double len = distance(p1, p2);
		int segments = 30; // 10*(int) (len/10);
		int i;
		double segf = 1. / segments;

		ps1.setLocation(p1);
		for (i = 0; i != segments; i++) {
			double cx = (((i + 1) * 6. * segf) % 2) - 1;
			double hsx = Math.sqrt(1 - cx * cx);
			if (hsx < 0)
				hsx = -hsx;
			interpPoint(p1, p2, ps2, i * segf, hsx * hs);
			double v = v1 + (v2 - v1) * i / segments;
			setVoltageColor(g, v);
			GraphicsUtil.drawThickLine(g, ps1, ps2);
			ps1.setLocation(ps2);
		}
		// GWT.log("Coil"+hs+" "+p1.x+" "+p1.y+" "+p2.x+" "+p2.y);
		// g.context.save();
		// g.context.setLineWidth(3.0);
		// g.context.setTransform(((double)(p2.x-p1.x))/len,
		// ((double)(p2.y-p1.y))/len,
		// -((double)(p2.y-p1.y))/len,((double)(p2.x-p1.x))/len,p1.x,p1.y);
		// CanvasGradient grad = g.context.createLinearGradient(0,0,len,0);
		// grad.addColorStop(0, getVoltageColor(g,v1).getHexValue());
		// grad.addColorStop(1.0, getVoltageColor(g,v2).getHexValue());
		// g.context.setStrokeStyle(grad);
		// g.context.beginPath();
		// g.context.arc(len*0.16667,0,len*0.16667,pi,(hs<0)?0:pi*2.0, hs<0);
		// g.context.arc(len*0.5,0,len*0.16667,pi,pi*2.0);
		// g.context.arc(len*0.83333,0,len*0.16667,pi,pi*2.0);
		// g.context.stroke();
		// g.context.restore();
		// g.context.setTransform(1.0, 0, 0, 1.0, 0, 0);
		// g.context.setLineWidth(1.0);
	}

//	private int getVoltageSource() {
//		return voltSource;
//	}

	// TODO: Badger: utils
	protected void drawDots(Graphics g, Point pa, Point pb, double pos) {
		if (sim.getSidePanel().getStoppedCheck().getState() || pos == 0 || !sim.getMainMenuBar().getOptionsMenuBar().getDotsCheckItem().getState())
			return;
		int dx = pb.getX() - pa.getX();
		int dy = pb.getY() - pa.getY();
		double dn = Math.sqrt(dx * dx + dy * dy);
		g.setColor(sim.getMainMenuBar().getOptionsMenuBar().getConventionCheckItem().getState() ? Color.yellow
				: Color.cyan);
		int ds = 16;
		pos %= ds;
		if (pos < 0)
			pos += ds;
		double di = 0;
		for (di = pos; di < dn; di += ds) {
			int x0 = (int) (pa.getX() + di * dx / dn);
			int y0 = (int) (pa.getY() + di * dy / dn);
			g.fillRect(x0 - 2, y0 - 2, 4, 4);
		}
	}

	// TODO: Badger: utils
	public void drawHandles(Graphics g, Color c) {
		g.setColor(c);
		g.fillRect(x1 - 3, y1 - 3, 7, 7);
		g.fillRect(x2 - 3, y2 - 3, 7, 7);
	}

	// TODO: Badger: utils
	private void drawPost(Graphics g, int x0, int y0) {
		g.setColor(whiteColor);
		g.fillOval(x0 - 3, y0 - 3, 7, 7);
	}

	// TODO: Badger: utils
	protected void drawPost(Graphics g, int x0, int y0, int n) {
		if (sim.getDragElm() == null && !needsHighlight() && sim.getCircuitNode(n).links.size() == 2)
			return;

		if (sim.getMouseMode() == MouseMode.DRAG_ROW || sim.getMouseMode() == MouseMode.DRAG_COLUMN)
			return;

		drawPost(g, x0, y0);
	}

	// TODO: Badger: utils
	public void drawPosts(Graphics g) {
		for (int i = 0; i != getPostCount(); i++) {
			Point p = getPost(i);
			drawPost(g, p.getX(), p.getY(), nodes[i]);
		}
	}

	// TODO: Badger: utils
	protected void drawValues(Graphics g, String s, double hs) {
		if (s == null)
			return;
		g.setFont(unitsFont);

		// FontMetrics fm = g.getFontMetrics();
		int w = (int) g.getContext().measureText(s).getWidth();

		g.setColor(whiteColor);
		int ya = (int) g.getCurrentFontSize() / 2;
		int xc, yc;
		if (this instanceof RailElm || this instanceof SweepElm) {
			xc = x2;
			yc = y2;
		} else {
			xc = (x2 + x1) / 2;
			yc = (y2 + y1) / 2;
		}
		int dpx = (int) (dpx1 * hs);
		int dpy = (int) (dpy1 * hs);
		if (dpx == 0) {
			g.drawString(s, xc - w / 2, yc - abs(dpy) - 2);
		} else {
			int xx = xc + abs(dpx) + 2;
			if (this instanceof VoltageElm || (x1 < x2 && y1 > y2))
				xx = xc - (w + abs(dpx) + 2);
			g.drawString(s, xx, yc + dpy + ya);
		}
	}

	public Rectangle getBoundingBox() {
		return boundingBox;
	}
	// TODO: Badger: abstraction
	public boolean getConnection(int n1, int n2) {
		return true;
	}

	protected double getCurcount() {
		return curcount;
	}

	// TODO: Badger: utils
	public double getCurrent() {
		return current;
	}

	private int getDefaultFlags() {
		return 0;
	}

	protected double getDn() {
		return dn;
	}

	protected int getDsign() {
		return dsign;
	}

	// TODO: Badger: abstraction
	public Class<?> getDumpClass() {
		return getClass();
	}
	
	// TODO: Badger: abstraction
	public int getDumpType() {
		return 0;
	}

	protected int getDx() {
		return dx;
	}

	protected int getDy() {
		return dy;
	}

	public EditInfo getEditInfo(int n) {
		return null;
	}
	
	// TODO: Badger: abstraction
	protected int getFlags() {
		return flags;
	}

	// TODO: Badger: abstraction
	public void getInfo(String arr[]) {
	}

	public int getInternalNodeCount() {
		return 0;
	}

	// TODO: Badger: abstraction
	protected Point getLead1() {
		return lead1;
	}

	// TODO: Badger: abstraction
	protected Point getLead2() {
		return lead2;
	}

	
	public int getNode(int n) {
		return nodes[n];
	}

	protected int[] getNodes() {
		return nodes;
	}

	protected Point getPoint1() {
		return point1;
	}

	protected Point getPoint2() {
		return point2;
	}

	public Point getPost(int n) {
		return (n == 0) ? point1 : (n == 1) ? point2 : null;
	}

//	private void doAdjust() {
//	}
//
//	private void setupAdjust() {
//	}

	public int getPostCount() {
		return 2;
	}

	public double getPostVoltage(int x) {
		return volts[x];
	}

	public double getPower() {
		return getVoltageDiff() * current;
	}

	// TODO: Badger: abstraction
	public String getScopeUnits(int x) {
		return (x == 1) ? "W" : "V";
	}

	// TODO: Badger: abstraction
	public double getScopeValue(int x) {
		return (x == 1) ? getPower() : getVoltageDiff();
	}

	public int getShortcut() {
		return 0;
	}

//	private void setConductanceColor(Graphics g, double w0) {
//		w0 *= powerMult;
//		// System.out.println(w);
//		double w = (w0 < 0) ? -w0 : w0;
//		if (w > 1)
//			w = 1;
//		int rg = (int) (w * 255);
//		g.setColor(new Color(rg, rg, rg));
//	}

	// TODO: Badger: abstraction
	protected Color getVoltageColor(Graphics g, double volts) {
		if (needsHighlight()) {
			return (selectColor);
		}
		if (!sim.getMainMenuBar().getOptionsMenuBar().getVoltsCheckItem().getState()) {
			if (!sim.getMainMenuBar().getOptionsMenuBar().getPowerCheckItem().getState()) // &&
												// !conductanceCheckItem.getState())
				return (whiteColor);
			return (g.getLastColor());
		}
		int c = (int) ((volts + voltageRange) * (colorScaleCount - 1) / (voltageRange * 2));
		if (c < 0)
			c = 0;
		if (c >= colorScaleCount)
			c = colorScaleCount - 1;
		return (colorScale[c]);
	}

	public double getVoltageDiff() {
		return volts[0] - volts[1];
	}

	public int getVoltageSourceCount() {
		return 0;
	}

	protected double[] getVolts() {
		return volts;
	}

	protected int getVoltSource() {
		return voltSource;
	}

	public int getX1() {
		return x1;
	}

	public int getX2() {
		return x2;
	}

	public int getY1() {
		return y1;
	}

	public int getY2() {
		return y2;
	}

	private void initBoundingBox() {
		boundingBox = new Rectangle();
		boundingBox.setBounds(min(x1, x2), min(y1, y2), abs(x2 - x1) + 1, abs(y2
				- y1) + 1);
	}

	// TODO: Badger: utils
	protected Point interpPoint(Point a, Point b, double f) {
		Point p = new Point();
		interpPoint(a, b, p, f);
		return p;
	}

	// TODO: Badger: utils
	protected Point interpPoint(Point a, Point b, double f, double g) {
		Point p = new Point();
		interpPoint(a, b, p, f, g);
		return p;
	}

	// TODO: Badger: utils
	protected void interpPoint(Point a, Point b, Point c, double f) {
		/*
		int xpd = b.x - a.x;
		int ypd = b.y - a.y;
		 * double q = (a.x*(1-f)+b.x*f+.48); System.out.println(q + " " + (int)
		 * q);
		 */
		c.setX((int) Math.floor(a.getX() * (1 - f) + b.getX() * f + .48));
		c.setY((int) Math.floor(a.getY() * (1 - f) + b.getY() * f + .48));
	}

	// TODO: Badger: utils
	protected void interpPoint(Point a, Point b, Point c, double f, double g) {
		// int xpd = b.x-a.x;
		// int ypd = b.y-a.y;
		int gx = b.getY() - a.getY();
		int gy = a.getX() - b.getX();
		g /= Math.sqrt(gx * gx + gy * gy);
		c.setX((int) Math.floor(a.getX() * (1 - f) + b.getX() * f + g * gx + .48));
		c.setY((int) Math.floor(a.getY() * (1 - f) + b.getY() * f + g * gy + .48));
	}

	// TODO: Badger: utils
	protected void interpPoint2(Point a, Point b, Point c, Point d, double f, double g) {
		// int xpd = b.x-a.x;
		// int ypd = b.y-a.y;
		int gx = b.getY() - a.getY();
		int gy = a.getX() - b.getX();
		g /= Math.sqrt(gx * gx + gy * gy);
		c.setX((int) Math.floor(a.getX() * (1 - f) + b.getX() * f + g * gx + .48));
		c.setY((int) Math.floor(a.getY() * (1 - f) + b.getY() * f + g * gy + .48));
		d.setX((int) Math.floor(a.getX() * (1 - f) + b.getX() * f - g * gx + .48));
		d.setY((int) Math.floor(a.getY() * (1 - f) + b.getY() * f - g * gy + .48));
	}

	public boolean isCenteredText() {
		return false;
	}

	public boolean isMouseElm() {
		return iAmMouseElm;
	}

	protected boolean isNoDiagonal() {
		return noDiagonal;
	}

	public boolean isSelected() {
		return selected;
	}

//	public boolean isWire() {
//		return false;
//	}

	public void move(int dx, int dy) {
		x1 += dx;
		y1 += dy;
		x2 += dx;
		y2 += dy;
		boundingBox.move(dx, dy);
		setPoints();
	}

//	private boolean isGraphicElmt() {
//		return false;
//	}

	public void movePoint(int n, int dx, int dy) {
		// modified by IES to prevent the user dragging points to create zero
		// sized nodes
		// that then render improperly
		int oldx = x1;
		int oldy = y1;
		int oldx2 = x2;
		int oldy2 = y2;
		if (n == 0) {
			x1 += dx;
			y1 += dy;
		} else {
			x2 += dx;
			y2 += dy;
		}
		if (x1 == x2 && y1 == y2) {
			x1 = oldx;
			y1 = oldy;
			x2 = oldx2;
			y2 = oldy2;
		}
		setPoints();
	}

	public boolean needsHighlight() {
		return iAmMouseElm || selected;
	}

	public boolean needsShortcut() {
		return getShortcut() > 0;
	}

	// TODO: Badger: utils
	protected Point[] newPointArray(int n) {
		Point a[] = new Point[n];
		while (n > 0)
			a[--n] = new Point();
		return a;
	}

	public void selectRect(Rectangle r) {
		selected = r.intersects(boundingBox);
	}

	// TODO: Badger: utils
	protected void setBbox(int x1, int y1, int x2, int y2) {
		if (x1 > x2) {
			int q = x1;
			x1 = x2;
			x2 = q;
		}
		if (y1 > y2) {
			int q = y1;
			y1 = y2;
			y2 = q;
		}
		boundingBox.setBounds(x1, y1, x2 - x1 + 1, y2 - y1 + 1);
	}

	// TODO: Badger: utils
	protected void setBbox(Point p1, Point p2, double w) {
		setBbox(p1.getX(), p1.getY(), p2.getX(), p2.getY());
//		int gx = p2.y - p1.y;
//		int gy = p1.x - p2.x;
		int dpx = (int) (dpx1 * w);
		int dpy = (int) (dpy1 * w);
		adjustBbox(p1.getX() + dpx, p1.getY() + dpy, p1.getX() - dpx, p1.getY() - dpy);
	}

	protected void setBoundingBox(Rectangle boundingBox) {
		this.boundingBox = boundingBox;
	}

	protected double setCurcount(double curcount) {
		this.curcount = curcount;
		return curcount;
	}

	protected void setCurrent(double current) {
		this.current = current;
	}

	public void setCurrent(int x, double c) {
		current = c;
	}

	protected void setDn(double dn) {
		this.dn = dn;
	}

	protected void setDsign(int dsign) {
		this.dsign = dsign;
	}

	protected void setDx(int dx) {
		this.dx = dx;
	}

	protected void setDy(int dy) {
		this.dy = dy;
	}

	public void setEditValue(int n, EditInfo ei) {
	}

	protected void setFlags(int flags) {
		this.flags = flags;
	}

	protected void setLead1(Point lead1) {
		this.lead1 = lead1;
	}

	protected void setLead2(Point lead2) {
		this.lead2 = lead2;
	}

	public void setMouseElm(boolean v) {
		iAmMouseElm = v;
	}

	public void setNode(int p, int n) {
		nodes[p] = n;
	}

	protected void setNodes(int nodes[]) {
		this.nodes = nodes;
	}

	public void setNodeVoltage(int n, double c) {
		volts[n] = c;
		calculateCurrent();
	}

	protected void setNoDiagonal(boolean noDiagonal) {
		this.noDiagonal = noDiagonal;
	}

	protected void setPoint1(Point point1) {
		this.point1 = point1;
	}

	protected void setPoint2(Point point2) {
		this.point2 = point2;
	}

	public void setPoints() {
		dx = x2 - x1;
		dy = y2 - y1;
		dn = Math.sqrt(dx * dx + dy * dy);
		dpx1 = dy / dn;
		dpy1 = -dx / dn;
		dsign = (dy == 0) ? sign(dx) : sign(dy);
		point1 = new Point(x1, y1);
		point2 = new Point(x2, y2);
	}

	// TODO: Badger: abstraction
	protected void setPowerColor(Graphics g, boolean yellow) {
		/*
		 * if (conductanceCheckItem.getState()) { setConductanceColor(g,
		 * current/getVoltageDiff()); return; }
		 */
		if (!sim.getMainMenuBar().getOptionsMenuBar().getPowerCheckItem().getState())
			return;
		setPowerColor(g, getPower());
	}

	// TODO: Badger: abstraction
	protected void setPowerColor(Graphics g, double w0) {
		w0 *= powerMult;
		// System.out.println(w);
		double w = (w0 < 0) ? -w0 : w0;
		if (w > 1)
			w = 1;
		int rg = 128 + (int) (w * 127);
		int b = (int) (128 * (1 - w));
		/*
		 * if (yellow) g.setColor(new Color(rg, rg, b)); else
		 */
		if (w0 > 0)
			g.setColor(new Color(rg, b, b));
		else
			g.setColor(new Color(b, rg, b));
	}

	public void setSelected(boolean x) {
		selected = x;
	}

	protected void setVoltageColor(Graphics g, double volts) {
		g.setColor(getVoltageColor(g, volts));
	}

	public void setVoltageSource(int n, int v) {
		voltSource = v;
	}

	protected void setVolts(double volts[]) {
		this.volts = volts;
	}

	protected void setVoltSource(int voltSource) {
		this.voltSource = voltSource;
	}

	public void setX1(int x1) {
		this.x1 = x1;
	}

	public int setX2(int x2) {
		this.x2 = x2;
		return x2;
	}

	public void setY1(int y1) {
		this.y1 = y1;
	}

	public int setY2(int y2) {
		this.y2 = y2;
		return y2;
	}

	protected void updateDotCount() {
		curcount = updateDotCount(current, curcount);
	}

	protected double updateDotCount(double cur, double cc) {

		if (sim.getSidePanel().getStoppedCheck().getState())
			return cc;
		double cadd = cur * currentMult;
		/*
		 * if (cur != 0 && cadd <= .05 && cadd >= -.05) cadd = (cadd < 0) ? -.05
		 * : .05;
		 */
		cadd %= 8;
		/*
		 * if (cadd > 8) cadd = 8; if (cadd < -8) cadd = -8;
		 */
		return cc + cadd;
	}


	public double getUuid() {
		return uuid;
	}

	public void setUuid(double uuid) {
		this.uuid = uuid;
	}

	public boolean isWire(){
		return (this instanceof WireElm);
	}

}
