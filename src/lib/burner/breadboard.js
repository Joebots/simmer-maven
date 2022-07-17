const GraphicElement = require('./graphic-element');
const Bank = require('./bank');

module.exports = class Breadboard extends GraphicElement {
  constructor(config) {
    super();
    this.load(config);
  }

  load(config) {
    this.initBanks(config.banks);
  }

  initBanks(banks = []) {
    this.banks = banks;
    this.children = this.banks
      .map(bank => new Bank(bank));
  }

  draw(context) {
    super.draw(context);
  }

  update() {
    super.update();
  }

  highlight(banks) {
    this.hideBanks();
    banks.forEach(bank => this.children[bank.index].highlight(bank.rows));
  }

  hideBanks() {
    this.children.forEach(bank => bank.highlight());
  }

  highlightAll(value) {
    this.children.forEach(bank => bank.highlightAll(value));
  }
};