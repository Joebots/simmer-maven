"use strict";

/** Common HSV hue for all blocks in this file. */
var GPIO_HUE = 250;

var PINS = GpioConfig;

Blockly.Blocks["pin_changed"] = {
    /**
     * Description.
     * 
     * @this Blockly.Block
     */
    init : function() {
        this.setHelpUrl("");
        this.setColour(GPIO_HUE);
        var arr = Object.keys(PINS).map(function(key) {
            return [ key, key ];
        });
        this.appendDummyInput()
            .appendField("when GPIO")
            .appendField(new Blockly.FieldDropdown(arr), "PIN")
            .appendField("changed")
        this.appendStatementInput("DO")
            .appendField("do");
        this.setTooltip("");
    }
};

/**
 * Description.
 * 
 * @param {!Blockly.Block}
 *            block Block to generate the code from.
 * @return {string} Completed code.
 */
Blockly.JavaScript["pin_changed"] = function(block) {
    var pin = block.getFieldValue("PIN");
    var callback = Blockly.JavaScript.statementToCode(block, "DO");
    var code = "gpioOn('change', " + pin + ", function(pin, value) {\n" +
    "    if (pin != " + pin + ") {\n" +
    "        return;\n" +
    "    }\n" +
    "    " + callback + "\n" +
    "});"
    return code;
};

Blockly.Blocks["pin_write"] = {
    /**
     * Description.
     * 
     * @this Blockly.Block
     */
    init : function() {
        this.setHelpUrl("");
        this.setColour(GPIO_HUE);
        var arr = Object.keys(PINS).map(function(key) {
            return [ key, key ];
        });
        this.appendValueInput("STATE", "pin_value")
            .appendField("set GPIO")
            .appendField(new Blockly.FieldDropdown(arr), "PIN")
            .appendField("to").setCheck("pin_value");
        this.setInputsInline(false);
        this.setPreviousStatement(true, null);
        this.setNextStatement(true, null);
        this.setTooltip("");
    }
};

/**
 * Description.
 * 
 * @param {!Blockly.Block}
 *            block Block to generate the code from.
 * @return {string} Completed code.
 */
Blockly.JavaScript["pin_write"] = function(block) {
    var pin = block.getFieldValue("PIN");
    var state = Blockly.JavaScript.valueToCode(block, "STATE",
            Blockly.JavaScript.ORDER_ATOMIC)
            || "0";
    state = (state === "HIGH") ? "true" : "false";
    var code = "gpioWrite(" + PINS[pin] + ", " + state + ");\n";
    return code;
};

Blockly.Blocks["pin_read"] = {
    /**
     * Description.
     * 
     * @this Blockly.Block
     */
    init : function() {
        this.setHelpUrl("");
        this.setColour(GPIO_HUE);
        var arr = Object.keys(PINS).map(function(key) {
            return [ key, key ];
        });
        this.appendDummyInput().appendField("get GPIO").appendField(
                new Blockly.FieldDropdown(arr), "PIN")
        this.setOutput(true, "pin_value");
        this.setTooltip("");
    }
};

/**
 * Description.
 * 
 * @param {!Blockly.Block}
 *            block Block to generate the code from.
 * @return {string} Completed code.
 */
Blockly.JavaScript["pin_read"] = function(block) {
    var pin = block.getFieldValue("PIN");
    var code = "gpioRead(" + PINS[pin] + ");\n";
    return [ code, Blockly.JavaScript.ORDER_ATOMIC ];
};

Blockly.Blocks["pin_binary"] = {
    /**
     * Description.
     * 
     * @this Blockly.Block
     */
    init : function() {
        this.setHelpUrl("");
        this.setColour(GPIO_HUE);
        this.appendDummyInput("")
            .appendField(new Blockly.FieldDropdown([ [ "HIGH", "HIGH" ],
                        [ "LOW", "LOW" ] ]), "STATE");
        this.setOutput(true, "pin_value");
        this.setTooltip("Set a pin state logic High or Low.");
    }
};

/**
 * Description.
 * 
 * @param {!Blockly.Block}
 *            block Block to generate the code from.
 * @return {array} Completed code with order of operation.
 */
Blockly.JavaScript["pin_binary"] = function(block) {
    var code = block.getFieldValue("STATE");
    return [ code, Blockly.JavaScript.ORDER_ATOMIC ];
};
