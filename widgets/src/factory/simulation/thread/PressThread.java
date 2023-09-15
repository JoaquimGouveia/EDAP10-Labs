package factory.simulation.thread;
import factory.simulation.FactoryMonitor;
import factory.model.Tool;
import factory.model.Widget;

public class PressThread implements Runnable{
    private FactoryMonitor monitor;
    private Tool pressTool;

    public PressThread(FactoryMonitor monitor, Tool pressTool) {
        this.monitor = monitor;    
        this.pressTool = pressTool;
    }

    @Override
    public void run() {
        while (true) {
            try {
                pressTool.waitFor(Widget.GREEN_BLOB);
                monitor.conveyorOff();
                pressTool.performAction();
                monitor.decrementInAction();
                monitor.conveyorOn();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}
