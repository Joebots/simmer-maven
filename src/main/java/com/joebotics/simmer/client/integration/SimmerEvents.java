package com.joebotics.simmer.client.integration;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * Created by joe on 2/12/17.
 */
public enum SimmerEvents implements Serializable, IsSerializable {

    CIRCUIT_WORKING("circuit.working"),
    CIRCUIT_BROKEN("circuit.broken"),
    CIRCUIT_BROKEN_NAN("circuit.broken.nan"),
    CIRCUIT_BROKEN_SINGULAR_MATRIX("circuit.broken.singular-matrix"),
    CIRCUIT_BROKEN_NO_PATH_FOR_CURRENT_SOURCE("circuit.broken.no-path-for-current-source"),
    CIRCUIT_BROKEN_VOLTAGE_SOURCE_LOOP("circuit.broken.voltage-source-loop"),
    CIRCUIT_BROKEN_CAPACITOR_LOOP("circuit.broken.capacitor-loop"),
    CIRCUIT_BROKEN_MATRIX_ERROR("circuit.broken.matrix-error"),
    CIRCUIT_STOPPED("circuit.stopped"),
    SYSTEM_ERROR("system.error"),
    SYSTEM_LOADED("system.loaded");

    public final String value;

    private SimmerEvents(String value){
        this.value = value;
    }
}
