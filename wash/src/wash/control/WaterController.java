package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;
import wash.control.WashingMessage.Order;

public class WaterController extends ActorThread<WashingMessage> {

    WashingIO washingIO;

    public WaterController(WashingIO io) {
        this.washingIO = io;
    }

    @Override
    public void run() {
        try {
            while (true) {
                WashingMessage message = receiveWithTimeout(60000 / Settings.SPEEDUP);
                if (message != null) {
                    ActorThread<WashingMessage> sender = message.sender();
                    Order order = message.order();

                    switch (order) {
                        case WATER_IDLE:
                            washingIO.drain(false);
                            washingIO.fill(false);
                            sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                        case WATER_FILL:
                            washingIO.fill(true);
                            sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                        case WATER_DRAIN:
                            washingIO.drain(true);
                            sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                        default:
                            break;
                    }
                }
            }
            

                // ... TODO ...
            
        } catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }
}
