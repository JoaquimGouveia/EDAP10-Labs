package train.simulation;

import java.util.Set;
import java.util.HashSet;
import train.model.Segment;

public class TrainMonitor {
    private Set<Segment> segments;
    
    public TrainMonitor() {
        this.segments = new HashSet<Segment>();
    }

    public synchronized Boolean isSegmentBusy(Segment segment) {
        return segments.contains(segment);
    }

    public synchronized void enterSegment(Segment segment) throws InterruptedException {
        while (isSegmentBusy(segment)) {
            wait();
        }
        segments.add(segment);
    }

    public synchronized void exitSegment(Segment segment) {
        segments.remove(segment);
        notifyAll();
    }
}
