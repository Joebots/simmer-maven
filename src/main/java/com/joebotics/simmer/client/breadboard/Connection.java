package com.joebotics.simmer.client.breadboard;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.util.Rectangle;
import org.eclipse.jetty.util.ajax.JSON;

public class Connection {
	public ConnectionPoint side1;
	public ConnectionPoint side2;

	public Connection(ConnectionPoint side1, ConnectionPoint side2 ){
		this.side1 = side1;
		this.side2 = side2;

		if (side1.toString().equals(side2.toString()) )
			throw new IllegalStateException("circular connection, both ends are equal");
	}

	public String toString(){
		return side1 + "<===>" + side2;
	}

	public String toJson(){
		String result = "\n{";

		result += "\"" + side1.getElement() + "\":" + side1.toJson() + ",";
		result += "\"" + side2.getElement() + "\":" + side2.toJson() + "}";

		return result;
	}

	public JSONObject toJSONObject(){
		JSONObject result = new JSONObject();
		result.put("side1", side1.toJSONObject());
		result.put("side2", side2.toJSONObject());
		return result;
	}

	public JSONObject toJSONObject(String parent){
		boolean side1isParent = side1.getElement().toString().equals(parent);

		// enclosing object
		JSONObject result = new JSONObject();
		result.put("x", new JSONNumber(side1.getX()));
		result.put("y", new JSONNumber(side1.getY()));
		result.put("post", new JSONNumber(side1isParent?side1.getPostNbr():side2.getPostNbr()));

		// enclosed object
		JSONObject target = new JSONObject();
		result.put("target", target);
		target.put("name", new JSONString(side1isParent?side2.getElement().toString():side1.getElement().toString()));
		target.put("post", new JSONNumber(side1isParent?side2.getPostNbr():side1.getPostNbr()));

		return result;
	}
}
