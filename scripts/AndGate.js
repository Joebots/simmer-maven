var value = true;
for(var i = 0; i < pins.length - 1; i++) {
	value = value && pins[i].getValue();
}
// The last pin is output
var outputPin = pins[pins.length - 1];
outputPin.setOutput(true);
outputPin.setValue(value);
