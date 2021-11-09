(function(f){if(typeof exports==="object"&&typeof module!=="undefined"){module.exports=f()}else if(typeof define==="function"&&define.amd){define([],f)}else{var g;if(typeof window!=="undefined"){g=window}else if(typeof global!=="undefined"){g=global}else if(typeof self!=="undefined"){g=self}else{g=this}g.EventBus = f()}})(function(){var define,module,exports;return (function e(t,n,r){function s(o,u){if(!n[o]){if(!t[o]){var a=typeof require=="function"&&require;if(!u&&a)return a(o,!0);if(i)return i(o,!0);var f=new Error("Cannot find module '"+o+"'");throw f.code="MODULE_NOT_FOUND",f}var l=n[o]={exports:{}};t[o][0].call(l.exports,function(e){var n=t[o][1][e];return s(n?n:e)},l,l.exports,e,t,n,r)}return n[o].exports}var i=typeof require=="function"&&require;for(var o=0;o<r.length;o++)s(r[o]);return s})({1:[function(require,module,exports){
//var winston = require('winston');
//var bunyan = require('bunyan');

const Utils = require("jbutil");

/**
 * Emitter which supports subscription by regular 
 * expressions compared against the event namespace.
 * 
 * @class EventBus
 * @constructor
 */
function EventBus(config){

	// Listener functions
	var entries = [];

	// add a remove method to the entries array
	entries.remove = Utils.arrayRemove;

	var lager = new Utils.Logger({
		level : "info",
		name : "EventBus"
	});


	// logger
	// var lager = bunyan.createLogger({
	// 	name: 'EventBus',
	// 	level: 'info'
	// });

		// new winston.Logger({
		// transports: [
		// 	new winston.transports.Console({colorize:true, prettyPrint: true, timestamp: true, label: "EventBus"})
		// 	//,new (winston.transports.File)({ filename: 'somefile.log' })
		// 	]
		// });

	function exprs(){
		var result = [];

		for( var i in entries ){
			result.push(entries[i].expr);
		}

		return result;
	}

	function matches(texpr, expr){
		var regex = new RegExp(expr);
		return regex.test(texpr);
	}
	
	//- public signature
	/**
	 * A list of all the regexes to be compared against event names
	 * @method topics
	 */
	EventBus.prototype.topics = exprs;

	/**
	 * Get the current logger or set a new config for the logger
	 * @method logger
	 * @param config the new logging config to apply
	 * @returns the logger if no config object is passed
	 */
	EventBus.prototype.logger = function(config){
		if( !config )
			return lager;

		lager = new Utils.Logger({
			level : "info",
			name : "EventBus"
		});
	};

	/**
	 * Bind a listener function to a regex.  When an
	 * event is fired whose name matches the regex,
	 * the listener function is called.
	 *
	 * @method bind
	 * @param expr the regular expression string to test against events
	 * @param listener the listener handler function invoked if <code>expr</code> matches the event name
	 * @param weight the priority of execution for the listener.  This may be any
	 * valid javascript number.  The numerical values Number.POSITIVE_INFINITY and
	 * Number.NEGATIVE_INFINITY may be used to define listeners that always execute
	 * before and after an event is processed by "normally" weighted handlers.
	 *
	 * Normally weighted handlers may have any numerical value betwen Number.MIN_VALUE and Number.MAX_VALUE.
	 */
	EventBus.prototype.bind = function(expr, listener, weight){
		var entry = {expr: expr, listener: listener, weight: weight, ts: new Date()};
		entries.push(entry);
		lager.info("bound entry:", entry);
	};

	/**
	 * Unbind a listener function and a regex.
	 *
	 * @method unbind
	 * @param expr the regular expression string to test against events
	 * @param listener the listener handler function invoked if <code>expr</code> matches the event name
	 */
	EventBus.prototype.unbind = function(expr, listener){

		for( var i in entries ){
			var entry = entries[i];
			if( entry.expr === expr && (listener && entry.listener === listener)){
				entries.remove(i);
			}
		}
	};

	/**
	 * Fire an event
	 *
	 * @method fire
	 * @param expr the regular expression string to test against events
	 * @param evt the event data being passed to each handler.  As a
	 * chain executes for an event, each handler may alter, decorate or modify
	 * this data as necessary.
	 */
	EventBus.prototype.fire = function(expr, evt){

		if( !evt )
			evt = {};

		evt.expr = expr;

		lager.info("fire: %s", expr);

		var matchedEntries = [];
		var matchedNames = [];

		for( var i=0; i<entries.length; i++ ){
			var entry = entries[i];
			var match = Utils.matches(expr, entry.expr);

			if( match ){

				if( !entry.weight )
					entry.weight = 0;

				matchedEntries.push(entry);
				matchedNames.push(entry.expr);
			}
		}

		matchedEntries.sort(Utils.sorter);
		var vetoed = true;
		var weight = -1;
		var fired = false;

		lager.info("executing %s listeners", matchedEntries.length/*, matchedNames*/);

		for( var i=0; i<matchedEntries.length; i++ ){
			var entry = matchedEntries[i];
			lager.info("weight %s] running handler %s", entry.weight, entry.name || entry.expr);

			if( entry.weight !== weight ){
				weight = entry.weight;
				evt.weight = weight;
				lager.debug("=============== handling weight %s ===============", weight);
			}

			try{
				vetoed = entry.listener(evt);

				if(entry.weight != Number.NEGATIVE_INFINITY && entry.weight != Number.POSITIVE_INFINITY)
					fired = true;

				if( vetoed === false ){
					lager.warn("entry returned false, %s entries didn't fire", matchedEntries.length-i);
					return false;
				}

			}
			catch(e){
				lager.warn("caught exception %s firing handler %s, %s entries didn't fire", e, entry, matchedEntries.length-i);
				lager.error(e);

				if( config.failOnError ){
					lager.error("throwing error: ", e);
					throw e.stack?e:new Error(e);
				}
				else{
					lager.error("swallowing error", e);
				}
			}
		}

		var nomatchns = EventBus.events.NO_MATCHES_FOUND;
		if(!fired && expr !== nomatchns){
			lager.warn("No entries found for event", expr);
			this.fire(nomatchns, evt);
		}
	}

}

EventBus.events = {NO_MATCHES_FOUND : "mq.warn.no-listener-match"};
module.exports = EventBus;


},{"jbutil":2}],2:[function(require,module,exports){
(function (process){

/**
 * Encompasses a set of handlers for interpreting or rendering
 * of a model based on a pluggable set of codecs.  This process
 * is an implementation of "Content Negotiation".
 *
 * @class Util
 * @read-only
 * @singleton
 */
var Util = new function(){};


/**
 * Returns a functions caller name if parseable.
 * @param fnc the function
 * @method caller
 */
Util.caller = function(fnc){

	if(typeof(fnc) !== "string" )
		fnc = fnc.toString();

	//return f.toString().substring(0, f.toString().indexOf("{"));
	return fnc.replace("\n|\t", "").split(")")[0] + ")";
};

/**
 * Wrapper around new <code>RegExp(expr).test(texpr)</code>
 * @method matches
 * @param texpr test string
 * @param expr regular expression
 * @returns {boolean}
 */
Util.matches = function(texpr, expr){
	return new RegExp(expr).test(texpr);
};

/**
 * Sorting function for sorting EventBus handlers by weight
 * @param a
 * @param b
 * @returns {number}
 */
Util.sorter = function(a, b){

	if( !a || !b )
		return 0;

	return a.weight>b.weight?1:0;
};

/**
 * Remove a single or range of elements from an Array
 *
 * This should be part of the standard Array object in Javascript!
 * This function via John Ressig; MIT License.
 * @method arrayRemove
 * @param from
 * @param to
 * @returns {Array}
 */
Util.arrayRemove = function(from, to) {
	var rest = this.slice((to || from) + 1 || this.length);
	this.length = from < 0 ? this.length + from : from;
	return this.push.apply(this, rest);
};

/**
 * Returns either the compilers isArray method, or a fallback
 * @method isArray
 */
Util.isArray = (function () {
	// Use compiler's own isArray when available
	if (Array.isArray) {
		return Array.isArray;
	}

	// Retain references to variables for performance
	// optimization
	var objectToStringFn = Object.prototype.toString,
		arrayToStringResult = objectToStringFn.call([]);

	return function (subject) {
		return objectToStringFn.call(subject) === arrayToStringResult;
	};
}());

Util.Logger = function(config){

	Util.Logger.LogLevels = {
		trace : 0,
		debug : 1,
		info  : 2,
		warn  : 3,
		error : 4
	};

	var level = config.level && setLevel(config.level) || Util.Logger.LogLevels.info;
	var name = config.name || "";

	function parseLevel(l){
		for( var i in Util.Logger.LogLevels ){
			if( Util.Logger.LogLevels[i] === l )
				return i;
		}
	}

	function setLevel(l){
		if( !l )
			return level;

		var levels = Util.Logger.LogLevels;

		if( typeof(l) == "string" ){
			level = levels[l];
		}

		switch(l){

			case levels.debug:
				level = levels.debug;
				return;

			case levels.error:
				level = levels.error;
				return;

			case levels.info:
				level = levels.info;
				return;

			case levels.trace:
				level = levels.trace;
				return;

			case levels.warn:
				level = levels.warn;
				return;
		}
	}

	function logit(){
		var args = arguments[1];
		var l = arguments[0];

		if( arguments[0] >= level && args )
		{
			if( process && process.stdout ){
				process.stdout.write(new Date().toISOString() + " == " + parseLevel(l) + " ==} ");
			}

			console.log.apply(console, args || "");
		}
	}

	Util.Logger.prototype.debug  = function(){logit(Util.Logger.LogLevels.debug, arguments)};
	Util.Logger.prototype.info   = function(){logit(Util.Logger.LogLevels.info,  arguments)};
	Util.Logger.prototype.warn   = function(){logit(Util.Logger.LogLevels.warn,  arguments)};
	Util.Logger.prototype.error  = function(){logit(Util.Logger.LogLevels.error, arguments)};
	Util.Logger.prototype.trace  = function(){logit(Util.Logger.LogLevels.trace, arguments)};

	Util.Logger.prototype.level = function(l){
		setLevel(l);
	};

	Util.Logger.prototype.setName = function(msg){
	};

};

module.exports = Util;
}).call(this,require('_process'))
},{"_process":3}],3:[function(require,module,exports){
// shim for using process in browser

var process = module.exports = {};

// cached from whatever global is present so that test runners that stub it
// don't break things.  But we need to wrap it in a try catch in case it is
// wrapped in strict mode code which doesn't define any globals.  It's inside a
// function because try/catches deoptimize in certain engines.

var cachedSetTimeout;
var cachedClearTimeout;

(function () {
  try {
    cachedSetTimeout = setTimeout;
  } catch (e) {
    cachedSetTimeout = function () {
      throw new Error('setTimeout is not defined');
    }
  }
  try {
    cachedClearTimeout = clearTimeout;
  } catch (e) {
    cachedClearTimeout = function () {
      throw new Error('clearTimeout is not defined');
    }
  }
} ())
var queue = [];
var draining = false;
var currentQueue;
var queueIndex = -1;

function cleanUpNextTick() {
    if (!draining || !currentQueue) {
        return;
    }
    draining = false;
    if (currentQueue.length) {
        queue = currentQueue.concat(queue);
    } else {
        queueIndex = -1;
    }
    if (queue.length) {
        drainQueue();
    }
}

function drainQueue() {
    if (draining) {
        return;
    }
    var timeout = cachedSetTimeout(cleanUpNextTick);
    draining = true;

    var len = queue.length;
    while(len) {
        currentQueue = queue;
        queue = [];
        while (++queueIndex < len) {
            if (currentQueue) {
                currentQueue[queueIndex].run();
            }
        }
        queueIndex = -1;
        len = queue.length;
    }
    currentQueue = null;
    draining = false;
    cachedClearTimeout(timeout);
}

process.nextTick = function (fun) {
    var args = new Array(arguments.length - 1);
    if (arguments.length > 1) {
        for (var i = 1; i < arguments.length; i++) {
            args[i - 1] = arguments[i];
        }
    }
    queue.push(new Item(fun, args));
    if (queue.length === 1 && !draining) {
        cachedSetTimeout(drainQueue, 0);
    }
};

// v8 likes predictible objects
function Item(fun, array) {
    this.fun = fun;
    this.array = array;
}
Item.prototype.run = function () {
    this.fun.apply(null, this.array);
};
process.title = 'browser';
process.browser = true;
process.env = {};
process.argv = [];
process.version = ''; // empty string to avoid regexp issues
process.versions = {};

function noop() {}

process.on = noop;
process.addListener = noop;
process.once = noop;
process.off = noop;
process.removeListener = noop;
process.removeAllListeners = noop;
process.emit = noop;

process.binding = function (name) {
    throw new Error('process.binding is not supported');
};

process.cwd = function () { return '/' };
process.chdir = function (dir) {
    throw new Error('process.chdir is not supported');
};
process.umask = function() { return 0; };

},{}]},{},[1])(1)
});