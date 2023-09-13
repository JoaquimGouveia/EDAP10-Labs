package train.simulation;
import java.util.ArrayDeque;

import train.model.*;
import train.view.TrainView;

public class TrainThread implements Runnable{
    private Route route;
    private ArrayDeque<Segment> queue;
    private TrainMonitor monitor;

    public TrainThread(TrainView view, TrainMonitor monitor) {
        this.monitor = monitor;
        this.route = view.loadRoute();
        this.queue = new ArrayDeque<Segment>();
    }

    // Make sure to call enterSegment even when creating the train
    private void createTrain() throws InterruptedException {
        Segment first = route.next();
        Segment second = route.next();
        Segment third = route.next();
        monitor.enterSegment(first);
        first.enter();
        queue.add(first);
        monitor.enterSegment(second);
        second.enter();
        queue.add(second);
        monitor.enterSegment(third);
        third.enter();
        queue.add(third);
    }

    @Override
    public void run() {
        try {
            createTrain();
            while (true) {
                Segment head = route.next();
                monitor.enterSegment(head);
                head.enter();
                queue.addFirst(head);
                Segment tail = queue.removeLast();
                tail.exit();
                monitor.exitSegment(tail);
            }
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
