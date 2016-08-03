package com.joebotics.simmer.client.breadboard;

import com.joebotics.simmer.client.gui.util.Rectangle;

public interface Identifiable {
   public void setUUID(double UUID);
   public double getUUID();
   public void setBoundedBox(Rectangle boundedBox);
   public Rectangle getBoundedBox();
} 
