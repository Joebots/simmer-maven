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

package com.joebotics.simmer.client.util;

import com.joebotics.simmer.client.Simmer;

public class Inductor {
	public static final int FLAG_BACK_EULER = 2;
	double compResistance, current;
	double curSourceValue;
	int flags;

	double inductance;
	int nodes[];
	Simmer sim;

	public Inductor(Simmer s) {
		sim = s;
		nodes = new int[2];
	}

	public double calculateCurrent(double voltdiff) {
		// we check compResistance because this might get called
		// before stamp(), which sets compResistance, causing
		// infinite current
		if (compResistance > 0)
			current = voltdiff / compResistance + curSourceValue;
		return current;
	}

	public void doStep(double voltdiff) {
		sim.stampCurrentSource(nodes[0], nodes[1], curSourceValue);
	}

	public boolean isTrapezoidal() {
		return (flags & FLAG_BACK_EULER) == 0;
	}

	public boolean nonLinear() {
		return false;
	}

	public void reset() {
		current = 0;
	}

	public void setup(double ic, double cr, int f) {
		inductance = ic;
		current = cr;
		flags = f;
	}

	public void stamp(int n0, int n1) {
		// inductor companion model using trapezoidal or backward euler
		// approximations (Norton equivalent) consists of a current
		// source in parallel with a resistor. Trapezoidal is more
		// accurate than backward euler but can cause oscillatory behavior.
		// The oscillation is a real problem in circuits with switches.
		nodes[0] = n0;
		nodes[1] = n1;
		if (isTrapezoidal())
			compResistance = 2 * inductance / sim.getTimeStep();
		else
			// backward euler
			compResistance = inductance / sim.getTimeStep();
		sim.stampResistor(nodes[0], nodes[1], compResistance);
		sim.stampRightSide(nodes[0]);
		sim.stampRightSide(nodes[1]);
	}

	public void startIteration(double voltdiff) {
		if (isTrapezoidal())
			curSourceValue = voltdiff / compResistance + current;
		else
			// backward euler
			curSourceValue = current;
	}
}
