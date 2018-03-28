/***********************************************************************************************************/
//                                               Controller                                                //
/***********************************************************************************************************/
var Controller = function (view) {
    this.view = view;
    this.anim = 500;
    this.offsetY = -7.25;
    this.offsetX = -7.25;
}

Controller.DEFAULT_INSTRUCTIONS =
    "<ul style='font-size: 13px; list-style: none; font-style: italic; width: 90%;'>" +
    "<li>To build the circuit on the screen, click next and back." +
    "<li>The component you're wiring up will flash on the schematic." +
    "<li>Click the flash for a picture of the component." +
    "<li>The breadboard will light up and show you where to plug in the component.</ul>"

Controller.PIN_INFO = {
    "LED": {
        inverted: true,
        hints: ["the positive wire is longer"]
    },
    "Switch": {
        inverted: true
    }
};

Controller.prototype.bindEventHandlers = function () {
    var view = this.view;

    var left = $("#back-btn");
    left.bind("click", {that: view}, view.doBack);

    var right = $("#forward-btn");
    right.bind("click", {that: view}, view.doNext);

    var highlighter = function (e) {
        var model = view.controller.getActiveComponent();
        var cmpName = model.el.substring(model.el.lastIndexOf(".") + 1, model.el.indexOf("Elm"));

        var pic = $("#active-component-picture");
        var img = `<img src="imgs/components/${cmpName.toLowerCase()}.png"/>`;
        pic.html(img).fadeToggle(this.anim);
    };

    $("#active-component").bind("click", {that: view}, highlighter);
    $("#active-component-picture").bind("click", {that: view}, highlighter);
};

Controller.prototype.getActiveComponent = function () {

    console.log("getActiveComponent", "activeStep:", this.view.activeStep, "totalSteps:", this.view.totalSteps);
    var step = this.view.steps[this.view.activeStep];

    return {
        stepIdx: this.view.activeStep,
        el: step.component.name,
        cmp: step.component,
        bounds: this.view.model.bounds[step.component.name]
    };
};

Controller.prototype.hideComponent = function (visiblePin) {
    var view = this.view;

    if (view.modelCmp && visiblePin >= 0)
        view.modelCmp.pinOuts[visiblePin].circuit.attr("opacity", 0);

    $("#active-component").css({display: "none"}).fadeOut(this.anim);
};

Controller.prototype.resize = function (bounds, padding) {
    padding = padding === undefined ? 0 : padding;

    return {
        top: (bounds.y) + this.offsetY - (padding / 2),
        left: (bounds.x) + this.offsetX - (padding / 2),
        width: bounds.width + (padding * 2),
        height: bounds.height + (padding * 2),
        "border-radius": padding / 2
    }
};

function getSimpleName(component) {
    model = {el: component};
    return model.el.substring(model.el.lastIndexOf(".") + 1, model.el.indexOf("Elm"));
}

function getPinLabels(bbpin) {
    var pinidx = bbpin.index;
    var activePinLabel = pinidx + 1;
    var label = bbpin.text ? `pin "${bbpin.text}"` : `pin # ${activePinLabel}`;
    if (bbpin.text) {
        activePinLabel = bbpin.text;
    }
    if (bbpin.description) {
        label += " (" + bbpin.description + ")";
    }
    return {
        activePinLabel: activePinLabel,
        label: label
    }
}

