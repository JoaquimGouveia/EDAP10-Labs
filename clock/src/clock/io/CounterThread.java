package clock.io;

public class CounterThread implements Runnable{
    private Monitor monitor;

    public CounterThread(Monitor monitor){
        this.monitor = monitor;
    }

    long nextIterationTime = System.currentTimeMillis();
    long sleepTime;

    public void run() {
        while (true) {
            try {
                monitor.tick();
                nextIterationTime += 1000;
                sleepTime = nextIterationTime - System.currentTimeMillis();
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
