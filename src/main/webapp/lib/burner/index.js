const Burner = require('./burner');
const burner = new Burner({
  breadboard: {
    banks: [
          {
            offsetX: 0,
            offsetY: 0,
            width: 4,
            height: 480,
            topMargin: 30,
            leftMargin: 30,
            rows: 3,
            vertical: true,
            type: 'BUS'
          },
          {
            offsetX: 80,
            offsetY: 0,
            width: 75,
            height: 4,
            topMargin: 30,
            leftMargin: 30,
            rows: 32,
            type: 'TERMINAL'
          },
          {
            offsetX: 170,
            offsetY: 0,
            width: 75,
            height: 4,
            topMargin: 30,
            leftMargin: 30,
            rows: 32,
            type: 'TERMINAL'
          },
          {
            offsetX: 275,
            offsetY: 0,
            width: 75,
            height: 4,
            topMargin: 30,
            leftMargin: 30,
            rows: 32,
            type: 'GPIO'
          }
        ]
  },
  connections: {
    marginLeft: 12,
    topMargin: 25,
    pitch: 15
  }
});

window.BurnerNew = burner;

