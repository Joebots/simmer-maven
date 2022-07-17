package com.joebotics.simmer.client.util;

import java.util.List;

import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;

public class CircuitElementUtil {
	
	List<AbstractCircuitElement> elementList = null;
	
	CircuitElementUtil(List<AbstractCircuitElement> elmList) {
		elementList = elmList;
	}

	public AbstractCircuitElement getElm(int n) {
		if (n >= elementList.size())
			return null;

		return elementList.get(n);
	}
	
	public List<AbstractCircuitElement> getElmList() {
		return elementList;
	}
}
