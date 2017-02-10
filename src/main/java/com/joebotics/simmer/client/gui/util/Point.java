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

package com.joebotics.simmer.client.gui.util;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

import java.io.Serializable;

public class Point implements Serializable{
	
	private int x;
	private int y;

	public Point() {
		x = 0;
		y = 0;
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Point(Point p) {
		setLocation(p);
	}

	public void setLocation(Point p) {
		x = p.x;
		y = p.y;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public String coords(){
		return "x:" + x + ",y:" + y;
	}

	public String toString(){
		return super.toString() + "::x=" + x + ",y=" + y;
	}

	public boolean equals(Point p){
		return p.x == x && p.y == y;
	}

	public JSONObject toJSONObject(){
		JSONObject result = new JSONObject();
		result.put("x", new JSONNumber(x));
		result.put("y", new JSONNumber(y));
		return result;
	}
}