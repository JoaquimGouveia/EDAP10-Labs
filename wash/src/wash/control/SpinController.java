package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;
import wash.io.WashingIO.Spin;
import wash.control.WashingMessage.Order;

public class SpinController extends ActorThread<WashingMessage> {

    WashingIO washingIO;

    public SpinController(WashingIO io) {
        this.washingIO = io;
    }

    @Override
    public void run() {

        // this is to demonstrate how to control the barrel spin:
        // io.setSpinMode(Spin.IDLE);

        try {

            while (true) {
                // wait for up to a (simulated) minute for a WashingMessage
                WashingMessage message = receiveWithTimeout(60000 / Settings.SPEEDUP);

                // if m is null, it means a minute passed and no message was received
                if (message != null) {
                    ActorThread<WashingMessage> sender = message.sender();
                    Order order = message.order();

                    /*When SpinController receives a SPIN_SLOW message, the barrel should alternate between 
                    slow left rotation and slow right rotation, changing direction every minute.
                    When SpinController receives a SPIN_FAST message, the barrel should rotate fast (centrifuge).
                    When SpinController receives a SPIN_OFF message, barrel rotation should stop */
                    switch (order) {
                        case SPIN_SLOW:
                            sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                            while (true) {
                                washingIO.setSpinMode(Spin.LEFT);
                                message = receiveWithTimeout(60000 / Settings.SPEEDUP);
                                if (message != null && message.order() != Order.SPIN_SLOW) {
                                    break;
                                }
                                washingIO.setSpinMode(Spin.RIGHT);
                                message = receiveWithTimeout(60000 / Settings.SPEEDUP);
                                if (message != null && message.order() != Order.SPIN_SLOW) {
                                    break;
                                }
                            }
                        case SPIN_FAST:
                            washingIO.setSpinMode(Spin.FAST);
                            sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                        case SPIN_OFF:
                            washingIO.setSpinMode(Spin.IDLE);
                            sender.send(new WashingMessage(this, Order.ACKNOWLEDGMENT));
                        default:
                            break;
                    }
                }

                // ... TODO ...
            }
        } catch (InterruptedException unexpected) {
            // we don't expect this thread to be interrupted,
            // so throw an error if it happens anyway
            throw new Error(unexpected);
        }
    }
}
