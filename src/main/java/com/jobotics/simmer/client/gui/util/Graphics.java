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

package com.jobotics.simmer.client.gui.util;


import com.google.gwt.canvas.dom.client.Context2d;

public class Graphics {

	private Context2d context;
	private Font currentFont = null;
	private int currentFontSize;
	private Color lastColor;

	public Graphics(Context2d context) {
		this.context = context;
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		context.beginPath();
		context.moveTo(x1, y1);
		context.lineTo(x2, y2);
		context.stroke();
		// context.closePath();
	}

	public void drawPolyline(int[] xpoints, int[] ypoints, int n) {
		int i;
		context.beginPath();
		for (i = 0; i < n; i++) {
			if (i == 0)
				context.moveTo(xpoints[i], ypoints[i]);
			else
				context.lineTo(xpoints[i], ypoints[i]);
		}
		context.closePath();
		context.stroke();
	}

	public void drawRect(int x, int y, int width, int height) {
		// context.beginPath();
		context.strokeRect(x, y, width, height);
		// context.closePath();
	}

	public void drawString(String s, int x, int y) {
		// context.beginPath();
		context.fillText(s, x, y);
		// context.closePath();
	}

	public void fillOval(int x, int y, int width, int height) {
		context.beginPath();
		context.arc(x + width / 2, y + width / 2, width / 2, 0, 2.0 * Math.PI);
		context.closePath();
		context.fill();
	}

	public void fillPolygon(Polygon p) {
		int i;
		context.beginPath();
		for (i = 0; i < p.getNpoints(); i++) {
			if (i == 0)
				context.moveTo(p.getXpoints()[i], p.getYpoints()[i]);
			else
				context.lineTo(p.getXpoints()[i], p.getYpoints()[i]);
		}
		context.closePath();
		context.fill();
	}

	public void fillRect(int x, int y, int width, int height) {
		// context.beginPath();
		context.fillRect(x, y, width, height);
		// context.closePath();
	}

	public Context2d getContext() {
		return context;
	}

	public int getCurrentFontSize() {
		return currentFontSize;
	}

	public Font getFont() {
		return currentFont;
	}

	public Color getLastColor() {
		return lastColor;
	}

	public void setColor(Color color) {
		if (color != null) {
			String colorString = color.getHexValue();
			context.setStrokeStyle(colorString);
			context.setFillStyle(colorString);
		} else {
			System.out.println("Ignoring null-Color");
		}
		lastColor = color;
	}

	public void setColor(String color) {
		context.setStrokeStyle(color);
		context.setFillStyle(color);
		lastColor = null;
	}

	public void setContext(Context2d context) {
		this.context = context;
	}

	public void setCurrentFontSize(int currentFontSize) {
		this.currentFontSize = currentFontSize;
	}

	public void setFont(Font f) {
		if (f != null) {
			context.setFont(f.getFontname());
			currentFontSize = f.getSize();
			currentFont = f;
		}
	}

	public void setLastColor(Color lastColor) {
		this.lastColor = lastColor;
	}

	public void setLineWidth(double width) {
		context.setLineWidth(width);
	}

}