package actor;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class ActorThread<M> extends Thread {

    BlockingQueue<M> queue = new ArrayBlockingQueue<>(30);

    /** Called by another thread, to send a message to this thread. */
    public void send(M message) {
        if (message != null) {
            queue.add(message);
        }
    }
    
    /** Returns the first message in the queue, or blocks if none available. */
    protected M receive() throws InterruptedException {
        M message = queue.take();
        return message;
    }
    
    /** Returns the first message in the queue, or blocks up to 'timeout'
        milliseconds if none available. Returns null if no message is obtained
        within 'timeout' milliseconds. */
    protected M receiveWithTimeout(long timeout) throws InterruptedException {
        M message = queue.poll(timeout, TimeUnit.MILLISECONDS);
        return message;
    }
}