Controller.prototype.showComponent = function (model) {

    var view = this.view;
    view.modelCmp = model.cmp;

    var pstep = view.steps.previous;
    var step = view.steps[this.view.activeStep];
    var cmpBounds = this.resize(model.bounds, 10);

    var cmpName = getSimpleName(model.el);
    var snbr = view.activeStep + 1;
    var steps = `<div id="pagination">${snbr}/${view.totalSteps}</div>`;
    cmpName = cmpName == "Voltage" ? "Voltage Source" : cmpName;
    var links = "";

    var pinInfo = Controller.PIN_INFO[cmpName] || {};


    var railTxt = step.bbpin.isRail ? (step.bbpin.isPowerRail ? "power" : "ground") + " rail on the " : "";
    var gpioTxt = step.bbpin.isGPIO ? " GPIO #" + (step.bbpin.row + 1) + " terminal on the " : "";

    if (pstep) {
        pstep.bbpin.circuit.attr("opacity", 0);
    }

    if (snbr == 1) {
        this.rendered = {};
    }

    for (var q in step.bbpin.components) {
        if (q == model.el || snbr == 1)
            continue;

        if (this.rendered[q] && !step.bbpin.isRail) {
            links = ` where the ${getSimpleName(q)} is plugged in`;
            if (pinInfo.hints) {
                links += "<br/><br/><b><i>Remember " + pinInfo.hints.join(" and ") + "</i></b>";
            }
        }
    }

    var pinLabels = getPinLabels(step.bbpin);
    var commentary = `Connect ${pinLabels.label} wire of the ${cmpName} to the ${gpioTxt}${railTxt}breadboard${links}`;
    var x = step.bbpin.position.x - 10;
    var y = step.bbpin.position.y - 10;
    $("#active-component").css(cmpBounds).show();
    $("#active-pin").css({top: y, left: x}).html(pinLabels.activePinLabel).show();
    $(".burner-command-desc").html(commentary + steps);

    step.bbpin.circuit.attr("opacity", 1);
    step.bbpin.circuit.node.setAttribute("fill", step.bbpin.isPowerRail ? "red" : step.bbpin.isGPIO ? "yellow" : "white");

    this.view.steps.previous = step;

    this.rendered[model.el] = true;
};

/***********************************************************************************************************/
//                                                     View                                                //
/***********************************************************************************************************/
BreadBoard.CircuitStates = {
    SYSTEM_LOADED: "system.loaded",
    CIRCUIT_WORKING: "circuit.working",
    CIRCUIT_BROKEN: "circuit.broken",
    CIRCUIT_BROKEN_NAN: "circuit.broken.nan",
    CIRCUIT_BROKEN_SINGULAR_MATRIX: "circuit.broken.singular-matrix",
    CIRCUIT_BROKEN_NO_PATH_FOR_CURRENT_SOURCE: "circuit.broken.no-path-for-current-source",
    CIRCUIT_BROKEN_VOLTAGE_SOURCE_LOOP: "circuit.broken.voltage-source-loop",
    CIRCUIT_BROKEN_CAPACITOR_LOOP: "circuit.broken.capacitor-loop",
    CIRCUIT_BROKEN_MATRIX_ERROR: "circuit.broken.matrix-error",
    CIRCUIT_BROKEN_ANY: "circuit.broken.*"
};

function BreadBoard(defaultConfig) {
    this.defaultConfig = defaultConfig;
    this.config = copy(defaultConfig);
};

function sanity(self) {

    $("#active-component-picture").fadeOut();

    with (self) {
        if (activeStep > totalSteps - 1)
            activeStep = 0;

        if (activeStep < 0)
            activeStep = totalSteps - 1;
    }
}

function copy(o) {
    var output, v, key;
    output = Array.isArray(o) ? [] : {};
    for (key in o) {
        v = o[key];
        output[key] = (typeof v === "object") ? copy(v) : v;
    }
    return output;
}

/**
    Sort model components to have power, ground and GPIO components in the
     head of the list to start circuit building from them
 */
function sortModelComponents(components) {
    var sortOrder = ["VoltageElm", "RailElm", "Ground", "Gpio"];
    return components.sort(function (a, b) {
        if (a.footprint != null && b.footprint == null) {
            return -1;
        }
        if (a.footprint == null && b.footprint != null) {
            return 1;
        }
        var first = a.name || a;
        var second = b.name || b;
        return Math.sign(sortOrder.findIndex((item) => second.indexOf(item) > -1) - sortOrder.findIndex((item) => first.indexOf(item) > -1));
    });
}

