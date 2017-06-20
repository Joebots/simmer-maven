package com.joebotics.simmer.client.elcomp;

import com.google.gwt.junit.client.GWTTestCase;

public class GwtTestAbstractCircuitElement extends GWTTestCase {

	@Override
	public String getModuleName() {
		return "com.joebotics.simmer.simmerJUnit";
	}
	
	public void testToJSONObject() {
		ResistorElm elm = new ResistorElm(0, 0);
		elm.setPoints();

		String expected = "{\"name\":\""+ elm.getName() + "\", \"posts\":[{\"x\":0, \"y\":0},{\"x\":0, \"y\":0}]}";
		assertEquals(expected, elm.toJSONObject().toString());
	}
}
