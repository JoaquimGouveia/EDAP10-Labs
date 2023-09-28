package wash.control;

import wash.io.WashingIO;
import wash.simulation.WashingSimulator;

public class Wash {

    private static Thread currentThread;

    public static void main(String[] args) throws InterruptedException {
        WashingSimulator sim = new WashingSimulator(Settings.SPEEDUP);

        WashingIO io = sim.startSimulation();

        TemperatureController temp = new TemperatureController(io);
        WaterController water = new WaterController(io);
        SpinController spin = new SpinController(io);

        temp.start();
        water.start();
        spin.start();

        while (true) {
            int n = io.awaitButton();

            switch (n) {
                case 0: 
                    currentThread.interrupt();
                    break;
                case 1: 
                    currentThread = new WashingProgram1(io, temp, water, spin);
                    currentThread.start();
                    break;
                case 2: 
                    currentThread = new WashingProgram2(io, temp, water, spin);
                    currentThread.start();
                    break;
                case 3: 
                    currentThread = new WashingProgram3(io, temp, water, spin);
                    currentThread.start();
                    break;
                default:
                    break;
            }   

            System.out.println("user selected program " + n);
        }
    }
};
