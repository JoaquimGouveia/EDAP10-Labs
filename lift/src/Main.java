import lift.LiftView;
import threads.LiftThread;
import threads.LiftMonitor;
import threads.PassengerThread;

public class Main {

    public static void main(String[] args) {
        final int NBR_FLOORS = 7, MAX_PASSENGERS = 4;

        LiftView liftView = new LiftView(NBR_FLOORS, MAX_PASSENGERS);
        LiftMonitor liftMonitor = new LiftMonitor(NBR_FLOORS, MAX_PASSENGERS);

        LiftThread liftThread = new LiftThread(liftView, liftMonitor);
        PassengerThread passenger1 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger2 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger3 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger4 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger5 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger6 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger7 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger8 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger9 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger10 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger11 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger12 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger13 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger14 = new PassengerThread(liftView, liftMonitor);
        PassengerThread passenger15 = new PassengerThread(liftView, liftMonitor);

        Thread lift = new Thread(liftThread);
        Thread passengerThread1 = new Thread(passenger1);
        Thread passengerThread2 = new Thread(passenger2);
        Thread passengerThread3 = new Thread(passenger3);
        Thread passengerThread4 = new Thread(passenger4);
        Thread passengerThread5 = new Thread(passenger5);
        Thread passengerThread6 = new Thread(passenger6);
        Thread passengerThread7 = new Thread(passenger7);
        Thread passengerThread8 = new Thread(passenger8);
        Thread passengerThread9 = new Thread(passenger9);
        Thread passengerThread10 = new Thread(passenger10);
        Thread passengerThread11 = new Thread(passenger11);
        Thread passengerThread12 = new Thread(passenger12);
        Thread passengerThread13 = new Thread(passenger13);
        Thread passengerThread14 = new Thread(passenger14);
        Thread passengerThread15 = new Thread(passenger15);

        lift.start();
        passengerThread1.start();
        passengerThread2.start();
        passengerThread3.start();
        passengerThread4.start();
        passengerThread5.start();
        passengerThread6.start();
        passengerThread7.start();
        passengerThread8.start();
        passengerThread9.start();
        passengerThread10.start();
        passengerThread11.start();
        passengerThread12.start();
        passengerThread13.start();
        passengerThread14.start();
        passengerThread15.start();
    }
}
