package clock.io;

public class ClockThread implements Runnable{
    private Monitor monitor;


    public ClockThread(Monitor monitor){
        this.monitor = monitor;
    }

    public void run() {
        long intervalTime = 1000;
        long startTime = System.currentTimeMillis();
        long sleeptime;
        while (true) {
            sleeptime = (intervalTime - (System.currentTimeMillis() - startTime) % intervalTime);
            try {
                Thread.sleep(sleeptime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            monitor.updateTime();
        }
    }
}
