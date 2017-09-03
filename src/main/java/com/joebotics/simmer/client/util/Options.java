package com.joebotics.simmer.client.util;

import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.storage.client.Storage;

public class Options {
    private Storage storage;
    Dictionary defaultOptions;

    public Options(Dictionary defaultOptions) {
        storage = Storage.getLocalStorageIfSupported();
        this.defaultOptions = defaultOptions;
        if (storage == null) {
            throw new RuntimeException("storage not supported");
        }
    }
    
    public Integer getInteger(OptionKey key) {
        String value = getValue(key);
        return Integer.valueOf(value);
    }

    public Boolean getBoolean(OptionKey key) {
        String value = getValue(key);
        return Boolean.valueOf(value);
    }

    public String getValue(OptionKey key) {
        String value = storage.getItem(key.name());
        if (value == null) {
            value = defaultOptions.get(key.name());
        }
        return value;
    }

    public void setValue(OptionKey key, String value) {
        storage.setItem(key.name(), value);
    }

    public void setValue(OptionKey key, Boolean value) {
        setValue(key, String.valueOf(value));
    }

    public void setValue(OptionKey key, Integer value) {
        setValue(key, String.valueOf(value));
    }
}