function parsePinouts(circuitModel) {
    var pinOuts = {};

    // for( var name in circuitModel.components ){
    var comps = sortModelComponents(circuitModel.components);

    for (var i in comps) {
        var comp = comps[i];
        var pins = comp.pins;

        for (var j = 0; j < pins.length; j++) {
            var pin = pins[j];
            pin.component = comp.name;
            pin.index = j;
            var po = pin.post;
            po.toString = function () {
                return `x:${this.x},y:${this.y}`;
            };
            pin.toString = function () {
                return `component: ${this.component}, index: ${this.index}`;
            };
            var coords = `x:${po.x},y:${po.y}`;

            if (!pinOuts[coords]) {
                pinOuts[coords] = {};
                pinOuts[coords].components = [];
            }
            pinOuts[coords].components.push({name: comp.name, model: comp});
            pinOuts[coords].position = po;
            pinOuts[coords].text = pin.text;
            pinOuts[coords].description = pin.description;
        }
    }

    return pinOuts;
}

function mapPinsToBreadboard(circuitModel, done, componentFilter, activeTerminal) {
    var terminalBankIndices = this.banks.reduce(function (result, element, index) {
        if (!element.type || element.type === "TERMINAL") {
            result.push(index);
        }
        return result;
    }, []);
    var powerBankIndex = this.banks.findIndex(function (element) {
        return element.type === "BUS";
    });
    var gpioBankIndex = this.banks.findIndex(function (element) {
        return element.type === "GPIO";
    });

    if (!activeTerminal) {
        activeTerminal = {
            bank: terminalBankIndices[0],
            row: 0
        }
    }

    const sensorsSet = new Set([]);
    Object.keys(circuitModel.pinOuts)
    .filter(pin => circuitModel.pinOuts[pin].components[0].model.footprint)
    .map(pin => circuitModel.pinOuts[pin])
    .forEach(pin => sensorsSet.add(pin.components[0].model.name));

    const sensors = [...sensorsSet];

    for (var xy in circuitModel.pinOuts) {

        var components = sortModelComponents(circuitModel.pinOuts[xy].components);

        // get the components at that xy
        for (var i in components) {
            var cmp = components[i];
            if (componentFilter && !componentFilter(cmp)) {
                continue;
            }
            var isPackagedElm = cmp.model.footprint != null;
            var isVoltageElm = cmp.name.indexOf("VoltageElm") != -1;
            var isPowerRail = cmp.name.indexOf("RailElm") != -1;
            var isGPIO = cmp.name.indexOf("Gpio") != -1;
            var isGround = cmp.name.indexOf("Ground") != -1;

            var originBank = activeTerminal.bank;
            var originRow = activeTerminal.row;

            // map each pinout
            for (var j = 0; j < cmp.model.pins.length; j++) {

                // order is ground, power or -,+ or 0,1 by pinout index
                if (done[cmp.name]) {
                    continue;
                }

                var pin = cmp.model.pins[j];
                var pos = pin.post;
                var mapping = {};

                if (isPackagedElm) {
                    var lead = cmp.model.footprint.leads[j];
                    activeTerminal.bank = originBank + lead.col;
                    activeTerminal.row = originRow + lead.row + sensors.indexOf(cmp.model.name);
                }
                var activeBank = activeTerminal.bank;
                var activeRow = activeTerminal.row;
                if (isVoltageElm) {
                    activeBank = powerBankIndex;
                    activeRow = parseInt(j);
                } else if (isPowerRail) {
                    activeBank = powerBankIndex;
                    // Hardcoded to the +5v rail for now
                    activeRow = 2;
                } else if (isGPIO) {
                    activeBank = gpioBankIndex;
                    activeRow = parseInt(GpioBreadBoardConfig[pin.text]) -1;
                } else if (isGround) {
                    activeBank = powerBankIndex;
                    activeRow = 0;
                }

                if (this.postsToBb[pos.toString()]) {
                    mapping = this.postsToBb[pos.toString()];
                } else {
                    mapping = {
                        position: pos,
                        bank: activeBank,
                        row: activeRow,
                        circuit: this.circuits[activeBank][activeRow],
                        components: {},
                        isRail: isVoltageElm || isPowerRail || isGround,
                        isPowerRail: (isVoltageElm && j == 0) || isPowerRail,
                        isGroundRail: (isVoltageElm && j == 1) || isGround,
                        isGPIO: isGPIO
                    };
                    this.postsToBb[pos.toString()] = mapping;
                }
                var pinMapping = {
                    position: mapping.position,
                    bank: mapping.bank,
                    row: mapping.row,
                    circuit:mapping.circuit,
                    components: mapping.components,
                    isRail: mapping.isRail,
                    isPowerRail: mapping.isPowerRail,
                    isGroundRail: mapping.isGroundRail,
                    isGPIO: mapping.isGPIO,
                    text: pin.text || null,
                    description: pin.description || null,
                    index: j
                };
                this.pinsToBb[pin.toString()] = pinMapping;

                // component to pin mapping, store the pin index
                // for each component at this connection point
                pinMapping.components[cmp.name] = j;

                if (activeBank == activeTerminal.bank && (++activeTerminal.bank) >= terminalBankIndices.length) {
                    activeTerminal.bank = terminalBankIndices[0];
                }
            }

            if (activeRow == activeTerminal.row && (++activeTerminal.row) >= this.banks[0].rows.length) {
                activeTerminal.row = 0;
            }
            done[cmp.name] = pinMapping;
        }
    }
    return activeTerminal;
}

