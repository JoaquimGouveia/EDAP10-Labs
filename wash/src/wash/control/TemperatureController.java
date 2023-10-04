package wash.control;

import actor.ActorThread;
import wash.control.WashingMessage.Order;
import wash.io.WashingIO;

public class TemperatureController extends ActorThread<WashingMessage> {

    WashingIO washingIO;
    private double targetTemp;
    private final double dt = 10;
    private boolean isTempReached = false;
    ActorThread<WashingMessage> sender;

    public TemperatureController(WashingIO io) {
        this.washingIO = io;
    }

    @Override
    public void run() {
        try {
            while (true) {
                WashingMessage message = receiveWithTimeout(10000 / Settings.SPEEDUP);

                if (message != null) {
                    this.sender = message.sender();
                    switch (message.order()) {
                        case TEMP_IDLE:
                            targetTemp = 20;
                            isTempReached = false;
                            washingIO.heat(false);
                            this.sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                            break;
                        case TEMP_SET_40:
                            targetTemp = 40;
                            isTempReached = false;
                            break;
                        case TEMP_SET_60:
                            targetTemp = 60;
                            isTempReached = false;
                            break;
                        default:
                            break;
                    }
                }

                double currentTemp = washingIO.getTemperature();
                double upperMargin = (this.dt * 0.0478) + 0.2;
                double lowerMargin = (this.dt * 9.52 * Math.pow(10, -3)) + 0.2;
                double lowerBound = targetTemp - 2;

                if (currentTemp > targetTemp - upperMargin) {
                    washingIO.heat(false);
                } else if (currentTemp < lowerBound + lowerMargin) {
                    washingIO.heat(true);
                }

                if ((currentTemp < targetTemp && currentTemp > lowerBound) && !isTempReached && targetTemp != 20) {
                    isTempReached = true;
                    System.out.println("Reached temperature");
                    this.sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                }
            }
            
        } catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }
}
