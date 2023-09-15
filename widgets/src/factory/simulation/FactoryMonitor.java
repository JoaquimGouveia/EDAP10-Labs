package factory.simulation;

import java.util.concurrent.Semaphore;

import factory.model.Conveyor;
import factory.model.Tool;

public class FactoryMonitor {
    private int inAction;
    private Conveyor conveyor;

    public FactoryMonitor(Conveyor conveyor) {
        this.inAction = 0;
        this.conveyor = conveyor;
    }

    public synchronized void conveyorOn() throws InterruptedException {
        if (this.inAction == 0) {
            this.conveyor.on();
        }
    }

    public synchronized void conveyorOff() {
        this.conveyor.off();
        inAction++;
    }

    public synchronized void decrementInAction() {
        inAction--;
    }
}
