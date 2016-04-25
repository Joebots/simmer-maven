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

package com.jobotics.simmer.client.gui.impl;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;

public class CheckboxMenuItem extends MenuItem implements Command {
	static final public String checkBoxHtml = "<div style=\"display:inline-block;width:15px;\">";
	private Command extcmd = null;
	private String name = "";
	private boolean on = false;
	private String shortcut = "";

	public CheckboxMenuItem(String s) {
		super(s, (Command) null);
		super.setScheduledCommand(this);
		name = s;
		setState(false);
	}

	public CheckboxMenuItem(String s, Command cmd) {
		super(s, (Command) null);
		super.setScheduledCommand(this);
		extcmd = cmd;
		name = s;
		setState(false);
	}

	public CheckboxMenuItem(String s, String c) {
		this(s);
		shortcut = c;
	}

	public CheckboxMenuItem(String s, String c, Command cmd) {
		this(s, cmd);
		shortcut = c;
	}

	public void addShortcut(String s) {
		shortcut = s;
	}

	public void execute() {
		setState(!on);
		if (extcmd != null)
			extcmd.execute();

	}

	public boolean getState() {
		return on;
	}

	public void setState(boolean newstate) {
		on = newstate;
		String s;
		if (on)
			// super.setHTML("&#10004;&nbsp;"+name);
			s = checkBoxHtml + "&#10004;</div>" + name;
		else
			// super.setHTML("&emsp;&nbsp;"+name);
			s = checkBoxHtml + "&nbsp;</div>" + name;
		if (shortcut != "")
			if (shortcut.length() == 1)
				s = s + "<div style=\"display:inline-block;width:20px;right:10px;text-align:center;position:absolute;\">" + shortcut + "</div>";
			else
				s = s + "<div style=\"display:inline-block;right:10px;text-align:right;position:absolute;\">" + shortcut + "</div>";
		setHTML(s);
	}

}
