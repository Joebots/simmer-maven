package com.joebotics.simmer.client.breadboard.interpreter.model;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;

/**
 * @deprecated Use {@code CircuitNodeLink} instead of the class *
 */
public class Connection {
	public PinOut side1;
	public PinOut side2;

	public Connection(PinOut side1, PinOut side2 ){
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
//		result.put("name", new JSONString(side1isParent?side1.getElement().toString():side2.getElement().toString()));
		result.put("post", new JSONNumber(side1isParent?side1.getPostNbr():side2.getPostNbr()));
		result.put("target", side1isParent?side2.toJSONObject():side1.toJSONObject());

		return result;
	}
}
