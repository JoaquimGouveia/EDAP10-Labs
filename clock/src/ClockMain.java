import java.util.concurrent.Semaphore;
import clock.AlarmClockEmulator;
import clock.io.Choice;
import clock.io.ClockInput;
import clock.io.ClockInput.UserInput;
import clock.io.ClockOutput;
import clock.io.ClockThread;
import clock.io.Monitor;
import java.time.LocalTime;

public class ClockMain {
    public static void main(String[] args) throws InterruptedException {
        AlarmClockEmulator emulator = new AlarmClockEmulator();

        ClockInput  in  = emulator.getInput();
        ClockOutput out = emulator.getOutput();
        Semaphore semaphore = in.getSemaphore();
        Semaphore mutex = new Semaphore(1);
        Monitor monitor = new Monitor(out, mutex);
        Thread timeThread = new Thread(new ClockThread(monitor));
        timeThread.start();


        //out.displayTime(15, 2, 37);   // arbitrary time: just an example

        while (true) {
            //This semaphore is signaled by the hardware interrupt and will only be available when the user has made a choice (realeased the keyboard)
            semaphore.acquire();  
            UserInput userInput = in.getUserInput();
            Choice choice = userInput.choice();
            int h = userInput.hours();
            int m = userInput.minutes();
            int s = userInput.seconds();
            out.alarm();
            
            switch (choice) {
                case SET_TIME:
                    monitor.setClockTime(LocalTime.of(h, m, s));
                case SET_ALARM:
                    monitor.setAlarmTime(LocalTime.of(h, m, s));
                case TOGGLE_ALARM:
                    monitor.toggleAlarm();
            }
            System.out.println("choice=" + choice + " h=" + h + " m=" + m + " s=" + s);
        }
    }
}
