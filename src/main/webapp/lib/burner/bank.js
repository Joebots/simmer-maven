const GraphicElement = require('./graphic-element');

module.exports = class Bank extends GraphicElement {
  constructor(config) {
    super();
    this.config = config;
    this.activeRows = {};
    this.rows = [];
  }

  draw(context) {
    this.rows
      .forEach(row => {
        context.rect(row.x, row.y, row.width, row.height, {
          background: row.active ? 'white' : 'black'
        });
      });
  }

  update() {
    const thickness = this.config.thickness || 3;
    const pitch = this.config.pitch || 15;
    let width = 0;
    let height = 0;

    if (this.config.vertical) {
      width = this.config.width || thickness;
      height = this.config.height || pitch * this.config.rows;
    } else {
      width = this.config.width || pitch * 5;
      height = this.config.height || thickness;
    }

    const x = this.config.leftMargin + this.config.offsetX;
    const y = this.config.topMargin + this.config.offsetY;
    this.rows = [];

    for (let i = 0; i < this.config.rows; i++) {
      let x1 = this.config.vertical ? x + (i * pitch) : x;
      let y1 = this.config.vertical ? y : y + (i * pitch);
      let w = width;
      let h = height;

      x1 -= thickness / 2;
      y1 -= thickness / 2;

      this.rows.push({
        x: Math.floor(x1),
        y: Math.floor(y1),
        width: w,
        height: h,
        active: this.activeRows[i]
      });
    }
  }

  highlight(rows = []) {
    this.activeRows = rows.reduce((accumulator, current) => {
      accumulator[current] = true;
      return accumulator;
    }, {});

    this.setDirty(true);
  }

  highlightAll(value) {
    for (let i = 0; i < this.config.rows; i++) {
      this.activeRows[i] = value;
    }

    this.setDirty(true);
  }

  getRows(indexes = []) {
    return this.rows.filter((row, i) => indexes.contains(i));
  }
};