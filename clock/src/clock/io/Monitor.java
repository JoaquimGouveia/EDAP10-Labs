package clock.io;
import java.time.LocalTime;
import java.util.concurrent.Semaphore;

public class Monitor {
    ClockOutput out;
    private LocalTime clockTime;
    private LocalTime alarmTime;
    private boolean alarmOn;
    private Semaphore mutex;

    public Monitor(ClockOutput out, Semaphore mutex){
        this.out = out;
        clockTime = LocalTime.now();
        this.mutex = mutex;
    }

    public void setClockTime(LocalTime time) {
        this.clockTime = time;
    }

    public void setAlarmTime(LocalTime time) {
        this.alarmTime = time;
    }

    public void toggleAlarm() {
        this.alarmOn = !this.alarmOn;
        out.setAlarmIndicator(alarmOn);
    }

    public void checkAlarm() {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (alarmOn && clockTime.equals(alarmTime)) {
            out.alarm();
        }
        mutex.release();
    }

    public void updateTime() {
        try {
            mutex.acquire();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String timeString = clockTime.toString().replace(":", "");
        int hours = Integer.parseInt(timeString.substring(0, 2));
        int minutes = Integer.parseInt(timeString.substring(2, 4));
        int seconds = Integer.parseInt(timeString.substring(4, 6));
        out.displayTime(hours, minutes, seconds);
        clockTime = clockTime.plusSeconds(1);
        mutex.release();
    }
}
