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

package com.joebotics.simmer.client.gui.util;

// info about each row/column of the matrix for simplification purposes
public class RowInfo {
	public static final int ROW_CONST = 1; // value is constant
	public static final int ROW_EQUAL = 2; // value is equal to another value
	public static final int ROW_NORMAL = 0; // ordinary value
	private boolean dropRow; // row is not needed in matrix
	private boolean lsChanges; // row's left side changes
	private int nodeEq;
	private int	type;
	private int	mapCol;
	private int	mapRow;
	private boolean rsChanges; // row's right side changes
	private double value;

	public RowInfo() {
		type = ROW_NORMAL;
	}

	public boolean isDropRow() {
		return dropRow;
	}

	public void setDropRow(boolean dropRow) {
		this.dropRow = dropRow;
	}

	public boolean isLsChanges() {
		return lsChanges;
	}

	public void setLsChanges(boolean lsChanges) {
		this.lsChanges = lsChanges;
	}

	public int getMapCol() {
		return mapCol;
	}

	public void setMapCol(int mapCol) {
		this.mapCol = mapCol;
	}

	public int getMapRow() {
		return mapRow;
	}

	public void setMapRow(int mapRow) {
		this.mapRow = mapRow;
	}

	public int getNodeEq() {
		return nodeEq;
	}

	public void setNodeEq(int nodeEq) {
		this.nodeEq = nodeEq;
	}

	public boolean isRsChanges() {
		return rsChanges;
	}

	public void setRsChanges(boolean rsChanges) {
		this.rsChanges = rsChanges;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
}
