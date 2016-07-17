package com.joebotics.simmer.client.gui.impl;



public interface Editable {

	EditInfo getEditInfo(int n);

	void setEditValue(int n, EditInfo ei);

}