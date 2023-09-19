package threads;
import lift.LiftView;
import lift.Passenger;

public class LiftMonitor {
    private int[] toEnter; // Number of passengers waiting to enter the lift at each floor
    private int[] toExit; // Number of passengers (inlift) waiting to exit at each floor
    private int currentFloor; // Current floor of the lift
    private int nbrPassengers; // Number of passengers in the lift
    private boolean isMoving; // True if the lift is moving, false if it is standing still
    private int currentDirection; // 1 if moving up, -1 if moving down
    private boolean isFull;
    private int nbrFloors;
    private int maxPassengers;

    public LiftMonitor(int nbrFloors, int maxPassengers) {
        this.currentDirection = 1;
        this.currentFloor = 0;
        this.nbrPassengers = 0;
        this.isMoving = true;
        this.isFull = false;
        this.nbrFloors = nbrFloors;
        this.maxPassengers = maxPassengers;
        this.toEnter = new int[nbrFloors];
        this.toExit = new int[nbrFloors];
    }

    public synchronized void handleFloor(int floor, LiftView lift) throws InterruptedException {
        this.currentFloor = floor;
        while ((this.toEnter[floor] != 0 && !isFull) || this.toExit[floor] != 0) {
            if (isMoving) {
                this.isMoving = !this.isMoving;
                lift.openDoors(floor);
            }
            notifyAll();
            wait();
        }
        if (!this.isMoving) {
            this.isMoving = !this.isMoving;
            lift.closeDoors();
        }
    }

    public synchronized void handlePassenger(int fromFloor, int toFloor, Passenger passenger) throws InterruptedException {
        this.toEnter[fromFloor] = this.toEnter[fromFloor] + 1;
        while (this.currentFloor != fromFloor || this.isMoving || this.isFull) {
            wait();
        }
        incrementPassengers();
        this.toEnter[fromFloor] = this.toEnter[fromFloor] - 1;
        this.toExit[toFloor] = this.toExit[toFloor] + 1;
        passenger.enterLift();
        System.out.println("jag är inne");
        notifyAll();
        while (this.currentFloor != toFloor || this.isMoving) {
            wait();
        }
        this.toExit[toFloor] = this.toExit[toFloor] - 1;
        decrementPassengers();
        passenger.exitLift();
        notifyAll();
    }

    public synchronized int updateDirection() {
        if (this.currentFloor == 0 && this.currentDirection == -1) {
            this.currentDirection = 1;
        } else if (this.currentFloor == this.nbrFloors - 1 && this.currentDirection == 1) {
            this.currentDirection = -1;
        }
        return currentDirection;
    }

    public synchronized void incrementPassengers() {
        this.nbrPassengers++;
        if (this.nbrPassengers == this.maxPassengers) {
            this.isFull = true;
        }
    }

    public synchronized void decrementPassengers() {
        this.nbrPassengers--;
        this.isFull = false;
    }

}
