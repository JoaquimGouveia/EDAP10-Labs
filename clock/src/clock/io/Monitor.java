package clock.io;
import java.time.LocalTime;
import java.util.concurrent.Semaphore;

public class Monitor {
    private ClockOutput out;
    private LocalTime time;
    private LocalTime alarmTime = LocalTime.of(0, 0, 0);
    Semaphore mutexTime = new Semaphore(1);
    Semaphore mutexAlarm = new Semaphore(1);
    Semaphore alarmSignal = new Semaphore(0);
    private boolean alarm;

    public Monitor(ClockOutput out) {
        this.out = out;
        this.time = LocalTime.now();
    }   

    public void updateTime() {
        out.displayTime(time.getHour(), time.getMinute(), time.getSecond());
    }

    public void setTime(LocalTime time) {
        try {
            mutexTime.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.time = time;
        updateTime();
        mutexTime.release();
    }

    public void tick() {
        try {
            mutexTime.acquire();
            this.time = time.plusSeconds(1);
            System.out.println(time);
            updateTime();
            mutexTime.release();

            if (isAlarm()) {
                alarmSignal.release();
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setAlarm(LocalTime alarmTime) {
        try {
            mutexAlarm.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.alarmTime = alarmTime;
        this.alarm = true;
        out.setAlarmIndicator(alarm);
        mutexAlarm.release();
    }

    public void toggleAlarm() {
        try {
            mutexAlarm.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        this.alarm = !this.alarm;
        out.setAlarmIndicator(alarm);
        mutexAlarm.release();
    }

    public boolean isAlarm() throws InterruptedException {
        mutexAlarm.acquire();
        boolean isAlarm = this.alarm && time.getHour() == alarmTime.getHour() && time.getMinute() == alarmTime.getMinute() && time.getSecond() == alarmTime.getSecond();
        mutexAlarm.release();
        return isAlarm;
    }

    public void soundAlarm() throws InterruptedException {
        alarmSignal.acquire();
        long nextIterationTime = System.currentTimeMillis();
        int ticks = 0;
        mutexAlarm.acquire();
        while (alarm && ticks < 20) {
            out.alarm();
            ticks++;
            nextIterationTime += 1000;
            long now = System.currentTimeMillis();
            long sleepTime = nextIterationTime - now;
            Thread.sleep(sleepTime);
        mutexAlarm.release();
        }
    }

}