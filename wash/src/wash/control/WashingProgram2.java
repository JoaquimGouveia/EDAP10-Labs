package wash.control;

import actor.ActorThread;
import wash.io.WashingIO;

import static wash.control.WashingMessage.Order.*;

// Program 1 for washing machine. 

public class WashingProgram2 extends ActorThread<WashingMessage> {

    private WashingIO io;
    private ActorThread<WashingMessage> temp;
    private ActorThread<WashingMessage> water;
    private ActorThread<WashingMessage> spin;
    
    public WashingProgram2(WashingIO io,
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
            io.lock(true);

            water.send(new WashingMessage(this, WATER_FILL));
            WashingMessage ack1 = receive();

            temp.send(new WashingMessage(this, TEMP_SET_40));
            WashingMessage ack2 = receive();

            spin.send(new WashingMessage(this, SPIN_SLOW));
            WashingMessage ack3 = receive();

            Thread.sleep(20 * 60000 / Settings.SPEEDUP);

            spin.send(new WashingMessage(this, SPIN_OFF));
            WashingMessage ack4 = receive();


            temp.send(new WashingMessage(this, TEMP_IDLE));
            WashingMessage ack5 = receive();

            water.send(new WashingMessage(this, WATER_DRAIN));
            WashingMessage ack6 = receive();

            water.send(new WashingMessage(this, WATER_FILL));
            WashingMessage ack7 = receive();

            temp.send(new WashingMessage(this, TEMP_SET_60));
            WashingMessage ack8  = receive();

            spin.send(new WashingMessage(this, SPIN_SLOW));
            WashingMessage ack9 = receive();

            Thread.sleep(30 * 60000 / Settings.SPEEDUP);

            spin.send(new WashingMessage(this, SPIN_OFF));
            WashingMessage ack10 = receive();

            temp.send(new WashingMessage(this, TEMP_IDLE));
            WashingMessage ack11 = receive();

            water.send(new WashingMessage(this, WATER_DRAIN));
            WashingMessage ack12 = receive();

            //--------------

            System.out.println("Start loop");
            for (int i = 0; i < 5; i++) {
                water.send(new WashingMessage(this, WATER_FILL));
                WashingMessage ack13 = receive();
                spin.send(new WashingMessage(this, SPIN_SLOW));
                WashingMessage ack14 = receive();
                Thread.sleep(2 * 60000 / Settings.SPEEDUP);
                spin.send(new WashingMessage(this, SPIN_OFF));
                WashingMessage ack15 = receive();
                water.send(new WashingMessage(this, WATER_DRAIN));
                WashingMessage ack16 = receive();
            }

             // start centrifuging
            spin.send(new WashingMessage(this, SPIN_FAST));
            WashingMessage ack18 = receive();
 
             // wait 5 minutes
            Thread.sleep(5 * 60000 / Settings.SPEEDUP);
 
             // stop centrifuging
            spin.send(new WashingMessage(this, SPIN_OFF));
            WashingMessage ack19 = receive();

            water.send(new WashingMessage(this, WATER_IDLE));
            WashingMessage ack20 = receive();
 
             // Now that the barrel has stopped, it is safe to open the hatch.
            io.lock(false);
            System.out.println("WashingProgram 2 done");

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
