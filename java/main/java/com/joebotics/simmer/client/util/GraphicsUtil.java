package com.joebotics.simmer.client.util;

import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Polygon;

public class GraphicsUtil {
	private static final double STANDARD_LINE_WIDTH = 1.0;
	private static final double THICK_LINE_WIDTH = 3.0;
	
	public static void drawThickArc(Graphics g, Point pa, Point pb, Point pc, int radius) {
		g.setLineWidth(THICK_LINE_WIDTH);
		g.drawArc(pa.getX(), pa.getY(), pb.getX(), pb.getY(), pc.getX(), pc.getY(), radius);
		g.setLineWidth(STANDARD_LINE_WIDTH);
	}
	public static void drawThickCircle(Graphics g, int cx, int cy, int radius) {
		g.setLineWidth(THICK_LINE_WIDTH);
		g.drawCircle(cx, cy, radius);
		g.setLineWidth(STANDARD_LINE_WIDTH);
	}
	public static void drawThickLine(Graphics g, int x, int y, int x2, int y2) {
		g.setLineWidth(THICK_LINE_WIDTH);
		g.drawLine(x, y, x2, y2);
		g.setLineWidth(STANDARD_LINE_WIDTH);
	}
	public static void drawThickLine(Graphics g, Point pa, Point pb) {
		g.setLineWidth(THICK_LINE_WIDTH);
		g.drawLine(pa.getX(), pa.getY(), pb.getX(), pb.getY());
		g.setLineWidth(STANDARD_LINE_WIDTH);
	}
	public static void drawThickPolygon(Graphics g, int xs[], int ys[], int c) {
		int i;
		for (i = 0; i != c - 1; i++)
			drawThickLine(g, xs[i], ys[i], xs[i + 1], ys[i + 1]);
		drawThickLine(g, xs[i], ys[i], xs[0], ys[0]);
	}
	public static void drawThickPolygon(Graphics g, Polygon p) {
		drawThickPolygon(g, p.getXpoints(), p.getYpoints(), p.getNpoints());
	}
	public static void drawThickRect(Graphics g, Point topLeft, Point bottomRight) {
		int xs[] = new int[] {topLeft.getX(), bottomRight.getX(), bottomRight.getX(), topLeft.getX()};
		int ys[] = new int[] {topLeft.getY(), topLeft.getY(), bottomRight.getY(), bottomRight.getY()};
		drawThickPolygon(g, xs, ys, 4);
	}
}
