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
        var img = `<img width="170" height="110" src="imgs/components/${cmpName.toLowerCase()}.png"/>`;
        pic.html(img).fadeToggle(this.anim);
    };

    $("#active-component").bind("click", {that: view}, highlighter);
    $("#active-component-picture").bind("click", {that: view}, highlighter);

    $(window).keydown(function (evt) {

        // if(ALLOW_BB_MOVE === false)
        //     return false;

        switch (evt.key) {
            case 'r':
                window.location.reload();
                break;

            case 'ArrowUp':
                $("svg").css("top", "-=.5");
                break;

            case 'ArrowDown':
                $("svg").css("top", "+=.5");
                break;

            case 'ArrowLeft':
                $("svg").css("left", "-=.5");
                break;

            case 'ArrowRight':
                $("svg").css("left", "+=.5");
                break;

            // case '=':
            //     var r1 = $("rect[width=20]");
            //     r1.attr("width",   r1.attr("width") + 1);
            //
            //     var r2 = $("rect[height=20]");
            //     r2.attr("height",   r1.attr("height") + 1);
            //     break;
            //
            // case '-':
            //     $("rect[width=20]").attr("width",   "-=1");
            //     $("rect[height=20]").attr("height", "-=1");
            //     break;
        }
    });
}

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

Controller.prototype.showComponent = function (model) {

    var view = this.view;
    view.modelCmp = model.cmp;

    var pstep = view.steps.previous;
    var step = view.steps[this.view.activeStep];
    var cmpBounds = this.resize(model.bounds, 10);

    var cmpName = getSimpleName(model.el);
    var snbr = view.activeStep + 1;
    var pinidx = step.bbpin.index;
    var steps = `<div id="pagination">${snbr}/${view.totalSteps}</div>`;
    cmpName = cmpName == "Voltage" ? "Voltage Source" : cmpName;
    var links = "";

    var pinInfo = Controller.PIN_INFO[cmpName] || {};
    var activePinLabel = pinidx + 1;
    var label = "pin # " + activePinLabel;

    if (step.bbpin.text) {
        activePinLabel = step.bbpin.text;
    }

    if (step.bbpin.description) {
        label += " (" + step.bbpin.description + ")";
    }

    var railTxt = step.bbpin.isRail ? pinidx == 1 ? "power" : "ground" + " rail on the " : "";

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
            var olabel = pinidx == 1 ? "ground" : "power";
            links = ` where the ${getSimpleName(q)} ${olabel} wire is plugged in`;

            if (pinInfo.hints) {
                links += "<br/><br/><b><i>Remember " + pinInfo.hints.join(" and ") + "</i></b>";
            }
        }
    }

    var commentary = `Connect the ${label} wire of the ${cmpName} to the ${railTxt}breadboard${links}`;
    var x = step.bbpin.position.x - 10;
    var y = step.bbpin.position.y - 10;
    $("#active-component").css(cmpBounds).show();
    $("#active-pin").css({top: y, left: x}).html(activePinLabel).show();
    $(".burner-command-desc").html(commentary + steps);

    step.bbpin.circuit.attr("opacity", 1);
    step.bbpin.circuit.node.setAttribute("fill", label == "power" ? "red" : "white");

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

