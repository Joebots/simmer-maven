//	  Extracted from file
//    Copyright 1995-2006 Sun Microsystems, Inc.  All Rights Reserved
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 2 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package com.joebotics.simmer.client.gui.util;

// Via http://grepcode.com/file_/repository.grepcode.com/java/root/jdk/openjdk/6-b14/java/awt/Rectangle.java/?v=source

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

import java.io.Serializable;

public class Rectangle implements Serializable{

	public int height;
	public int width;
	public int x;
	public int y;

	public Rectangle() {
		x = 0;
		y = 0;
		width = 0;
		height = 0;
	}

	public Rectangle(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rectangle(Rectangle r) {
		this(r.x, r.y, r.width, r.height);
	}

	public boolean contains(int X, int Y) {
		int w = this.width;
		int h = this.height;
		if ((w | h) < 0) {
			// At least one of the dimensions is negative...
			return false;
		}
		// Note: if either dimension is zero, tests below must return false...
		int x = this.x;
		int y = this.y;
		if (X < x || Y < y) {
			return false;
		}
		w += x;
		h += y;
		// overflow || intersect
		return ((w < x || w > X) && (h < y || h > Y));
	}

	public boolean equals(Object obj) {
		if (obj instanceof Rectangle) {
			Rectangle r = (Rectangle) obj;
			return ((x == r.x) && (y == r.y) && (width == r.width) && (height == r.height));
		}
		return super.equals(obj);
	}

	public boolean intersects(Rectangle r) {
		int tw = this.width;
		int th = this.height;
		int rw = r.width;
		int rh = r.height;
		if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
			return false;
		}
		int tx = this.x;
		int ty = this.y;
		int rx = r.x;
		int ry = r.y;
		rw += rx;
		rh += ry;
		tw += tx;
		th += ty;
		// overflow || intersect
		return ((rw < rx || rw > tx) && (rh < ry || rh > ty)
				&& (tw < tx || tw > rx) && (th < ty || th > ry));
	}

	public void move(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public Rectangle union(Rectangle r) {
		long tx2 = this.width;
		long ty2 = this.height;
		if ((tx2 | ty2) < 0) {
			// This rectangle has negative dimensions...
			// If r has non-negative dimensions then it is the answer.
			// If r is non-existant (has a negative dimension), then both
			// are non-existant and we can return any non-existant rectangle
			// as an answer. Thus, returning r meets that criterion.
			// Either way, r is our answer.
			return new Rectangle(r);
		}
		long rx2 = r.width;
		long ry2 = r.height;
		if ((rx2 | ry2) < 0) {
			return new Rectangle(this);
		}
		int tx1 = this.x;
		int ty1 = this.y;
		tx2 += tx1;
		ty2 += ty1;
		int rx1 = r.x;
		int ry1 = r.y;
		rx2 += rx1;
		ry2 += ry1;
		if (tx1 > rx1)
			tx1 = rx1;
		if (ty1 > ry1)
			ty1 = ry1;
		if (tx2 < rx2)
			tx2 = rx2;
		if (ty2 < ry2)
			ty2 = ry2;
		tx2 -= tx1;
		ty2 -= ty1;
		// tx2,ty2 will never underflow since both original rectangles
		// were already proven to be non-empty
		// they might overflow, though...
		if (tx2 > Integer.MAX_VALUE)
			tx2 = Integer.MAX_VALUE;
		if (ty2 > Integer.MAX_VALUE)
			ty2 = Integer.MAX_VALUE;
		return new Rectangle(tx1, ty1, (int) tx2, (int) ty2);
	}

	public JSONObject toJSONObject(){
		JSONObject result = new JSONObject();
		result.put("x", new JSONNumber(this.x));
		result.put("y", new JSONNumber(this.y));
		result.put("width", new JSONNumber(this.width));
		result.put("height", new JSONNumber(this.height));
		return result;
	}
	public void rotate(Point origin, double angle) {
				Point point1 = new Point(x, x + width);
				Point point2 = new Point(y, y + height);
				Point newPoint1 = rotatePoint(point1, origin, angle);
				Point newPoint2 = rotatePoint(point2, origin, angle);
				x = newPoint1.getX();
				y = newPoint1.getY();
				width = newPoint2.getX() - newPoint1.getX();
				height = newPoint2.getY() - newPoint1.getY();
			}

			private Point rotatePoint(Point origin, Point point, double angle) {
				Point normalizedPoint = new Point(point.getX() - origin.getX(), point.getY() - origin.getY());
				int rotatedX = (int)Math.round(normalizedPoint.getX() * Math.cos(angle) - normalizedPoint.getY() * Math.sin(angle));
				int rotatedY = (int)Math.round(normalizedPoint.getX() * Math.sin(angle) + normalizedPoint.getY() * Math.cos(angle));
				return new Point(point.getX() + rotatedX, point.getY() + rotatedY);
			}
}
