package com.jobotics.simmer.client.elcomp.chips;

public class CC2NegElm extends CC2Elm {
	public CC2NegElm(int xx, int yy) {
		super(xx, yy, -1);
	}

	public Class<CC2Elm> getDumpClass() {
		return CC2Elm.class;
	}
}