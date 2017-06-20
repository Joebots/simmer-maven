package com.joebotics.simmer.client.breadboard.interpreter;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.joebotics.simmer.client.breadboard.interpreter.model.CircuitElement;
import com.joebotics.simmer.client.breadboard.interpreter.model.Connection;
import com.joebotics.simmer.client.breadboard.interpreter.model.PinOut;
import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.elcomp.ChipElm;
import com.joebotics.simmer.client.elcomp.CircuitNode;
import com.joebotics.simmer.client.elcomp.CircuitNodeLink;
import com.joebotics.simmer.client.gui.util.Point;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by joe on 2/6/17.
 * @deprecated Used Standard Simmer model classes for the both apps
 */
public class BreadboardCircuitParserListener implements CircuitParserListener {

    private Map<String, List<PinOut>> pinsForElement = new HashMap<>();
    private Map<String, List<PinOut>> linkedConnectionPoints = new HashMap<>();
    private Map<String, PinOut> connectionPoints = new HashMap<>();
    private Map<AbstractCircuitElement, List<Connection>> connectionsFor = new HashMap<>();
    private Map<String, List<Connection>> connectionsForByName = new HashMap<>();
    private List<AbstractCircuitElement> circuitElements = new ArrayList<>();
    private List<Connection> connections = new ArrayList<>();
    private Map<String, CircuitElement> elementsByName = new HashMap<>();

    private Comparator<PinOut> comparator = new Comparator<PinOut>() {
        @Override
        public int compare(PinOut o1, PinOut o2) {
            return o1.getPostNbr() > o2.getPostNbr()?1:0;
        }
    };

    private static final Logger lager = Logger.getLogger(BreadboardCircuitParserListener.class.getName());


    @Override
    public void onStart() {
    }

    @Override
    public void onCircuitNode(CircuitNode node) {
    }

    public void onCircuitNodeLink(CircuitNode node, CircuitNodeLink cnl) {
    	if (node.internal) {
    		return;
    	}
        Point p = cnl.getElm().getPost(cnl.getNum());
        PinOut cp = new PinOut(p, cnl.getNum(), cnl.getElm());

        connectionPoints.put(cp+"", cp);

        if( !pinsForElement.containsKey(cnl.getElm().toString()) ){
            pinsForElement.put(cnl.getElm().toString(), new ArrayList<PinOut>());
        }

        if( !pinsForElement.get(cnl.getElm().toString()).contains(cp) ){
            pinsForElement.get(cnl.getElm().toString()).add(cp);
        }

        if( !circuitElements.contains(cnl.getElm()) ){
            circuitElements.add(cnl.getElm());
        }

        if( !linkedConnectionPoints.containsKey(p.coords()) ){
            linkedConnectionPoints.put(p.coords(), new ArrayList<PinOut>());
        }

        if( !linkedConnectionPoints.get(p.coords()).contains(cp) ){
            linkedConnectionPoints.get(p.coords()).add(cp);
        }
    }

    @Override
    public void onEnd() {
        for( AbstractCircuitElement ce : circuitElements ){
            Map<String, PinOut> posts = new HashMap<>();

            CircuitElement el = new CircuitElement();
            el.setName(ce.toString());

            elementsByName.put(el.getName(), el);

            Collections.sort(pinsForElement.get(el.getName()), comparator);

            if( !connectionsFor.containsKey(ce) ){
                List l = new ArrayList<Connection>();
                connectionsFor.put(ce, l);
                connectionsForByName.put(ce.toString(), l);
            }

            handlePosts(ce, el);
        }
    }

    private void handlePosts(AbstractCircuitElement ce, CircuitElement el) {
        for( int i=0; i<ce.getPostCount(); i++ ) {
            PinOut cp = connectionPoints.get(ce + ":" + i);
            List<PinOut> links = linkedConnectionPoints.get(cp.coords());
            el.addPost(ce.getPost(i));

            for( PinOut cpt : links ){

                // don't include a link to ourself
                if( cpt.toString().equals(cp.toString()) )
                    continue;

                Connection c = new Connection(cp, cpt);
                connections.add(c);
                connectionsFor.get(cp.getElement()).add(c);

                if( cp.getElement().toString().equals(el.getName() ) ){
                    el.addPinOut(cpt);
                }
            }
        }
    }

    @Override
    public JSONObject toJSONObject(){
        JSONObject result = new JSONObject();
        result.put("bounds", createBoundsJSON());

        JSONObject connections = new JSONObject();
        result.put("components", connections);

        JSONArray elements = new JSONArray();
        result.put("elements", elements);
        int idx = 0;

        for( String name : elementsByName.keySet()){
            List<Connection> connectionsFor = connectionsForByName.get(name);

            JSONObject entry = new JSONObject();
            connections.put(name, entry);

            createJSONTargets(connectionsFor, entry);
            createJSONPinouts(name, entry);

            elements.set(idx++, new JSONString(name));
        }

        return result;
    }

    private void createJSONPinouts(String name, JSONObject entry) {
        List<PinOut> pinOuts = pinsForElement.get(name);
        JSONArray pinOutArr = new JSONArray();

        for( int j=0; j<pinOuts.size(); j++){
            JSONObject obj = new JSONObject();
            obj.put("x", new JSONNumber(pinOuts.get(j).getX()));
            obj.put("y", new JSONNumber(pinOuts.get(j).getY()));
            pinOutArr.set(j, obj);

        }

        entry.put("pinOuts", pinOutArr);
    }

    private void createJSONTargets(List<Connection> connectionsFor, JSONObject entry) {
        JSONObject targets = new JSONObject();

        for( int j=0; j<connectionsFor.size(); j++ ){
            Connection c = connectionsFor.get(j);
            String name = c.side2.getElement().toString();
            targets.put(name, c.toJSONObject(name));
        }

        entry.put("targets", targets);
    }

    private JSONObject createBoundsJSON(){
        JSONObject result = new JSONObject();

        for( AbstractCircuitElement ace : this.circuitElements ){
            result.put(ace.toString(), ace.getBoundingBox().toJSONObject());
        }

        return result;
    }
}
