package org.talend.dataquality.datamasking.shuffling;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This class is a handler of {@link ShuffleColumn} who has an internal class implemented runnable to call the shuffle
 * method in the {@link ShuffledColumn} DOC qzhao class global comment.
 */
public class ShufflingHandler {

    protected ShufflingService shufflingService;

    protected AsynchronizedOutputRunnable runnable = null;

    protected Queue<List<List<Object>>> resultQueue;

    public ShufflingHandler(ShufflingService shufflingService, Queue<List<List<Object>>> resultQueue) {
        super();
        this.shufflingService = shufflingService;
        this.resultQueue = (resultQueue == null) ? new ConcurrentLinkedQueue<List<List<Object>>>() : resultQueue;
    }

    class AsynchronizedOutputRunnable implements Runnable {

        @Override
        public void run() {
            try {
                ConcurrentLinkedQueue<Future<List<List<Object>>>> queue = shufflingService.getConcurrentQueue();
                while (!shufflingService.hasFinished() || !queue.isEmpty()) {
                    // System.out.println(">>>> run in handler " + shufflingService.hasFinished() + " " + queue.size());
                    if (queue.isEmpty()) {
                        Thread.sleep(100);
                        continue;
                    }
                    Future<List<List<Object>>> future = queue.poll();
                    List<List<Object>> rows = future.get();
                    resultQueue.add(rows);

                }
            } catch (InterruptedException e) {
                shufflingService.shutDown();
            } catch (ExecutionException e) {
                shufflingService.shutDown();
            }

        }

    }

    public void start() {
        if (runnable == null) {
            runnable = new AsynchronizedOutputRunnable();
        }
        Thread t = new Thread(runnable);
        t.start();

    }

}