function BreadBoard(config) {
    this.banks = config.banks;
    this.config = config;
    this.controller = new Controller(this);
    this.controller.bindEventHandlers(this);
    $(".burner-command-desc").html(Controller.DEFAULT_INSTRUCTIONS);
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

function parsePinouts(circuitModel) {
    var pinOuts = {};

    // for( var name in circuitModel.components ){
    var els = circuitModel.elements.sort(function (a, b) {
        return a.indexOf("VoltageElm") != -1 ? -1 :
            a.indexOf("Ground") != -1 ? -1 :
                a.indexOf("Gpio") != -1 ? -1 : 0;
    });

    for (var i in els) {
        var name = els[i];
        var pins = circuitModel.components[name].pins;

        for (var j = 0; j < pins.length; j++) {
            var pin = pins[j];
            pin.component = name;
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
            pinOuts[coords].components.push({name: name, model: circuitModel.components[name]});
            pinOuts[coords].position = po;
            pinOuts[coords].text = pin.text;
            pinOuts[coords].description = pin.description;
        }
    }

    return pinOuts;
}

function mapPinsToBreadboard(circuitModel, done) {
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

    var activeBank = 0;
    var activeRow = 0;

    for (var xy in circuitModel.pinOuts) {

        var components = circuitModel.pinOuts[xy].components;

        // get the components at that xy
        for (var i in components) {
            var cmp = components[i];
            var isRail = cmp.name.indexOf("VoltageElm") != -1;
            var isGPIO = cmp.name.indexOf("Gpio") != -1;
            var isGround = cmp.name.indexOf("Ground") != -1;

            // map each pinout
            for (var j = 0; j < cmp.model.pins.length; j++) {

                // order is ground, power or -,+ or 0,1 by pinout index
                if (done[cmp.name]) {
                    continue;
                }

                var pin = cmp.model.pins[j];
                var pos = pin.post;
                var mapping = {};

                if (isRail) {
                    activeBank = powerBankIndex;
                    activeRow = parseInt(j);
                } else if (isGPIO) {
                    activeBank = gpioBankIndex;
                    activeRow = this.banks[gpioBankIndex].rows - parseInt(GpioConfig[pin.text]);
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
                        isRail: isRail,
                        isPowerRail: isRail && j == 0,
                        isGroundRail: isRail && j == 1,
                        text: pin.text,
                        description: pin.description,
                        index: j
                    };
                    this.postsToBb[pos.toString()] = mapping;
                }
                this.pinsToBb[pin.toString()] = mapping;

                // component to pin mapping, store the pin index
                // for each component at this connection point
                mapping.components[cmp.name] = j;

                if ((++activeBank) >= terminalBankIndices.length) {
                    activeBank = 0;
                }
            }

            if ((++activeRow) >= this.banks[0].rows.length) {
                activeRow = 0;
            }
            done[cmp.name] = mapping;
        }
    }
}

BreadBoard.prototype.setTarget = function (target) {
    this.paper = Raphael(target, this.config.width, this.config.height);
    this.circuits = this.createBanks(this.banks);
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
};

BreadBoard.prototype.layout = function (circuitModel, cb) {
    console.log('layout', circuitModel);
    this.reset();

    this.model = circuitModel || {elements: [], bounds: {}, components: {}};

    this.totalSteps = 0;
    this.activeStep = -1;

    this.pinsToBb = [];
    this.postsToBb = [];

    $("#active-component").hide();

    var pinOuts = parsePinouts(circuitModel);
    circuitModel.pinOuts = pinOuts;

    var done = {};
    this.steps = [];

    mapPinsToBreadboard.call(this, circuitModel, done);

    var els = this.model.elements;
    for (var d in els) {

        var cmp = this.model.components[els[d]];
        cmp.name = els[d];

        for (var j in cmp.pins) {
            var bbpin = this.pinsToBb[cmp.pins[j].toString()];
            console.log(`${this.totalSteps} ${cmp.name} pin ${bbpin.index} of ${cmp.pins.length} : ${cmp.pins[j]}`, bbpin);
            this.steps.push({component: cmp, bbpin: bbpin});
            this.totalSteps++;
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

    var cfg = bank;
    var isVert = cfg.dir == 'y';

    var width = cfg.width || 50;
    var height = cfg.height || 3;
    var x = cfg.x || 0;
    var y = cfg.y || 0;
    var thickness = bank.thickness || this.config.thickness || 3;
    var pitch = bank.pitch || this.config.pitch || 7;

    var result = [];
    for (var i = 0; i < cfg.rows; i++) {
        var x1 = isVert ? x + (i * pitch) : x;
        var y1 = isVert ? y : y + (i * pitch);
        var w = isVert ? thickness : width;
        var h = isVert ? height : thickness;

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

BreadBoard.prototype.showAllBanks = function () {
    var r = this.paper.rect(0, 0, this.config.width, this.config.height);
    r.attr("stroke", "#f00");
    for (var i in this.circuits) {
        var group = this.circuits[i];
        for (var j in group) {
            var bank = group[j];
            bank.attr("opacity", 1);
        }
    }
};
