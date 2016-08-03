package com.joebotics.simmer.client.breadboard;

import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.gui.util.Point;
import com.joebotics.simmer.client.gui.util.Rectangle;

public class CircuitComponent implements Identifiable{
	String type;
	String typeClassName;
	Class<? extends AbstractCircuitElement>  typeClass;
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
	public Class<? extends AbstractCircuitElement> getTypeClass() {
		return typeClass;
	}
	public void setTypeClass(Class<? extends AbstractCircuitElement> typeClass) {
		this.typeClass = typeClass;
	}
	public Point[] getPosts() {
		return posts;
	}
	public void setPosts(Point[] posts) {
		this.posts = posts;
	}
	@Override
	public double getUUID() {
		return UUID;
	}
	@Override
	public void setUUID(double uUID) {
		UUID = uUID;
	}
	@Override
	public void setBoundedBox(Rectangle boundedBox) {
		this.boundedBox = boundedBox;
		
	}
	@Override
	public Rectangle getBoundedBox() {
		return boundedBox;
	}

}
