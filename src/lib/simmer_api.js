var Bgpio = Bgpio || {};

var SimmerAPI = function(board) {
    this.board = board;
};

SimmerAPI.prototype.connect = function() {
};

SimmerAPI.prototype.disconnect = function() {
};

SimmerAPI.prototype.gpioWrite = function(pinNumber, value) {
    console.log(`SimmerAPI.gpioWrite(${pinNumber}, ${value})`);
    if (this.board.eventBus) {
        this.board.eventBus.fireEvent(new com.joebotics.simmer.client.event.GpioEvent(pinNumber, value))
    }
};

SimmerAPI.prototype.servoWrite = function(pinNumber, angle) {
    console.log(`SimmerAPI.servoWrite(${pinNumber}, ${angle})`);
    if (this.board.eventBus) {
        this.board.eventBus.fireEvent(new com.joebotics.simmer.client.event.ServoEvent(pinNumber, angle))
    }
};

SimmerAPI.prototype.gpioRead = function(pinNumber, callback) {
    console.log(`SimmerAPI.gpioRead(${pinNumber}, ${callback})`);
    /*if (this.board.eventBus) {
        this.board.eventBus.fireEvent(new com.joebotics.simmer.client.event.GpioEvent(pinNumber, value))
    }*/
};

SimmerAPI.prototype.gpioOn = function(pinNumber, callback) {
    console.log(`SimmerAPI.onGpioChanged(${pinNumber}, ${callback})`);
    if (this.board.eventBus) {
        this.board.eventBus.addHandler(com.joebotics.simmer.client.event.GpioEvent.TYPE, function(event) {
            callback(event.getPinNumber(), event.getValue());
        });
    }
};

SimmerAPI.prototype.onI2CEvent = function(address, register, messageLength, callback) {
    console.log(`SimmerAPI.onI2CEvent(${address}, ${register}, ${messageLength}, ${callback})`);
    if (this.board.eventBus) {
        this.board.eventBus.addHandler(com.joebotics.simmer.client.event.GpioEvent.TYPE, function(event) {
            callback(event.getValue());
        });
    }
};

SimmerAPI.prototype.reset = function() {
};

Bgpio.SimmerAPI = new SimmerAPI(Bgpio);