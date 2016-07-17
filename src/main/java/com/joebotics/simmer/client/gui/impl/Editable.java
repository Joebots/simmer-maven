package com.jobotics.simmer.client.gui.impl;



public interface Editable {

	public abstract EditInfo getEditInfo(int n);

	public abstract void setEditValue(int n, EditInfo ei);

}