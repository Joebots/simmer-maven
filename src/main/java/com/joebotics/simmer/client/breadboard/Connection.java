package com.joebotics.simmer.client.breadboard;

import com.joebotics.simmer.client.gui.util.Rectangle;

public class Connection implements Identifiable {
  double side1UUID;
  double side2UUID;
  int side1PostIndex;
  int side2PostIndex; 
  Rectangle boundedBox;
  double UUID;
	
	public double getSide1UUID() {
		return side1UUID;
	}
	public void setSide1UUID(double side1uuid) {
		side1UUID = side1uuid;
	}
	public double getSide2UUID() {
		return side2UUID;
	}
	public void setSide2UUID(double side2uuid) {
		side2UUID = side2uuid;
	}
	public int getSide1PostIndex() {
		return side1PostIndex;
	}
	public void setSide1PostIndex(int side1PostIndex) {
		this.side1PostIndex = side1PostIndex;
	}
	public int getSide2PostIndex() {
		return side2PostIndex;
	}
	public void setSide2PostIndex(int side2PostIndex) {
		this.side2PostIndex = side2PostIndex;
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
