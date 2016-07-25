package com.joebotics.simmer.client.breadboard;

import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Rectangle;

public class CircuitComponent implements Identifiable{
	String type;
	String typeClassName;
	Class<AbstractCircuitElement>  typeClass;
	Rectangle boundedBox;
	Point[] posts;
	double UUID;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getTypeClassName() {
		return typeClassName;
	}
	public void setTypeClassName(String typeClassName) {
		this.typeClassName = typeClassName;
	}
	public Class<AbstractCircuitElement> getTypeClass() {
		return typeClass;
	}
	public void setTypeClass(Class<AbstractCircuitElement> typeClass) {
		this.typeClass = typeClass;
	}
	public Rectangle getBoundedBox() {
		return boundedBox;
	}
	public void setBoundedBox(Rectangle boundedBox) {
		this.boundedBox = boundedBox;
	}
	public Point[] getPosts() {
		return posts;
	}
	public void setPosts(Point[] posts) {
		this.posts = posts;
	}
	public double getUUID() {
		return UUID;
	}
	public void setUUID(double uUID) {
		UUID = uUID;
	}

}
