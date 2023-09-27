package wash.control;

import wash.io.WashingIO;
import wash.simulation.WashingSimulator;
import wash.control.WashingProgram3;
import wash.control.WashingProgram1;

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
                
                case 1: 
                    currentThread = new WashingProgram1(io, temp, water, spin);
                    currentThread.start();

                case 3: 
                    currentThread = new WashingProgram3(io, temp, water, spin);
                    currentThread.start();
            }

            System.out.println("user selected program " + n);

            // TODO:
            // if the user presses buttons 1-3, start a washing program
            // if the user presses button 0, and a program has been started, stop it
        }
    }
};
