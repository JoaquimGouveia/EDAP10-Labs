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
    private int enteringExitingCount;

    public LiftMonitor(int nbrFloors, int maxPassengers) {
        this.currentDirection = 1;
        this.currentFloor = 0;
        this.nbrPassengers = 0;
        this.enteringExitingCount = 0;
        this.isMoving = true;
        this.isFull = false;
        this.nbrFloors = nbrFloors;
        this.maxPassengers = maxPassengers;
        this.toEnter = new int[nbrFloors];
        this.toExit = new int[nbrFloors];
    }

    public synchronized void handleFloor(int floor, LiftView lift) throws InterruptedException {
        this.currentFloor = floor;

        //lift waits until there is someone waiting for the lift
        while (!anyWaiting()) {
            wait();
        }

        //lift waits while there are people waiting to enter or exit the lift (in the current floor)
        while ((this.toEnter[floor] != 0 && !isFull) || this.toExit[floor] != 0) { // or anyWaiting() will make the lift wait with open doors.
            if (isMoving) {
                this.isMoving = !this.isMoving;
                lift.openDoors(floor);
            }
            notifyAll();
            wait();
        }

        //lift waits while there are people entering or exiting the lift
        while (this.enteringExitingCount != 0) {
            wait();
        }
        if (!this.isMoving) {
            this.isMoving = !this.isMoving;
            lift.closeDoors();
        }
    }

    /*public synchronized void handlePassenger(int fromFloor, int toFloor, Passenger passenger) throws InterruptedException {
        this.toEnter[fromFloor] = this.toEnter[fromFloor] + 1;
        notifyAll();
        while (this.currentFloor != fromFloor || this.isMoving || this.isFull) {
            wait();
        }
        incrementPassengers();
        this.toEnter[fromFloor] = this.toEnter[fromFloor] - 1;
        this.toExit[toFloor] = this.toExit[toFloor] + 1;
        passenger.enterLift();
        notifyAll();
        while (this.currentFloor != toFloor || this.isMoving) {
            wait();
        }
        this.toExit[toFloor] = this.toExit[toFloor] - 1;
        decrementPassengers();
        passenger.exitLift();
        notifyAll();
    }*/

    public synchronized void handleEnter(int fromFloor, int toFloor) throws InterruptedException {
        this.toEnter[fromFloor] = this.toEnter[fromFloor] + 1;
        notifyAll(); //notifies the elevator that someone is waiting to enter
        while (this.currentFloor != fromFloor || this.isMoving || this.isFull) {
            wait();
        }
        incrementPassengers();
        this.toEnter[fromFloor] = this.toEnter[fromFloor] - 1;
        this.toExit[toFloor] = this.toExit[toFloor] + 1;
        this.enteringExitingCount++;
        notifyAll();
    }

    public synchronized void handleExit(int toFloor) throws InterruptedException {
        while (this.currentFloor != toFloor || this.isMoving) {
            wait();
        }
        this.toExit[toFloor] = this.toExit[toFloor] - 1;
        decrementPassengers();
        this.enteringExitingCount++;
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

    public synchronized boolean anyWaiting() {
        for (int i = 0; i < this.toEnter.length; i++) {
            if (this.toEnter[i] != 0 || this.toExit[i] != 0) {
                return true;
            }
        }
        return false;
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

    public synchronized void decrementEnteringExitingCount() {
        this.enteringExitingCount--;
        notifyAll();
    }

}