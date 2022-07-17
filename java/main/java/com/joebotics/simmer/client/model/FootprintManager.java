package com.joebotics.simmer.client.model;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.joebotics.simmer.client.util.JSON;
import com.joebotics.simmer.client.util.MessageI18N;

import java.util.logging.Logger;

/**
 * Created by gologuzov on 08.01.18.
 */
public class FootprintManager {
    private static final Logger lager = Logger.getLogger(FootprintManager.class.getName());

    private Footprint[] footprints;

    public FootprintManager() {
        init();
    }

    public Footprint getFootprint(String name) {
        for (Footprint footprint : footprints) {
            if (name.equals(footprint.name)) {
                return footprint;
            }
        }
        return null;
    }

    private void init() {
        String url = "conf/footprints.json?v=" + Math.random();
        lager.info("loadFootprints:" + url);

        RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            requestBuilder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    GWT.log(MessageI18N.getMessage("File_Error_Response"), exception);
                }

                public void onResponseReceived(Request request, Response response) {
                    if (response.getStatusCode() == Response.SC_OK) {
                        String text = response.getText();
                        footprints = JSON.parse(text);
                    } else
                        lager.info(MessageI18N.getMessage("Bad_file_server_response") + response.getStatusText());
                }
            });
        } catch (RequestException e) {
            GWT.log(MessageI18N.getMessage("failed_file_reading"), e);
        }
    }
}
