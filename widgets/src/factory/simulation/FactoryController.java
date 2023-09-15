package factory.simulation;

import factory.model.Conveyor;
import factory.model.Tool;
import factory.simulation.thread.PaintThread;
import factory.simulation.thread.PressThread;

public class FactoryController {
    
    public static void main(String[] args) {
        Factory factory = new Factory();

        Conveyor conveyor = factory.getConveyor();
        
        Tool press = factory.getPressTool();
        Tool paint = factory.getPaintTool();

        FactoryMonitor monitor = new FactoryMonitor(conveyor);

        PaintThread paintThread = new PaintThread(monitor, paint);
        PressThread pressThread = new PressThread(monitor, press);

        Thread paintMachine = new Thread(paintThread);
        Thread pressMachine = new Thread(pressThread);

        paintMachine.start();
        pressMachine.start();

    }
}
