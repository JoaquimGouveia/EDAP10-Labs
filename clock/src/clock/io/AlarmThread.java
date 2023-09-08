package clock.io;

public class AlarmThread implements Runnable{
    private Monitor monitor;

    public AlarmThread(Monitor monitor){
        this.monitor = monitor;
    }

    public void run() {
        while (true) {
            try {
                monitor.soundAlarm();
            } catch (InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
}
