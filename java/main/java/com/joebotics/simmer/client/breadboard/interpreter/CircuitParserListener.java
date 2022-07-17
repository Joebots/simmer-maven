package com.joebotics.simmer.client.breadboard.interpreter;

import com.google.gwt.json.client.JSONObject;
import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.elcomp.CircuitNode;
import com.joebotics.simmer.client.elcomp.CircuitNodeLink;
import com.joebotics.simmer.client.gui.util.Point;

/**
 * Created by joe on 2/5/17.
 * @deprecated Used Standard Simmer model classes for the both apps
 */
public interface CircuitParserListener {
    public JSONObject toJSONObject();
    public void onCircuitNode(CircuitNode node);
    public void onCircuitNodeLink(CircuitNode node, CircuitNodeLink link);
    public void onStart();
    public void onEnd();
//    public ACET onAbstractCircuitElement(AbstractCircuitElement ace);
}
