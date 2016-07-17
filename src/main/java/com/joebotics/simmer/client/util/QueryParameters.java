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

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.http.client.URL;

public class QueryParameters {
	private Map<String, String> map = new HashMap<String, String>();

	public QueryParameters() {
		String search = getQueryString();
		if ((search != null) && (search.length() > 0)) {
			String[] nameValues = search.substring(1).split("&");
			for (int i = 0; i < nameValues.length; i++) {
				String[] pair = nameValues[i].split("=");

				map.put(pair[0], URL.decode(pair[1]));
			}
		}
	}

	public boolean getBooleanValue(String key, boolean def) {
		String val = getValue(key);
		if (val == null)
			return def;
		else
			return (val == "1" || val.equalsIgnoreCase("true"));
	}

	private native String getQueryString()
	/*-{
	      return $wnd.location.search;
	}-*/;

	public String getValue(String key) {
		return (String) map.get(key);
	}
}
