package com.joebotics.simmer.client.elcomp;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.joebotics.simmer.client.gui.util.Point;

import jsinterop.annotations.JsIgnore;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsType;

@JsType
public class Pin {
	public static final double VOLTAGE_HIGH_LEVEL = 5.0;
	public static final double VOLTAGE_THRESHOLD_LEVEL = 2.5;
	public static final double VOLTAGE_LOW_LEVEL = 0;

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

	@JsIgnore
	public Pin(int number, Side side, String text) {
		this(number, side, text, null);
	}
	
	@JsIgnore
	public Pin(int number, Side side, String text, String description) {
		this.number = number;
		this.side = side;
		this.text = text;
		this.description = description;
	}

	@JsMethod
	public int getNumber() {
		return number;
	}

	@JsIgnore
	public Side getSide() {
		return side;
	}

	@JsIgnore
	public Point getPost() {
		return post;
	}

	@JsIgnore
	public void setPost(Point post) {
		this.post = post;
	}

	@JsMethod
	public double getVoltage() {
		return voltage;
	}

	@JsMethod
	public void setVoltage(double voltage) {
		this.voltage = voltage;
	}

	@JsMethod
	public double getCurrent() {
		return current;
	}

	@JsMethod
	public void setCurrent(double current) {
		this.current = current;
	}

	@JsMethod
	public String getText() {
		return text;
	}

	@JsMethod
	public void setText(String text) {
		this.text = text;
	}

	@JsMethod
	public boolean isOutput() {
		return output;
	}

	@JsMethod
	public void setOutput(boolean output) {
		this.output = output;
	}

	@JsIgnore
	public int getVoltageSource() {
		return voltageSource;
	}

	@JsIgnore
	public void setVoltageSource(int voltageSource) {
		this.voltageSource = voltageSource;
	}
	
	@JsMethod
	public void setValue(Boolean value) {
		this.voltage = value ? VOLTAGE_HIGH_LEVEL : VOLTAGE_LOW_LEVEL;
	}
	
	@JsMethod
	public Boolean getValue() {
		return this.voltage > VOLTAGE_THRESHOLD_LEVEL;
	}

	@JsIgnore
	public boolean isLineOver() {
		return lineOver;
	}

	@JsIgnore
	public void setLineOver(boolean lineOver) {
		this.lineOver = lineOver;
	}

	@JsIgnore
	public boolean isBubble() {
		return bubble;
	}

	@JsIgnore
	public void setBubble(boolean bubble) {
		this.bubble = bubble;
	}

	@JsMethod
	public boolean isClock() {
		return clock;
	}

	@JsIgnore
	public void setClock(boolean clock) {
		this.clock = clock;
	}

	@JsIgnore
	public boolean isState() {
		return state;
	}

	@JsIgnore
	public boolean setState(boolean state) {
		this.state = state;
		return state;
	}

	@JsIgnore
	public Point getTextloc() {
		if (textloc == null && post != null && stub != null) {
			int dx = post.getX() - stub.getX();
			int dy = post.getY() - stub.getY();
			
			int xc = stub.getX() + dx / 4;
			int yc = stub.getY() + dy / 4;
			return new Point(xc - 5,  yc - 8);
		} else {
			return textloc;
		}
	}

	@JsIgnore
	public void setTextloc(Point textloc) {
		this.textloc = textloc;
	}

	@JsIgnore
	public int getBubbleX() {
		return bubbleX;
	}

	@JsIgnore
	public void setBubbleX(int bubbleX) {
		this.bubbleX = bubbleX;
	}

	@JsIgnore
	public int getBubbleY() {
		return bubbleY;
	}

	@JsIgnore
	public void setBubbleY(int bubbleY) {
		this.bubbleY = bubbleY;
	}

	@JsIgnore
	public Point getStub() {
		return stub;
	}

	@JsIgnore
	public void setStub(Point stub) {
		this.stub = stub;
	}

	@JsIgnore
	public double getCurcount() {
		return curcount;
	}

	@JsIgnore
	public void setCurcount(double curcount) {
		this.curcount = curcount;
	}
	
	@JsIgnore
    public String getDescription() {
		return description;
	}

	@JsIgnore
	public void setDescription(String description) {
		this.description = description;
	}

	@JsIgnore
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