BreadBoard.prototype.setTarget = function (target) {
    this.target = target;
    this.controller = new Controller(this);
    this.controller.bindEventHandlers(this);
    $(".burner-command-desc").html(Controller.DEFAULT_INSTRUCTIONS);
    this.loadConfig();
    this.applyConfig();
};

BreadBoard.prototype.applyConfig = function () {
    $("#" + this.target).empty();
    this.banks = copy(this.config.banks);
    this.paper = Raphael(this.target, this.config.width, this.config.height);
    this.border = this.paper.rect(0, 0, this.config.width, this.config.height);
    this.circuits = this.createBanks(this.banks);
    this.showAllBanks(this._showAllBanks);
};

BreadBoard.prototype.doNext = function (e) {

    var self = e.data.that;

    self.activeStep++;
    sanity(self)
    var model = self.controller.getActiveComponent();
    self.controller.showComponent(model);

    console.log("doNext", self.activeStep);
};

BreadBoard.prototype.doBack = function (e) {

    var self = e.data.that;

    self.activeStep--;
    sanity(self)
    var model = self.controller.getActiveComponent();
    self.controller.showComponent(model);

    console.log("doBack", self.activeStep);
};

BreadBoard.prototype.reset = function (cb) {

    if (this.clicked || !this.steps)
        return;

    this.clicked = true;
    this.rendered = {};
    console.log("reset", $("#wizard-text"), Controller.DEFAULT_INSTRUCTIONS);

    $("#active-pin").hide();

    setTimeout(function () {
        $(".burner-command-desc").html(Controller.DEFAULT_INSTRUCTIONS);
    }, 10);

    for (var i in this.steps) {
        var step = this.steps[i];
        step.bbpin.circuit.node.setAttribute("fill", "black");
    }

    this.clicked = false;
    this.activeStep = 0;

    $("#active-component-picture").hide();
};

function isExternalComponent(cmp) {
    var embeddedComponents = ["VoltageElm", "RailElm", "Ground", "Gpio"];
    return cmp.pins.length != 1
        || embeddedComponents.findIndex((item) => cmp.name.indexOf(item) > -1) == -1;
}

