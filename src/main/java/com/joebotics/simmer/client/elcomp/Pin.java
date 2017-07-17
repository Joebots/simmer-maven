package com.joebotics.simmer.client.elcomp;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.joebotics.simmer.client.gui.util.Point;

public class Pin {
	private static final double VOLTAGE_HIGH_LEVEL = 5.0;
	private static final double VOLTAGE_THRESHOLD_LEVEL = 2.5;
	private static final double VOLTAGE_LOW_LEVEL = 0;

    private int number;
    private Side side;
    private String text;
    private String description;
    private Point post;
    private Point stub;
	private boolean output;
    
    private double voltage;
	private double current;
	
	// TODO: find out
	private int voltageSource;
	private boolean lineOver;
	private boolean bubble;
	private boolean clock;
	private boolean state;
	private Point textloc;
	private int bubbleX;
	private int bubbleY;
	private double curcount;

	public Pin(int number, Side side, String text) {
		this(number, side, text, null);
	}
	
	public Pin(int number, Side side, String text, String description) {
		this.number = number;
		this.side = side;
		this.text = text;
		this.description = description;
	}

	public int getNumber() {
		return number;
	}

	public Side getSide() {
		return side;
	}

	public Point getPost() {
		return post;
	}

	public void setPost(Point post) {
		this.post = post;
	}

	public double getVoltage() {
		return voltage;
	}

	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}

	public double getCurrent() {
		return current;
	}

	public void setCurrent(double current) {
		this.current = current;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isOutput() {
		return output;
	}

	public void setOutput(boolean output) {
		this.output = output;
	}

	public int getVoltageSource() {
		return voltageSource;
	}

	public void setVoltageSource(int voltageSource) {
		this.voltageSource = voltageSource;
	}
	
	public void setValue(Boolean value) {
		this.voltage = VOLTAGE_HIGH_LEVEL;
	}
	
	public Boolean isValue() {
		return this.voltage > VOLTAGE_THRESHOLD_LEVEL;
	}

	public boolean isLineOver() {
		return lineOver;
	}

	public void setLineOver(boolean lineOver) {
		this.lineOver = lineOver;
	}

	public boolean isBubble() {
		return bubble;
	}

	public void setBubble(boolean bubble) {
		this.bubble = bubble;
	}

	public boolean isClock() {
		return clock;
	}

	public void setClock(boolean clock) {
		this.clock = clock;
	}

	public boolean isState() {
		return state;
	}

	public boolean setState(boolean state) {
		this.state = state;
		return state;
	}

	public Point getTextloc() {
		if (textloc == null && post != null && stub != null) {
			int dx = post.getX() - stub.getX();
			int dy = post.getY() - stub.getY();
			
			int xc = stub.getX() + dx / 4;
			int yc = stub.getY() + dy / 4;
			return new Point(xc + 5,  yc + 5);
		} else {
			return textloc;
		}
	}

	public void setTextloc(Point textloc) {
		this.textloc = textloc;
	}

	public int getBubbleX() {
		return bubbleX;
	}

	public void setBubbleX(int bubbleX) {
		this.bubbleX = bubbleX;
	}

	public int getBubbleY() {
		return bubbleY;
	}

	public void setBubbleY(int bubbleY) {
		this.bubbleY = bubbleY;
	}

	public Point getStub() {
		return stub;
	}

	public void setStub(Point stub) {
		this.stub = stub;
	}

	public double getCurcount() {
		return curcount;
	}

	public void setCurcount(double curcount) {
		this.curcount = curcount;
	}
	
    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public JSONObject toJSONObject() {
        JSONObject result = new JSONObject();
        result.put("number", new JSONNumber(number));
        if (text != null) {
            result.put("text", new JSONString(text));        	
        }
        if (description != null) {
            result.put("description", new JSONString(description));        	
        }
        if (post != null) {
            JSONObject object = new JSONObject();
            object.put("x", new JSONNumber(post.getX()));
            object.put("y", new JSONNumber(post.getY()));
            result.put("post", object);
        }
        return result;
    }
    
    public String toString() {
    	return toJSONObject().toString();
    }
}