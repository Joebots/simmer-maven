const Breadboard = require('./breadboard');

describe('Breadboard', () => {
  let target;
  const config = {
    context: {},
    banks: [
      {
        type: 'BUS',
        rows: 2,
        vertical: true
      },
      {
        type: 'TERMINAL',
        rows: 32
      },
      {
        type: 'TERMINAL',
        rows: 32
      },
      {
        type: 'GPIO',
        rows: 32
      }
    ]
  };

  beforeEach(() => {
    target = new Breadboard(config);
  });

  it('should initialize breadboard', done => {
      done();
  });
});