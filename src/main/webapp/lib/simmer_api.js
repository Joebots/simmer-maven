// Singleton
class SimmerAPI{
    static instance
    board

    constructor() {
        if(!SimmerAPI.instance){
            this.board = new BlocklyGPIO()
            SimmerAPI.instance = this
        }

        return SimmerAPI.instance
    }

    connect(){}

    disconnect(){}

    gpioWrite(pinNumber, value){
        console.log(`SimmerAPI.gpioWrite(${pinNumber}, ${value})`);
        if (this.board.eventBus) {
            this.board.eventBus.fireEvent(new com.joebotics.simmer.client.event.GpioEvent(pinNumber, value))
        }
    }

    servoWrite(pinNumber, angle){
        console.log(`SimmerAPI.servoWrite(${pinNumber}, ${angle})`);
        if (this.board.eventBus) {
            this.board.eventBus.fireEvent(new com.joebotics.simmer.client.event.ServoEvent(pinNumber, angle))
        }
    }

    gpioRead(pinNumber, callback){
        console.log(`SimmerAPI.gpioRead(${pinNumber}, ${callback})`);
        if (this.board.eventBus) {
            this.board.eventBus.fireEvent(new com.joebotics.simmer.client.event.GpioEvent(pinNumber, value))
        }
    }

    gpioOn(pinNumber, callback){
        console.log(`SimmerAPI.onGpioChanged(${pinNumber}, ${callback})`);
        if (this.board.eventBus) {
            this.board.eventBus.addHandler(com.joebotics.simmer.client.event.GpioEvent.TYPE, function(event) {
                callback(event.getPinNumber(), event.getValue());
            });
        }
    }

    onI2CEvent(address, register, messageLength, callback){
        console.log(`SimmerAPI.onI2CEvent(${address}, ${register}, ${messageLength}, ${callback})`);
        if (this.board.eventBus) {
            this.board.eventBus.addHandler(com.joebotics.simmer.client.event.GpioEvent.TYPE, function(event) {
                callback(event.getValue());
            });
        }
    }

    reset(){}
}