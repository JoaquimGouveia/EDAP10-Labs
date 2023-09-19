package threads;
import lift.LiftView;

public class LiftThread implements Runnable{
    private LiftView lift;
    private int currentFloor;
    private LiftMonitor liftMonitor;
    
    public LiftThread(LiftView lift, LiftMonitor liftMonitor) {
        this.lift = lift;
        this.liftMonitor = liftMonitor;
        this.currentFloor = 0;
    }

    @Override
    public void run() {
        while (true) {
            try {
                liftMonitor.handleFloor(currentFloor, lift);
                int direction = liftMonitor.updateDirection();
                int nextFloor = this.currentFloor + direction;
                lift.moveLift(currentFloor, nextFloor);
                this.currentFloor = nextFloor;
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
