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

package com.jobotics.simmer.client.elcomp;

//import java.awt.*;
//import java.util.StringTokenizer;
import java.util.Vector;

import com.jobotics.simmer.client.gui.impl.Checkbox;
import com.jobotics.simmer.client.gui.impl.EditInfo;
import com.jobotics.simmer.client.gui.impl.GraphicElm;
import com.jobotics.simmer.client.gui.util.Font;
import com.jobotics.simmer.client.gui.util.Graphics;
import com.jobotics.simmer.client.util.StringTokenizer;

public class TextElm extends GraphicElm {
	final int FLAG_BAR = 2;
	final int FLAG_CENTER = 1;
	Vector<String> lines;
	int size;
	String text;

	public TextElm(int xx, int yy) {
		super(xx, yy);
		text = "hello";
		lines = new Vector<String>();
		lines.add(text);
		size = 24;
	}

	public TextElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f);
		size = new Integer(st.nextToken()).intValue();
		text = st.nextToken();
		while (st.hasMoreTokens())
			text += ' ' + st.nextToken();
		split();
	}

	public void drag(int xx, int yy) {
		setX(xx);
		setY(yy);
		setX2(xx + 16);
		setY2(yy);
	}

	public void draw(Graphics g) {
		// Graphics2D g2 = (Graphics2D)g;
		// g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		// RenderingHints.VALUE_ANTIALIAS_ON);
		Font oldfont = g.getFont();
		g.setColor(needsHighlight() ? selectColor : lightGrayColor);
		Font f = new Font("SansSerif", 0, size);
		g.setFont(f);
		// FontMetrics fm = g.getFontMetrics();
		int i;
		int maxw = -1;
		for (i = 0; i != lines.size(); i++) {
			// int w = fm.stringWidth((String) (lines.elementAt(i)));
			int w = (int) g.getContext().measureText((String) (lines.elementAt(i)))
					.getWidth();
			if (w > maxw)
				maxw = w;
		}
		int cury = getY();
		setBbox(getX(), getY(), getX(), getY());
		for (i = 0; i != lines.size(); i++) {
			String s = (String) (lines.elementAt(i));
			int sw = (int) g.getContext().measureText(s).getWidth();
			if ((getFlags() & FLAG_CENTER) != 0)
				setX((g.getContext().getCanvas().getWidth() - sw) / 2);
			g.drawString(s, getX(), cury);
			if ((getFlags() & FLAG_BAR) != 0) {
				int by = cury - g.getCurrentFontSize();
				g.drawLine(getX(), by, getX() + sw - 1, by);
			}
			adjustBbox(getX(), cury - g.getCurrentFontSize(), getX() + sw, cury + 3);
			cury += g.getCurrentFontSize() + 3;
		}
		setX2(getBoundingBox().x + getBoundingBox().width);
		setY2(getBoundingBox().y + getBoundingBox().height);
		g.setFont(oldfont);
	}

	public String dump() {
		return super.dump() + " " + size + " " + text;
	}

	public int getDumpType() {
		return 'x';
	}

	public EditInfo getEditInfo(int n) {
		if (n == 0) {
			EditInfo ei = new EditInfo("Text", 0, -1, -1);
			ei.text = text;
			return ei;
		}
		if (n == 1)
			return new EditInfo("Size", size, 5, 100);
		if (n == 2) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Center", (getFlags() & FLAG_CENTER) != 0);
			return ei;
		}
		if (n == 3) {
			EditInfo ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new Checkbox("Draw Bar On Top",
					(getFlags() & FLAG_BAR) != 0);
			return ei;
		}
		return null;
	}

	public void getInfo(String arr[]) {
		arr[0] = text;
	}

	@Override
	public int getShortcut() {
		return 't';
	}

	public boolean isCenteredText() {
		return (getFlags() & FLAG_CENTER) != 0;
	}

	public void setEditValue(int n, EditInfo ei) {
		if (n == 0) {
			text = ei.textf.getText();
			split();
		}
		if (n == 1)
			size = (int) ei.value;
		if (n == 3) {
			if (ei.checkbox.getState())
				setFlags(getFlags() | FLAG_BAR);
			else
				setFlags(getFlags() & ~FLAG_BAR);
		}
		if (n == 2) {
			if (ei.checkbox.getState())
				setFlags(getFlags() | FLAG_CENTER);
			else
				setFlags(getFlags() & ~FLAG_CENTER);
		}
	}

	void split() {
		int i;
		lines = new Vector<String>();
		StringBuffer sb = new StringBuffer(text);
		for (i = 0; i < sb.length(); i++) {
			char c = sb.charAt(i);
			if (c == '\\') {
				sb.deleteCharAt(i);
				c = sb.charAt(i);
				if (c == 'n') {
					lines.add(sb.substring(0, i));
					sb.delete(0, i + 1);
					i = -1;
					continue;
				}
			}
		}
		lines.add(sb.toString());
	}
}
