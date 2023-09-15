package factory.simulation.thread;
import factory.model.Tool;
import factory.simulation.FactoryMonitor;
import factory.model.Widget;

public class PaintThread implements Runnable{
    private FactoryMonitor monitor;
    private Tool paintTool;

    public PaintThread(FactoryMonitor monitor, Tool paintTool) {
        this.monitor = monitor;
        this.paintTool = paintTool;
    }

    @Override
    public void run() {
        while (true) {
            try {
                paintTool.waitFor(Widget.BLUE_MARBLE);
                monitor.conveyorOff();
                paintTool.performAction();
                monitor.decrementInAction();
                monitor.conveyorOn();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
