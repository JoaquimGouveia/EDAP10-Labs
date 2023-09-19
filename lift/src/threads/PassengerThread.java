package threads;
import lift.LiftView;
import lift.Passenger;

public class PassengerThread implements Runnable {

    private LiftView liftView;
    private LiftMonitor liftMonitor;
    private Passenger passenger;
    private int fromFloor;
    private int toFloor;

    public PassengerThread(LiftView liftView, LiftMonitor liftMonitor) {
        this.liftView = liftView;
        this.liftMonitor = liftMonitor;
    }

    @Override
    public void run() {
        while (true) {
            try {
                this.passenger = liftView.createPassenger();
                this.fromFloor = passenger.getStartFloor();
                this.toFloor = passenger.getDestinationFloor();
                passenger.begin();
                liftMonitor.handleEnter(fromFloor, toFloor);
                passenger.enterLift();
                liftMonitor.decrementEnteringExitingCount();
                liftMonitor.handleExit(toFloor);
                passenger.exitLift();
                liftMonitor.decrementEnteringExitingCount();
                //liftMonitor.handlePassenger(fromFloor, toFloor, passenger);
                passenger.end();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
