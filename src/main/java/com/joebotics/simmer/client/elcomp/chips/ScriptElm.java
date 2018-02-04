package com.joebotics.simmer.client.elcomp.chips;

import com.joebotics.simmer.client.elcomp.ChipElm;
import com.joebotics.simmer.client.elcomp.Pin;
import com.joebotics.simmer.client.elcomp.Side;
import com.joebotics.simmer.client.gui.EditInfo;
import com.joebotics.simmer.client.gui.util.Graphics;
import com.joebotics.simmer.client.util.Base64Util;
import com.joebotics.simmer.client.util.ScriptExecutor;
import com.joebotics.simmer.client.util.StringTokenizer;

import gwt.material.design.client.ui.MaterialCheckBox;
import gwt.material.design.client.ui.MaterialTextArea;

/**
 * Created by joe on 8/14/16.
 */
public class ScriptElm extends ChipElm {
	
	private int pinsCount;
	protected String scriptlet;
	private ScriptExecutor executor;
	
	public ScriptElm(int xx, int yy) {
		super(xx, yy);
		executor = new ScriptExecutor();
	}

	public ScriptElm(int xa, int ya, int xb, int yb, int f, StringTokenizer st) {
		super(xa, ya, xb, yb, f, st);
		executor = new ScriptExecutor();
		pinsCount = new Integer(st.nextToken()).intValue();
		if (st.hasMoreTokens()) {
			scriptlet = Base64Util.decodeString(st.nextToken());
		}
		setupPins();
	}
    
    @Override
	public void draw(Graphics g) {
		drawChip(g);
		String s = "JS";
		drawCenteredText(g, s, getX1() + (getX2() - getX1())/2, getY2(), true);
	}
    
	public String dump() {
		String dump = super.dump() + " " + pinsCount;
		if(scriptlet != null) {
			dump += " " + Base64Util.encodeString(scriptlet);
		}
		return dump;
	}
    
    @Override
	public void execute() {
    	if (scriptlet != null) {
    		executor.nativeCalcFunction(scriptlet, getPins());
    	}
	}
	
    @Override
	public int getDumpType() {
		return 300;
	}
    
	public int getPostCount() {
		return pinsCount;
	}

    @Override
    public int getVoltageSourceCount() {
    	int count = 0;
    	for (int i = 0; i < getPins().length; i++) {
    		if (getPins()[i].isOutput()) {
    			count++;
    		}
    	}
        return count;
    }
    
	public EditInfo getEditInfo(int n) {
		EditInfo ei;
		switch (n) {
		case 0:
			ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Flip X");
            ei.checkbox.setValue((getFlags() & FLAG_FLIP_X) != 0);
			return ei;
		case 1:
			ei = new EditInfo("", 0, -1, -1);
			ei.checkbox = new MaterialCheckBox("Flip Y");
            ei.checkbox.setValue((getFlags() & FLAG_FLIP_Y) != 0);
			return ei;
		case 2:
			ei = new EditInfo("# of Pins", pinsCount, 1, 8);
			ei.setDimensionless();
			return ei;
		case 3:
			ei = new EditInfo("Script");
			ei.texta = new MaterialTextArea();
			ei.texta.setText(scriptlet);
			ei.setDimensionless();
			return ei;
		}
		return null;
	}
	
	public void setEditValue(int n, EditInfo ei) {
		switch (n) {
		case 0:
			if (ei.checkbox.getValue())
				setFlags(getFlags() | FLAG_FLIP_X);
			else
				setFlags(getFlags() & ~FLAG_FLIP_X);
			break;
		case 1:
			if (ei.checkbox.getValue())
				setFlags(getFlags() | FLAG_FLIP_Y);
			else
				setFlags(getFlags() & ~FLAG_FLIP_Y);
			break;
		case 2:
			pinsCount = (int) ei.value;
			if (pinsCount != getPins().length) {
				setupPins();
			}
			break;
		case 3:
			scriptlet = ei.texta.getText();
			reset();
			break;
		}
		setPoints();
	}

    @Override
    public void setupPins() {
		setSizeX(3);
		int half = (int) Math.round(pinsCount/2d);
		setSizeY(half > 3 ? half : 3);
		Pin[] pins = new Pin[pinsCount];
    	for (int i = 0; i < getPostCount(); i++) {
    		int pos = i < half ? i : i - half;
     		pins[i] = new Pin(pos, i < half ? Side.WEST : Side.EAST, String.valueOf(i + 1));
    	}
    	setPins(pins);
		allocNodes();
    }
}
