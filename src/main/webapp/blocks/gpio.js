/**
 * @license Licensed under the Apache License, Version 2.0 (the "License"):
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 * @fileoverview Description.
 */
'use strict';

/** Common HSV hue for all blocks in this file. */
var GPIO_HUE = 250;

var PINS = [['2', '3'], ['3', '5'], ['4', '7'], ['17', '11'], ['27', '13'],
            ['22', '15'], ['10', '19'], ['9', '21'], ['11', '23'],
            ['14', '8'], ['15', '10'], ['18', '12'], ['23', '16'], ['24', '18'],
            ['25', '22'], ['8', '24'], ['7', '26']];

Blockly.Blocks['led_set'] = {
  /**
   * Description.
   * @this Blockly.Block
   */
  init: function() {
    this.setHelpUrl('');
    this.setColour(GPIO_HUE);
    this.appendValueInput('STATE', 'pin_value')
        .appendField('set LED on pin#')
        .appendField(new Blockly.FieldDropdown(PINS), 'PIN')
        .appendField('to')
        .setCheck('pin_value');
    this.setInputsInline(false);
    this.setPreviousStatement(true, null);
    this.setNextStatement(true, null);
    this.setTooltip('');
  }
};

/**
 * Description.
 * @param {!Blockly.Block} block Block to generate the code from.
 * @return {string} Completed code.
 */
Blockly.JavaScript['led_set'] = function(block) {
  var pin = block.getFieldValue('PIN');
  var state = Blockly.JavaScript.valueToCode(
      block, 'STATE', Blockly.JavaScript.ORDER_ATOMIC) || '0';
  state = (state === 'HIGH') ? 'true' : 'false';
  var code = 'setDiagramPin(' + pin + ', ' + state + ');\n';
  return code;
};

/**
 * Description.
 * @param {!Blockly.Block} block Block to generate the code from.
 * @return {string} Completed code.
 */
Blockly.Python['led_set'] = function(block) {
  var pin = block.getFieldValue('PIN');
  // Very hackish way to get the BMC pin number, need to create a proper look
  // up dicionary with a function to generate the dropdown
  for (var i = 0; i < PINS.length; i++) {
    if (PINS[i][1] == pin) {
      pin = PINS[i][0];
      break;
    }
  }
  var state = Blockly.Python.valueToCode(
      block, 'STATE', Blockly.Python.ORDER_ATOMIC) || '0';

  Blockly.Python.definitions_['import_gpiozero'] = 'from gpiozero import LED';
  Blockly.Python.definitions_['declare_led' + pin] =
      'led' + pin + ' = LED(' + pin + ')';

  var code = 'led' + pin + '.'
  if (state == 'HIGH') {
    code += 'on()\n';
  } else {
    code += 'off()\n';
  }
  return code;
};

Blockly.Blocks['pin_binary'] = {
  /**
   * Description.
   * @this Blockly.Block
   */
  init: function() {
    this.setHelpUrl('');
    this.setColour(GPIO_HUE);
    this.appendDummyInput('')
        .appendField(
            new Blockly.FieldDropdown([['HIGH', 'HIGH'], ['LOW', 'LOW']]),
           'STATE');
    this.setOutput(true, 'pin_value');
    this.setTooltip('Set a pin state logic High or Low.');
  }
};

/**
 * Description.
 * @param {!Blockly.Block} block Block to generate the code from.
 * @return {array} Completed code with order of operation.
 */
Blockly.JavaScript['pin_binary'] = function(block) {
  var code = block.getFieldValue('STATE');
  return [code, Blockly.JavaScript.ORDER_ATOMIC];
};

/**
 * Description.
 * @param {!Blockly.Block} block Block to generate the code from.
 * @return {array} Completed code with order of operation.
 */
Blockly.Python['pin_binary'] = function(block) {
  var code = block.getFieldValue('STATE');
  return [code, Blockly.Python.ORDER_ATOMIC];
};
