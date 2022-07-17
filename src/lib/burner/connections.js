const GraphicElement = require('./graphic-element');

module.exports = class Connections extends GraphicElement {
  constructor(config = {}) {
    super();
    this.config = config;
    this.connections = [];
  }

  load(connections) {
    this.reset();

    return new Promise(resolve => {
      setTimeout(() => {
        this.connections = connections
          .map(connection => ({
            background: {
              type: 'circle',
              x: connection.x,
              y: connection.y,
              width: this.config.width,
              color: this.config.color
            },
            title: connection.title
          }));
        resolve();
      });
    });
  }

  reset() {
    // Hide previously highlighted connections
    this.highlight([]);
  }

  highlight(indices) {
    if (indices) {
      this.highlighted = indices;
    }
    else {
      this.highlighted = Array.apply(null, {length: this.connections.length}).map(Number.call, Number);
    }

    this.setDirty(true);
  }

  update() {
    const connections = this.connections.sort((left, right) => right.background.y - left.background.y);

    for (let i = 1; i < connections.length; i++) {
      const previousConnection = connections[i - 1];
      const connection = connections[i];

      const distance = connection.background.y + (connection.background.width * 2) - previousConnection.background.y;

      if(distance >= 0) {
        if(connection.background.y === previousConnection.background.y) {
            connection.background.x += connection.background.width * 2;
        }
        else {
            connection.background.y -= distance + 1;
        }
      }
    }

    this.connections.forEach((connection, i) => connection.active = this.highlighted.indexOf(i) !== -1);
  }

  draw(context) {
    this.connections
      .forEach(connection => {
        switch (connection.background.type) {
          case 'circle':
            if (connection.active) {
              context
                .arc(connection.background.x, connection.background.y, connection.background.width, {
                  background: connection.background.color
                })
                .text(connection.title, connection.background.x, connection.background.y, {
                  color: 'black',
                  font: '12px Arial',
                  align: 'center'
                });
            }
            else {
              context
                .arc(connection.background.x, connection.background.y, connection.background.width, {
                  background: 'black'
                })
            }
            break;
        }
      });
  }
};