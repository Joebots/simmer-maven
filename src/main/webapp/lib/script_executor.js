ScriptExecutor = function () {
	this.nativeCalcFunction = function(scriptlet, pins) {
		var f = new Function("pins", scriptlet);
		var result = f(pins);
		return result;
	};
};
