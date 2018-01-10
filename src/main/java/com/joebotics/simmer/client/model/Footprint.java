package com.joebotics.simmer.client.model;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

/**
 * Created by gologuzov on 08.01.18.
 */
@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class Footprint {
    public String name;
    public Lead[] leads;

    @JsOverlay
    public final JSONObject toJSONObject(){
        JSONObject result = new JSONObject();
        result.put("name", new JSONString(name));
        JSONArray jsonLeads = new JSONArray();
        for (int i = 0; i < leads.length; i++) {
            jsonLeads.set(i, leads[i].toJSONObject());
        }
        result.put("leads", jsonLeads);
        return result;
    }

    @JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
    public static class Lead {
        public String number;
        public int col;
        public int row;

        @JsOverlay
        public final JSONObject toJSONObject() {
            JSONObject result = new JSONObject();
            result.put("number", new JSONString(number));
            result.put("col", new JSONNumber(col));
            result.put("row", new JSONNumber(row));
            return result;
        }
    }
}
