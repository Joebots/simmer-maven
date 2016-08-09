package com.joebotics.simmer.client.breadboard;

import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.util.Rectangle;

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
}
