package com.joebotics.simmer.client.breadboard;

import java.util.HashMap;
import java.util.List;






public class CircuitLibrary extends HashMap<Double,Identifiable> {

   List<Connection> getConnectionsFor(CircuitComponent component){
	   return null;
   }
   List<CircuitComponent> getComponentsFor(Connection connection){
	   return null;
   }
   boolean isConnected(Identifiable identifiable){
	   return false;
   }

}
