module.exports = class GraphicElement {
  constructor() {
    this.children = [];
  }

  draw() {

  }

  update() {

  }

  setDirty(state) {
    this.isDirty = state;
  }
};