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

package com.joebotics.simmer.client.elcomp.chips;

import com.joebotics.simmer.client.elcomp.ChipElm;
import com.joebotics.simmer.client.util.StringTokenizer;

//import java.awt.*;
//import java.util.StringTokenizer;

public class SevenSegDecoderElm extends ChipElm {

	private static final boolean[][] symbols = {
			{ true, true, true, true, true, true, false },// 0
			{ false, true, true, false, false, false, false },// 1
			{ true, true, false, true, true, false, true },// 2
			{ true, true, true, true, false, false, true },// 3
			{ false, true, true, false, false, true, true },// 4
			{ true, false, true, true, false, true, true },// 5
			{ true, false, true, true, true, true, true },// 6
			{ true, true, true, false, false, false, false },// 7
			{ true, true, true, true, true, true, true },// 8
			{ true, true, true, false, false, true, true },// 9
			{ true, true, true, false, true, true, true },// A
			{ false, false, true, true, true, true, true },// B
			{ true, false, false, true, true, true, false },// C
			{ false, true, true, true, true, false, true },// D
			{ true, false, false, true, true, true, true },// E
			{ true, false, false, false, true, true, true },// F
	};

	public SevenSegDecoderElm(int xx, int yy) {
		super(xx, yy);
	}

	public SevenSegDecoderElm(int xa, int ya, int xb, int yb, int f,
			StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
	}

	public void execute() {
		int input = 0;
		if (getPins()[7].isValue())
			input += 8;
		if (getPins()[8].isValue())
			input += 4;
		if (getPins()[9].isValue())
			input += 2;
		if (getPins()[10].isValue())
			input += 1;

		for (int i = 0; i < 7; i++) {
			getPins()[i].setValue(symbols[input][i]);
		}
	}

	public String getChipName() {
		return "Seven Segment LED Decoder";
	}

	public int getDumpType() {
		return 197;
	}

	public int getPostCount() {
		return 11;
	}

	public int getVoltageSourceCount() {
		return 7;
	}

	boolean hasReset() {
		return false;
	}

	public void setupPins() {
		setSizeX(3);
		setSizeY(7);
		setPins(new Pin[getPostCount()]);

		getPins()[7] = new Pin(0, SIDE_W, "I3");
		getPins()[8] = new Pin(1, SIDE_W, "I2");
		getPins()[9] = new Pin(2, SIDE_W, "I1");
		getPins()[10] = new Pin(3, SIDE_W, "I0");

		getPins()[0] = new Pin(0, SIDE_E, "a");
		getPins()[0].setOutput(true);
		getPins()[1] = new Pin(1, SIDE_E, "b");
		getPins()[1].setOutput(true);
		getPins()[2] = new Pin(2, SIDE_E, "c");
		getPins()[2].setOutput(true);
		getPins()[3] = new Pin(3, SIDE_E, "d");
		getPins()[3].setOutput(true);
		getPins()[4] = new Pin(4, SIDE_E, "e");
		getPins()[4].setOutput(true);
		getPins()[5] = new Pin(5, SIDE_E, "f");
		getPins()[5].setOutput(true);
		getPins()[6] = new Pin(6, SIDE_E, "g");
		getPins()[6].setOutput(true);
	}

}
