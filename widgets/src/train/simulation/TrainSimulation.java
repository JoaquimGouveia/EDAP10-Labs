package train.simulation;

import train.view.TrainView;

public class TrainSimulation {

    public static void main(String[] args) {

        TrainView view = new TrainView();
        TrainMonitor monitor = new TrainMonitor();

        for (int i = 0; i < 20; i++) {
            TrainThread train = new TrainThread(view, monitor);
            Thread thread = new Thread(train);
            thread.start();
        }

        /*TrainThread train1 = new TrainThread(view, monitor);
        TrainThread train2 = new TrainThread(view, monitor);
        TrainThread train3 = new TrainThread(view, monitor);
        TrainThread train4 = new TrainThread(view, monitor);
        TrainThread train5 = new TrainThread(view, monitor);
        TrainThread train6 = new TrainThread(view, monitor);
        TrainThread train7 = new TrainThread(view, monitor);
        TrainThread train8 = new TrainThread(view, monitor);
        TrainThread train9 = new TrainThread(view, monitor);
        TrainThread train10 = new TrainThread(view, monitor);
        TrainThread train11 = new TrainThread(view, monitor);
        TrainThread train12 = new TrainThread(view, monitor);
        TrainThread train13 = new TrainThread(view, monitor);
        TrainThread train14 = new TrainThread(view, monitor);
        TrainThread train15 = new TrainThread(view, monitor);
        TrainThread train16 = new TrainThread(view, monitor);
        TrainThread train17 = new TrainThread(view, monitor);
        TrainThread train18 = new TrainThread(view, monitor);
        TrainThread train19 = new TrainThread(view, monitor);
        TrainThread train20 = new TrainThread(view, monitor);

        Thread thread1 = new Thread(train1);
        Thread thread2 = new Thread(train2);
        Thread thread3 = new Thread(train3);
        Thread thread4 = new Thread(train4);
        Thread thread5 = new Thread(train5);
        Thread thread6 = new Thread(train6);
        Thread thread7 = new Thread(train7);
        Thread thread8 = new Thread(train8);
        Thread thread9 = new Thread(train9);
        Thread thread10 = new Thread(train10);
        Thread thread11 = new Thread(train11);
        Thread thread12 = new Thread(train12);
        Thread thread13 = new Thread(train13);
        Thread thread14 = new Thread(train14);
        Thread thread15 = new Thread(train15);
        Thread thread16 = new Thread(train16);
        Thread thread17 = new Thread(train17);
        Thread thread18 = new Thread(train18);
        Thread thread19 = new Thread(train19);
        Thread thread20 = new Thread(train20);

        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
        thread5.start();
        thread6.start();
        thread7.start();
        thread8.start();
        thread9.start();
        thread10.start();
        thread11.start();
        thread12.start();
        thread13.start();
        thread14.start();
        thread15.start();
        thread16.start();
        thread17.start();
        thread18.start();
        thread19.start();
        thread20.start();
        */
        
    }

}
