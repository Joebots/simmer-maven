package com.joebotics.simmer.client.util;

import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Polygon;

public class GraphicsUtil {
	public static void drawThickCircle(Graphics g, int cx, int cy, int ri) {
		int a;
		double m = Math.PI / 180;
		double r = ri * .98;
		for (a = 0; a != 360; a += 20) {
			double ax = Math.cos(a * m) * r + cx;
			double ay = Math.sin(a * m) * r + cy;
			double bx = Math.cos((a + 20) * m) * r + cx;
			double by = Math.sin((a + 20) * m) * r + cy;
			drawThickLine(g, (int) ax, (int) ay, (int) bx, (int) by);
		}
	}
	public static void drawThickLine(Graphics g, int x, int y, int x2, int y2) {
		// g.drawLine(x, y, x2, y2);
		// g.drawLine(x+1, y, x2+1, y2);
		// g.drawLine(x, y+1, x2, y2+1);
		// g.drawLine(x+1, y+1, x2+1, y2+1);
		g.setLineWidth(3.0);
		g.drawLine(x, y, x2, y2);
		g.setLineWidth(1.0);
	}
	public static void drawThickLine(Graphics g, Point pa, Point pb) {
		// g.drawLine(pa.x, pa.y, pb.x, pb.y);
		// g.drawLine(pa.x+1, pa.y, pb.x+1, pb.y);
		// g.drawLine(pa.x, pa.y+1, pb.x, pb.y+1);
		// g.drawLine(pa.x+1, pa.y+1, pb.x+1, pb.y+1);
		g.setLineWidth(3.0);
		g.drawLine(pa.getX(), pa.getY(), pb.getX(), pb.getY());
		g.setLineWidth(1.0);
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
	
}
