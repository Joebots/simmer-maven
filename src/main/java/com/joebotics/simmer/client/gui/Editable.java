package com.joebotics.simmer.client.gui;



public interface Editable {

	EditInfo getEditInfo(int n);

	void setEditValue(int n, EditInfo ei);

}
