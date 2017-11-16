var Bgpio = Bgpio || {};

var SimmerAPI = function(board) {
    this.board = board;
};

SimmerAPI.prototype.gpioWrite = function(pinNumber, value) {
    console.log("SimmerAPI.gpioWrite(" + pinNumber + ", " + value + ")");
    if (this.board.eventBus) {
        this.board.eventBus.fireEvent(new com.joebotics.simmer.client.event.GpioEvent(pinNumber, value))
    }
};

SimmerAPI.prototype.gpioRead = function(pinNumber) {
    console.log("SimmerAPI.gpioRead(" + pinNumber + ")");
    /*if (this.board.eventBus) {
        this.board.eventBus.fireEvent(new com.joebotics.simmer.client.event.GpioEvent(pinNumber, value))
    }*/
};

SimmerAPI.prototype.gpioOn = function(callback) {
    console.log("SimmerAPI.onGpioChanged(" + callback + ")");
    if (this.board.eventBus) {
        this.board.eventBus.addHandler(com.joebotics.simmer.client.event.GpioEvent.TYPE, function(event) {
            callback(event.getPinNumber(), event.getValue());
        });
    }
};

Bgpio.SimmerAPI = new SimmerAPI(Bgpio);