package com.joebotics.simmer.client.breadboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;






public class CircuitLibrary extends HashMap<Double,Identifiable> {

   List<Connection> getConnectionsFor(CircuitComponent component){
	   List<Connection> returnList = new ArrayList<>(); 
	   double componentUUID = component.getUUID();
	   for (Map.Entry<Double, Identifiable> entry : this.entrySet()){
		   if (entry.getValue() instanceof CircuitComponent) continue;
		   Connection connection = (Connection) entry.getValue();
		   if (connection.getSide1UUID() == componentUUID || connection.getSide2UUID() == componentUUID){
			   returnList.add(connection);
		   }
	   }
	  return returnList;	
   }
   List<CircuitComponent> getComponentsFor(Connection connection){
	   List<CircuitComponent> returnList = new ArrayList<>(); 
	
	   returnList.add((CircuitComponent) this.get(connection.getSide1UUID()));
	   returnList.add((CircuitComponent) this.get(connection.getSide2UUID()));
		 
	  return returnList;
   }
   boolean isConnected(Identifiable identifiable){
	   return false;
   }

}
