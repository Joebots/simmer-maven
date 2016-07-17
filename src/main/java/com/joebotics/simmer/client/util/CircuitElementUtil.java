package com.joebotics.simmer.client.util;

import java.util.Vector;

import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;

public class CircuitElementUtil {
	
	Vector<AbstractCircuitElement> elementList = null;
	
	CircuitElementUtil(Vector<AbstractCircuitElement> elmList) {
		elementList = elmList;
	}

	public AbstractCircuitElement getElm(int n) {
		if (n >= elementList.size())
			return null;

		return elementList.elementAt(n);
	}
	
	public Vector<AbstractCircuitElement> getElmList() {
		return elementList;
	}
	
}
