class CanvasDrawer {
  constructor() {
    this.running = false;
  }

  load(canvas, tree = []) {
    this.tree = tree;
    this.stop();
    this.context = origami(canvas);

    this.drawBackground();
  }

  start() {
    this.running = true;
    this.run();
  }

  stop() {
    this.running = false;
  }

  run() {
    if (this.running) {
      this.render(this.tree);
      window.requestAnimationFrame(() => this.run());
    }
  }

  render(tree = []) {
    tree
      .forEach(node => {
        if (node.isDirty) {
          node.update();
          node.draw(this.context);
          node.setDirty(false);
          this.context.draw();
        }

        this.render(node.children);
      });
  }

  drawBackground() {
    this.context
      .background('black')
      .draw();
  }

  drawBorder() {
    this.context
      .border({
        border: '1px solid #F00'
      })
      .draw();
  }

  reset() {
    this.context.clear();
  }

  restore() {
    this.restoreTree(this.tree);
  }

  restoreTree(tree = []) {
    tree.forEach(node => {
      node.setDirty(true);
      this.restoreTree(node.children);
    });
  }
}

module.exports = CanvasDrawer;