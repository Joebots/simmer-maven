"use strict";

/** Common HSV hue for all blocks in this file. */
var GPIO_HUE = 250;
var GPIO_GREEN = 65;

var PINS = GpioHardwareConfig;

Blockly.Blocks["pin_changed"] = {
    /**
     * Description.
     * 
     * @this Blockly.Block
     */
    init : function() {
        var arr = Object.keys(PINS).map(function(key) {
            return [ key, key ];
        });
        this.appendDummyInput()
            .appendField("when GPIO")
            .appendField(new Blockly.FieldDropdown(arr), "PIN")
            .appendField(new Blockly.FieldVariable("value"), "VALUE")
            .appendField("changed");
        this.appendStatementInput("DO")
            .appendField("do");
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setTooltip("");
        this.setColour(GPIO_GREEN);
        this.setHelpUrl("");
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
    var valueVar = Blockly.JavaScript.variableDB_.getName(block.getFieldValue('VALUE'), Blockly.Variables.NAME_TYPE);
    var callback = Blockly.JavaScript.statementToCode(block, "DO");
    var code = `gpioOn(${PINS[pin]}, function(pin, ${valueVar}) {\n` +
    `    ${callback}\n` +
    `});`;
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
        this.appendValueInput("VALUE", "pin_value")
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
    var value = Blockly.JavaScript.valueToCode(block, "VALUE",
            Blockly.JavaScript.ORDER_ATOMIC)
            || "0";
    var code = "gpioWrite(" + PINS[pin] + ", " + value + ");\n";
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

Blockly.Blocks["pin_digital"] = {
    /**
     * Description.
     * 
     * @this Blockly.Block
     */
    init : function() {
        this.setHelpUrl("");
        this.setColour(GPIO_HUE);
        this.appendDummyInput("")
            .appendField(new Blockly.FieldDropdown([ [ "HIGH", "1" ],
                        [ "LOW", "0" ] ]), "VALUE");
        this.setOutput(true, "pin_value");
        this.setTooltip("Set a value for digital pin High (1) or Low (0).");
    }
};

/**
 * Description.
 * 
 * @param {!Blockly.Block}
 *            block Block to generate the code from.
 * @return {array} Completed code with order of operation.
 */
Blockly.JavaScript["pin_digital"] = function(block) {
    var code = block.getFieldValue("VALUE");
    return [ code, Blockly.JavaScript.ORDER_ATOMIC ];
};

Blockly.Blocks["pin_analog"] = {
    /**
     * Analog Arduino pins
     * https://www.arduino.cc/reference/en/language/functions/analog-io/analogread/
     * @this Blockly.Block
     *
     */
    init : function() {
        this.setHelpUrl("");
        this.setColour(GPIO_HUE);
        this.appendDummyInput("")
            .appendField(new Blockly.FieldNumber("0", 0, 1023, 1), "VALUE");
        this.setOutput(true, "pin_value");
        this.setTooltip("Set a value for analog pin from 0 to 1023.");
    }
};

/**
 * Description.
 *
 * @param {!Blockly.Block}
 *            block Block to generate the code from.
 * @return {array} Completed code with order of operation.
 */
Blockly.JavaScript["pin_analog"] = function(block) {
    var code = block.getFieldValue("VALUE");
    return [ code, Blockly.JavaScript.ORDER_ATOMIC ];
};