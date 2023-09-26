import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

    private ExecutorService threadPool;

    private final JPanel workList;
    private final JPanel progressList;
    
    private final JProgressBar mainProgressBar;

    // -----------------------------------------------------------------------
    
    private CodeBreaker() {
        StatusWindow w  = new StatusWindow();
        this.threadPool = Executors.newFixedThreadPool(2);

        workList        = w.getWorkList();
        progressList    = w.getProgressList();
        mainProgressBar = w.getProgressBar();
        w.enableErrorChecks();
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
        SwingUtilities.invokeLater(() -> {
            WorklistItem workItem = new WorklistItem(n, message);
            JButton breakButton = new JButton("Break");
            JButton cancelButton = new JButton("Cancel");
            breakButton.addActionListener(e -> {
                ProgressItem progressItem = new ProgressItem(n, message);
                progressList.add(progressItem);
                workList.remove(workItem);
                ProgressTracker tracker = new Tracker(progressItem.getProgressBar());
                Runnable task = () -> {
                    try {
                        String decryptedMessage = Factorizer.crack(message,n,tracker);
                        SwingUtilities.invokeLater(() -> {
                            progressItem.getTextArea().setText(decryptedMessage);
                            JButton removeButton = new JButton("Remove");
                            removeButton.addActionListener(d -> {
                                progressList.remove(progressItem);
                                mainProgressBar.setValue(mainProgressBar.getValue() - 1_000_000);
                                this.mainProgressBar.setMaximum(this.mainProgressBar.getMaximum() - 1_000_000);
                            });
                            progressItem.add(removeButton);
                        });
                    } catch (InterruptedException e1) {
                        System.out.println("Interrupted");
                    }
                };
                Future<?> future = threadPool.submit(task);
                cancelButton.addActionListener(f -> {
                    future.cancel(true);
                    progressItem.getTextArea().setText("Cancelled");
                    int processedPPM = progressItem.getProgressBar().getValue();
                    int remainingPPM = 1_000_000 - processedPPM;
                    mainProgressBar.setValue(mainProgressBar.getValue() + remainingPPM);
                    progressItem.getProgressBar().setValue(1_000_000);
                    progressItem.remove(cancelButton);
                });
                progressItem.add(cancelButton);
                this.mainProgressBar.setMaximum(this.mainProgressBar.getMaximum() + 1_000_000);
            });
            workItem.add(breakButton);
            workList.add(workItem);
        });
    }

    /** ProgressTracker: reports how far factorization has progressed */ 
    private class Tracker implements ProgressTracker {
        private JProgressBar progressBar;
        
        public Tracker(JProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        /**
         * Called by Factorizer to indicate progress. The total sum of
         * ppmDelta from all calls will add upp to 1000000 (one million).
         * 
         * @param  ppmDelta   portion of work done since last call,
         *                    measured in ppm (parts per million)
         */
        @Override
        public void onProgress(int ppmDelta) {
            int ppmDelta1 = Math.min(ppmDelta, 1_000_000 - progressBar.getValue());
            SwingUtilities.invokeLater(() -> {
                progressBar.setValue(progressBar.getValue() + ppmDelta1);
                mainProgressBar.setValue(mainProgressBar.getValue() + ppmDelta1);
            });
        }
    }
}
