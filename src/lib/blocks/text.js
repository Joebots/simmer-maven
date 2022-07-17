"use strict";

/** Overwriting JavaScript generator to print into the html "fake console" */
Blockly.JavaScript["text_print"] = function(block) {
  // Print statement.
  var argument0 = Blockly.JavaScript.valueToCode(block, "TEXT",
      Blockly.JavaScript.ORDER_NONE) || "\"\"";
  return "println(" + argument0 + ");\n";
};