BreadBoard.prototype.layout = function (circuitModel, cb) {
    console.log('layout', circuitModel);
    this.reset();

    this.model = circuitModel || {bounds: {}, components: {}};

    this.totalSteps = 0;
    this.activeStep = -1;

    this.pinsToBb = [];
    this.postsToBb = [];

    $("#active-component").hide();

    var pinOuts = parsePinouts(circuitModel);
    circuitModel.pinOuts = pinOuts;

    var done = {};
    this.steps = [];

    var activeTerminal = mapPinsToBreadboard.call(this, circuitModel, done, function (cmp) {
        return cmp.name.indexOf("Wire") == -1;
    });
    mapPinsToBreadboard.call(this, circuitModel, done, function (cmp) {
        return cmp.name.indexOf("Wire") != -1;
    }, activeTerminal);

    var comps = this.model.components;
    for (var d in comps) {
        var cmp = comps[d];
        // Adding steps for external components only. Internal components like power and GPIO are excluded
        if (isExternalComponent(cmp)) {
            for (var j in cmp.pins) {
                var bbpin = this.pinsToBb[cmp.pins[j].toString()];
                console.log(`${this.totalSteps} ${cmp.name} pin ${bbpin.index} of ${cmp.pins.length} : ${cmp.pins[j]}`, bbpin);
                this.steps.push({component: cmp, bbpin: bbpin});
                this.totalSteps++;
            }
        }
    }
};

/**
 * <code>
 *  config = {
 *      &lt;dir        : 'x' or 'y'&gt;  // direction of the circuits
 *      rows        : number,
 *      cols        : number,
 *      x           : number,
 *      y           : number,
 *  }
 * </code>
 * @param config
 */
BreadBoard.prototype.createBanks = function (banks) {

    var result = [];
    if (!banks)
        var banks = this.banks;

    for (var i in banks) {
        var bank = this.createBank(banks[i]);
        result.push(bank);
    }

    return result;
};

/**
 * <code>
 *  config = {
 *      &lt;dir        : 'x' or 'y'&gt;  // direction of the circuits
 *      rows        : number,
 *      cols        : number,
 *      x           : number,
 *      y           : number,
 *  }
 * </code>
 * @param bank
 */
BreadBoard.prototype.createBank = function (bank) {

    var isVert = bank.dir == 'y';

    var thickness = this.config.thickness || 3;
    var pitch = this.config.pitch || 7;
    if (isVert) {
        bank.width = bank.width || thickness;
        bank.height = bank.height || pitch * this.config.rowCount;
    } else {
        bank.width = bank.width || pitch * 5;
        bank.height = bank.height || thickness;
    }
    bank.offsetX = bank.offsetX || 0;
    bank.offsetY = bank.offsetY || 0;
    bank.rows = bank.rows || this.config.rowCount;

    var x = this.config.leftMargin + bank.offsetX;
    var y = this.config.topMargin + bank.offsetY;

    var result = [];
    for (var i = 0; i < bank.rows; i++) {
        var x1 = isVert ? x + (i * pitch) : x;
        var y1 = isVert ? y : y + (i * pitch);
        var w = bank.width;
        var h = bank.height;

        x1 -= thickness / 2;
        y1 -= thickness / 2;

        var r = this.paper.rect(x1, y1, w, h);

        r.bounds = {
            x: x1,
            y: y1,
            width: w,
            height: h
        };

        r.attr({fill: "white", opacity: 0});
        r.bank = bank;

        result.push(r);
    }
    return result;
};

BreadBoard.prototype.showAllBanks = function (value) {
    this._showAllBanks = value;
    this.border.attr("stroke", value ? "#f00" : "#000");
    for (var i in this.circuits) {
        var group = this.circuits[i];
        for (var j in group) {
            var bank = group[j];
            bank.attr("opacity", value ? 1 : 0);
        }
    }
};

BreadBoard.prototype.saveConfig = function () {
    window.localStorage.setItem('BreadboardConfig', JSON.stringify(this.config));
};

BreadBoard.prototype.resetConfig = function () {
    window.localStorage.removeItem('BreadboardConfig');
    this.config = copy(this.defaultConfig);
};

BreadBoard.prototype.loadConfig = function () {
    var config = window.localStorage.getItem('BreadboardConfig');
    if (config !== null) {
        this.config = JSON.parse(config);
    }
};