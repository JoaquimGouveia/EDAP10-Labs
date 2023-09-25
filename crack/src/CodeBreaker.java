import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

    private final JPanel workList;
    private final JPanel progressList;
    
    private final JProgressBar mainProgressBar;

    // -----------------------------------------------------------------------
    
    private CodeBreaker() {
        StatusWindow w  = new StatusWindow();

        workList        = w.getWorkList();
        progressList    = w.getProgressList();
        mainProgressBar = w.getProgressBar();
    }
    
    // -----------------------------------------------------------------------
    
    public static void main(String[] args) {

        /*
         * Most Swing operations (such as creating view elements) must be performed in
         * the Swing EDT (Event Dispatch Thread).
         * 
         * That's what SwingUtilities.invokeLater is for.
         */

        SwingUtilities.invokeLater(() -> {
            CodeBreaker codeBreaker = new CodeBreaker();
            new Sniffer(codeBreaker).start();
        });
    }

    // -----------------------------------------------------------------------

    /** Called by a Sniffer thread when an encrypted message is obtained. */
    @Override
    public void onMessageIntercepted(String message, BigInteger n) {
        ExecutorService pool = Executors.newFixedThreadPool(2);
        SwingUtilities.invokeLater(() -> {
            WorklistItem workItem = new WorklistItem(n, message);
            JButton breakButton = new JButton("Break");
            breakButton.addActionListener(e -> {
                ProgressItem progressItem = new ProgressItem(n, message);
                progressList.add(progressItem);
                workList.remove(workItem);
                ProgressTracker tracker = new Tracker();
                Runnable task = () -> {
                    try {
                        Factorizer.crack(message,n,tracker);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                };
                pool.submit(task);
            });
            workItem.add(breakButton);
            workList.add(workItem);
        });
    }

    /** ProgressTracker: reports how far factorization has progressed */ 
    private static class Tracker implements ProgressTracker {
        private int totalProgress = 0;

        /**
         * Called by Factorizer to indicate progress. The total sum of
         * ppmDelta from all calls will add upp to 1000000 (one million).
         * 
         * @param  ppmDelta   portion of work done since last call,
         *                    measured in ppm (parts per million)
         */
        @Override
        public void onProgress(int ppmDelta) {
            totalProgress += ppmDelta;
            System.out.println("progress = " + totalProgress + "/1000000");
        }
    }
}
