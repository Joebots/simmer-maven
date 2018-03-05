package com.joebotics.simmer.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.joebotics.simmer.client.elcomp.AbstractCircuitElement;
import com.joebotics.simmer.client.elcomp.CircuitNode;
import com.joebotics.simmer.client.elcomp.CircuitNodeLink;
import com.joebotics.simmer.client.model.Footprint;

public class CircuitModel {
    private String title;
    private List<AbstractCircuitElement> elmList;
    private List<CircuitNode> nodeList;

    public CircuitModel() {
        this.elmList = new ArrayList<AbstractCircuitElement>();
        resetNodeList();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AbstractCircuitElement> getElmList() {
        return elmList;
    }

    public Collection<AbstractCircuitElement> getOrderedElmList() {
        Set<AbstractCircuitElement> result = new LinkedHashSet<>();
        for (CircuitNode node : nodeList) {
            for (CircuitNodeLink link : node.links) {
                result.add(link.getElm());
            }
        }
        return result;
    }

    public List<CircuitNode> getNodeList() {
        return nodeList;
    }

    public List<CircuitNode> resetNodeList() {
        this.nodeList = new ArrayList<CircuitNode>();
        return this.nodeList;
    }

    public JSONObject toJSONObject() {
        JSONObject result = new JSONObject();
        result.put("bounds", createBoundsJSON());

        JSONArray components = new JSONArray();
        result.put("components", components);

        int idx = 0;
        for (AbstractCircuitElement element : elmList) {
            Map<CircuitNodeLink, Set<CircuitNodeLink>> links = getConnectionsLinks(element);

            JSONObject entry = new JSONObject();
            entry.put("name", new JSONString(element.getName()));
            components.set(idx++, entry);

            createJSONTargets(links, entry);
            createJSONPinouts(element, entry);
            createJSONFootprint(element, entry);
        }

        return result;
    }

    private Map<CircuitNodeLink, Set<CircuitNodeLink>> getConnectionsLinks(AbstractCircuitElement element) {
        Map<CircuitNodeLink, Set<CircuitNodeLink>> map = new HashMap<>();
        for (CircuitNode node : nodeList) {
            for (CircuitNodeLink link : node.links) {
                if (element.equals(link.getElm())) {
                    Set<CircuitNodeLink> links = new HashSet<CircuitNodeLink>(node.links);
                    links.remove(link);
                    map.put(link, links);
                }
            }
        }
        return map;
    }

    private void createJSONPinouts(AbstractCircuitElement element, JSONObject entry) {
        // TODO remove the pinOuts key (Compatibility with current version of Breadboard)
        JSONArray pinOutArr = new JSONArray();
        for (int i = 0; i < element.getPostCount(); i++) {
            JSONObject obj = new JSONObject();
            obj.put("x", new JSONNumber(element.getPost(i).getX()));
            obj.put("y", new JSONNumber(element.getPost(i).getY()));
            pinOutArr.set(i, obj);

        }
        entry.put("pinOuts", pinOutArr);

        // New implementation (Named Pins)
        JSONArray pins = new JSONArray();
        for (int i = 0; i < element.getPostCount(); i++) {
            pins.set(i, element.getPins()[i].toJSONObject());
        }
        entry.put("pins", pins);
    }

    private void createJSONTargets(Map<CircuitNodeLink, Set<CircuitNodeLink>> links, JSONObject entry) {
        JSONObject targets = new JSONObject();
        for (Map.Entry<CircuitNodeLink, Set<CircuitNodeLink>> item : links.entrySet()) {
            for (CircuitNodeLink link : item.getValue()) {
                String name = link.getElm().getName();
                targets.put(name, link.toJSONObject(item.getKey()));
            }
        }
        entry.put("targets", targets);
    }

    private void createJSONFootprint(AbstractCircuitElement element, JSONObject entry) {
        Footprint footprint = element.getFootprint();
        if (footprint != null) {
            entry.put("footprint", footprint.toJSONObject());
        }
    }

    private JSONObject createBoundsJSON() {
        JSONObject result = new JSONObject();

        for (AbstractCircuitElement ace : getOrderedElmList()) {
            result.put(ace.getName(), ace.getBoundingBox().toJSONObject());
        }

        return result;
    }
}
