import java.util.concurrent.Semaphore;
import clock.AlarmClockEmulator;
import clock.io.AlarmThread;
import clock.io.Choice;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;
import clock.io.CounterThread;
import clock.io.Monitor;
import java.time.LocalTime;

public class ClockMain {
    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        Semaphore semaphore = in.getSemaphore();

        Monitor monitor = new Monitor(out);

        CounterThread ct = new CounterThread(monitor);
        AlarmThread at = new AlarmThread(monitor);
    
        Thread counterThread = new Thread(ct);
        Thread alarmThread = new Thread(at);
        counterThread.start();
        alarmThread.start();

        while (true) {
            //This semaphore is signaled by the hardware interrupt and will only be available when the user has made a choice (realeased the keyboard)
            semaphore.acquire();  
            UserInput userInput = in.getUserInput();
            Choice choice = userInput.choice();
            int h = userInput.hours();
            int m = userInput.minutes();
            int s = userInput.seconds();
            
            switch (choice) {
                case SET_TIME:
                    monitor.setTime(LocalTime.of(h, m, s));
                    break;
                case SET_ALARM:
                    monitor.setAlarm(LocalTime.of(h, m, s));
                    break;
                case TOGGLE_ALARM:
                    monitor.toggleAlarm();
                    break;
            }
            System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);
        }
    }
}
