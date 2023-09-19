package threads;
import lift.LiftView;

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

        //lift waits while there are people actually entering or exiting the lift
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

    /*handleEnter handles a passenger's waiting to enter the lift. 
     It waits until the lift is at the same floor as the passenger, not moving and not full.
    */
    public synchronized void handleEnter(int fromFloor, int toFloor) throws InterruptedException {
        this.toEnter[fromFloor]+=1;
        notifyAll(); //notifies the elevator that someone is waiting to enter, in this case notify() would suffice since only the lift thread is waiting
        while (this.currentFloor != fromFloor || this.isMoving || this.isFull) {
            wait();
        }
        this.enteringExitingCount++;
        incrementPassengers();
        this.toEnter[fromFloor] = this.toEnter[fromFloor] - 1;
        this.toExit[toFloor] = this.toExit[toFloor] + 1;
        notifyAll();
    }

    /*handleExit handles a passenger's waiting to exit the lift.
     It waits until the lift is at the same floor as the passenger and not moving.
     */
    public synchronized void handleExit(int toFloor) throws InterruptedException {
        while (this.currentFloor != toFloor || this.isMoving) {
            wait();
        }
        this.enteringExitingCount++;
        this.toExit[toFloor] = this.toExit[toFloor] - 1;
        decrementPassengers();
        notifyAll();
    }

    // Updates direction of the lift
    public synchronized int updateDirection() {
        if (this.currentFloor == 0 && this.currentDirection == -1) {
            this.currentDirection = 1;
        } else if (this.currentFloor == this.nbrFloors - 1 && this.currentDirection == 1) {
            this.currentDirection = -1;
        }
        return currentDirection;
    }

    // Returns true if there is anyone waiting to enter the lift
    public synchronized boolean anyWaiting() {
        for (int i = 0; i < this.toEnter.length; i++) {
            if (this.toEnter[i] != 0 || this.toExit[i] != 0) {
                return true;
            }
        }
        return false;
    }

    // increments number of passengers in lift, also checks for max capacity
    public synchronized void incrementPassengers() {
        this.nbrPassengers++;
        if (this.nbrPassengers == this.maxPassengers) {
            this.isFull = true;
        }
    }

    // decrements number of passengers in lift, thus setting isFull to false
    public synchronized void decrementPassengers() {
        this.nbrPassengers--;
        this.isFull = false;
    }

    // decrements enteringExitingCount, notifying the lift to continue if == 0
    public synchronized void decrementEnteringExitingCount() {
        this.enteringExitingCount--;
        notifyAll();
    }

}