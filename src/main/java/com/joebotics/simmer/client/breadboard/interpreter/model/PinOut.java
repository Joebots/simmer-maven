package com.joebotics.simmer.client.breadboard.interpreter.model;

import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.util.Point;

/**
 * Created by joe on 8/7/16.
 */
public class PinOut extends Point {

    private int postNbr;
    private AbstractCircuitElement element;

    public PinOut(){}

    public PinOut(Point p){
        super(p);
    }

    public PinOut(Point p, int postNbr){
        super.setLocation(p);
        this.postNbr = postNbr;
    }

    public PinOut(Point p, int postNbr, AbstractCircuitElement element){
        super.setLocation(p);
        this.postNbr = postNbr;
        this.element = element;
    }

    public int getPostNbr() {
        return postNbr;
    }

    public void setPostNbr(int postNbr) {
        this.postNbr = postNbr;
    }

    public AbstractCircuitElement getElement() {
        return element;
    }

    public void setElement(AbstractCircuitElement element) {
        this.element = element;
    }

    public String toString(){
        return element + ":" + postNbr;
    }

    public String toJson(){
        return toJson().toString();
    }

    public JSONObject toJSONObject(){
        JSONObject result = new JSONObject();
        result.put("component", new JSONString(element.toString()));
        result.put("post", new JSONNumber(postNbr));
        result.put("x", new JSONNumber(getX()));
        result.put("y", new JSONNumber(getY()));
        return result;
    }
}
