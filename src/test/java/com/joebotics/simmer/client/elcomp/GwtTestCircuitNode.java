package com.joebotics.simmer.client.elcomp;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestCircuitNode extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "com.joebotics.simmer.simmerJUnit";
	}
	
	public void testToJSONObject() {
		CircuitNode node = new CircuitNode();
		node.x = 0;
		node.y = 0;
		
		String expected = "{\"x\":0, \"y\":0, \"links\":[]}";
		assertEquals(expected, node.toJSONObject().toString());
	}
}
