package com.joebotics.simmer.client.breadboard.interpreter.model;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.joebotics.simmer.client.gui.util.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * @deprecated Use {@code AbstractCircuitElement} instead of the class
 * Created by joe on 2/8/17.
 */
public class CircuitElement {
    private String name;
    private List<PinOut> connections = new ArrayList<>();
    private List<Point> posts = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PinOut> getPinOuts() {
        return connections;
    }

    public void setPinOuts(List<PinOut> connections) {
        this.connections = connections;
    }

    public void addPinOut(PinOut pinOut){
        this.connections.add(pinOut);
    }

    public List<Point> getPosts() {
        return posts;
    }

    public void setPosts(List<Point> posts) {
        this.posts = posts;
    }

    public JSONObject toJSONObject(){
        JSONObject result = new JSONObject();
        result.put("name", new JSONString(name));

        JSONArray pins = new JSONArray();
        for( int i=0; i<connections.size(); i++ ){
            pins.set(i, connections.get(i).toJSONObject());
        }
        result.put("connections", pins);

        JSONArray posts = new JSONArray();
        for( int i=0; i<this.posts.size(); i++ ){
            posts.set(i, this.posts.get(i).toJSONObject());
        }
        result.put("posts", posts);

        return result;
    }

    public void addPost(Point post) {
        posts.add(post);
    }
}
