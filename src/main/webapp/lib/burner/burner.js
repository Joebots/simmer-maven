const Breadboard = require('./breadboard');
const Connections = require('./connections');
const CanvasDrawer = require('./canvas-drawer');

class Burner {
  constructor(config) {
    this.config = config;

    this.init();
  }

  init() {
    this.activeStepIndex = -1;
    this.steps = [];
    this.layers = [];
    this.connections = {};

    this.initBreadboard();
    this.initConnections();
    this.initDrawer();
  }

  initBreadboard() {
    this.breadboard = new Breadboard(this.config.breadboard);

    this.layers.push(this.breadboard);
  }

  initConnections() {
    this.connections = new Connections({
      color: '#b2a474',
      width: 10
    });

    this.layers.push(this.connections);
  }

  initDrawer() {
    this.drawer = new CanvasDrawer();
  }

  updateCanvas(target, config) {
    const canvas = window.document.querySelector(target);

    if(canvas) {
      canvas.width = config.width;
      canvas.height = config.height;
      this.load(target);
      this.drawer.restore();
    }
  }

  load(target) {
    this.drawer.load(target, this.layers);
    this.drawer.start();
  }

  loadConfig(config) {
    this.config = config;
    this.breadboard.load(this.config.breadboard);

    if(this.activeStepIndex > -1) {
      this.show(this.activeStepIndex);
    }

    this.updateCanvas('#burner', {
      width: this.config.breadboard.width,
      height: this.config.breadboard.height
    });
  }

  layout(data) {
    this.reset();
    this.steps = data.map(item => {
      const banks = item.pins.reduce((accumulator, current) => {
        accumulator[current.bank] = accumulator[current.bank] || {rows: [], connections: []};
        accumulator[current.bank].rows.push(current.row);
        accumulator[current.bank].connections.push({
          title: current.text,
          x: this.config.connections.leftMargin,
          y: this.config.connections.topMargin + current.row * this.config.connections.pitch
        });

        return accumulator;
      }, {});

      return {
        banks: Object.keys(banks)
          .map(bank => ({
            index: bank,
            rows: banks[bank].rows,
            connections: banks[bank].connections
          }))
      };
    });
  }

  reset() {
    this.activeStepIndex = -1;
    this.breadboard.highlight([]);
    this.connections.highlight([]);
  }

  next() {
    this.drawer.reset();
    const nextStepIndex = ++this.activeStepIndex >= this.steps.length ? this.activeStepIndex = 0 : this.activeStepIndex;
    this.show(nextStepIndex);
  }

  prev() {
    this.drawer.reset();
    const prevStepIndex = --this.activeStepIndex < 0 ? this.activeStepIndex = this.steps.length - 1 : this.activeStepIndex;
    this.show(prevStepIndex);
  }

  show(stepIndex) {
    this.breadboard.highlight(this.steps[stepIndex].banks);
    this.connections.load(this.steps[stepIndex].banks.reduce((accumulator, current) => {
      Array.prototype.push.apply(accumulator, current.connections);

      return accumulator;
    }, []))
      .then(() => this.connections.highlight());
  }

  parseCircuit(circuit) {

  }

  highlightAll(value) {
    this.breadboard.highlightAll(value);
  }
}

module.exports = Burner;