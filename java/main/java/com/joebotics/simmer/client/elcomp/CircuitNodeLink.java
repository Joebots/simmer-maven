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

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.joebotics.simmer.client.gui.util.Point;

public class CircuitNodeLink {
	private AbstractCircuitElement elm;
	private int num;
	public AbstractCircuitElement getElm() {
		return elm;
	}
	public void setElm(AbstractCircuitElement elm) {
		this.elm = elm;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	
    public JSONObject toJSONObject() {
        JSONObject result = new JSONObject();
        result.put("component", new JSONString(elm.getName()));
        result.put("post", new JSONNumber(num));
        return result;
    }
	
    // TODO refactoring
    public JSONObject toJSONObject(CircuitNodeLink sourceLink){
        JSONObject result = new JSONObject();
        result.put("post", new JSONNumber(num));
        
        JSONObject target = new JSONObject();
        target.put("component", new JSONString(sourceLink.elm.getName()));
        target.put("post", new JSONNumber(sourceLink.num));
        Point post = sourceLink.elm.getPost(sourceLink.num);
        target.put("x", new JSONNumber(post.getX()));
        target.put("y", new JSONNumber(post.getY()));
        result.put("target", target);
        
        return result;
    }
}
