package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

import static wash.control.WashingMessage.Order.*;

// Program 1 for washing machine. 

public class WashingProgram1 extends ActorThread<WashingMessage> {

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;
    
    public WashingProgram1(WashingIO io,
                           ActorThread<WashingMessage> temp,
                           ActorThread<WashingMessage> water,
                           ActorThread<WashingMessage> spin) 
    {
        this.io = io;
        this.temp = temp;
        this.water = water;
        this.spin = spin;
    }
    
    @Override
    public void run() {
        try {
            // Lock the hatch
            io.lock(true);

            // Let water into the machine
            water.send(new WashingMessage(this, WATER_FILL));
            WashingMessage ack1 = receive();

            // Heat to 40C*
            temp.send(new WashingMessage(this, TEMP_SET_40));
            WashingMessage ack2 = receive();

            // Instruct SpinController to rotate barrel slowly, back and forth
            // Expect an acknowledgment in response.
            spin.send(new WashingMessage(this, SPIN_SLOW));
            WashingMessage ack3 = receive();


            // Spin for thirty simulated minutes (one minute == 60000 milliseconds)
            Thread.sleep(30 * 60000 / Settings.SPEEDUP);

            // Spin OFF
            spin.send(new WashingMessage(this, SPIN_OFF));
            WashingMessage ack4 = receive();

            // Lower temperature
            temp.send(new WashingMessage(this, TEMP_IDLE));
            WashingMessage ack5 = receive();

            // Drain
            water.send(new WashingMessage(this, WATER_DRAIN));
            WashingMessage ack6 = receive();

            System.out.println("Start loop");
            for (int i = 0; i < 5; i++) {
                water.send(new WashingMessage(this, WATER_FILL));
                WashingMessage ack7 = receive();
                spin.send(new WashingMessage(this, SPIN_SLOW));
                WashingMessage ack8 = receive();
                Thread.sleep(2 * 60000 / Settings.SPEEDUP);
                spin.send(new WashingMessage(this, SPIN_OFF));
                WashingMessage ack9 = receive();
                water.send(new WashingMessage(this, WATER_DRAIN));
                WashingMessage ack10 = receive();
            }

            // start centrifuging
            spin.send(new WashingMessage(this, SPIN_FAST));
            WashingMessage ack11 = receive();

            // wait 5 minutes
            Thread.sleep(5 * 60000 / Settings.SPEEDUP);

            // stop centrifuging
            spin.send(new WashingMessage(this, SPIN_OFF));
            WashingMessage ack12 = receive();

            water.send(new WashingMessage(this, WATER_IDLE));
            WashingMessage ack13 = receive();


            // Now that the barrel has stopped, it is safe to open the hatch.
            io.lock(false);
            System.out.println("WashingProgram 1 done");

        } catch (InterruptedException e) {
            
            // If we end up here, it means the program was interrupt()'ed:
            // set all controllers to idle

            temp.send(new WashingMessage(this, TEMP_IDLE));
            water.send(new WashingMessage(this, WATER_IDLE));
            spin.send(new WashingMessage(this, SPIN_OFF));
            System.out.println("washing program terminated");
        }
    }
}
