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

import java.util.List;
import java.util.Vector;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

public class CircuitNode {
	public boolean internal;
	public List<CircuitNodeLink> links;
	public int x, y;

	public CircuitNode() {
		links = new Vector<CircuitNodeLink>();
	}
	
    public JSONObject toJSONObject() {
        JSONObject result = new JSONObject();
        result.put("x", new JSONNumber(x));
        result.put("y", new JSONNumber(y));
        JSONArray jsonLinks = new JSONArray();
        for (int i = 0; i < links.size(); i++) {
        	jsonLinks.set(i, links.get(i).toJSONObject());
        }
        result.put("links", jsonLinks);
        return result;
    }
    
    public String toString() {
    	return toJSONObject().toString();
    }
}
