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

package com.jobotics.simmer.client.util;


public class HintTypeEnum {

	public enum HintType {
		HINT_UNSET(-1),
		HINT_LC(1),
		HINT_RC(2),
		HINT_3DB_C(3),
		HINT_TWINT(4),
		HINT_3DB_L(5);
		
		private int hintValue;
		
		private HintType(int value){
			this.hintValue = value;
		}
		
		public int getValue(){ 
		    return this.hintValue; 
		}
		
	    public HintType getHintFromValue(int value){
	        for(HintType h : HintType.values()){
	            if(value == h.hintValue){
	            	return h;
	            }
	        }
	        return HintType.HINT_UNSET; //or null ??
	    }
	}
}





