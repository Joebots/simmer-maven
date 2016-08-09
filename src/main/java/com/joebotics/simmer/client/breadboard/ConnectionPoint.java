package com.joebotics.simmer.client.breadboard;

import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.util.Point;

/**
 * Created by joe on 8/7/16.
 */
public class ConnectionPoint extends Point {

    private int postNbr;
    private AbstractCircuitElement element;

    public ConnectionPoint(){}

    public ConnectionPoint(Point p){
        super.setLocation(p);
    }

    public ConnectionPoint(Point p, int postNbr){
        super.setLocation(p);
        this.postNbr = postNbr;
    }

    public ConnectionPoint(Point p, int postNbr, AbstractCircuitElement element){
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
        String result = "{";

        result += "\"component\":\"" + element.toString() + "\",";
        result += "\"post\":\"" + postNbr + "\",";
        result += "\"x\":\"" + getX() + "\",";
        result += "\"y\":\"" + getY() + "\"}";

        return result;
    }
}
