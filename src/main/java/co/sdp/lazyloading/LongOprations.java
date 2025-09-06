package co.sdp.lazyloading;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventQueue;
import org.zkoss.zk.ui.event.EventQueues;

public class LongOprations {
	private static final Logger logger = LogManager.getLogger(LongOprations.class);

    private static final String GLOBAL_QUEUE_NAME = "asyncTasksQueue";
    private static boolean listenerRegistered = false;

    private LongOprations() {}

    public static void submitTask(Runnable task) {
        EventQueue<Event> queue = EventQueues.lookup(GLOBAL_QUEUE_NAME, EventQueues.APPLICATION, true);

        if (!listenerRegistered) {
            synchronized (LongOprations.class) {
                if (!listenerRegistered) {
                    queue.subscribe(event -> {
                        Runnable r = (Runnable) event.getData();
                        if (r != null) {
                            try {
                                logger.info("Executing async task: {}", r.getClass().getName());
                                r.run();
                                logger.info("Async task {} completed successfully", r.getClass().getName());
                            } catch (Exception e) {
                                logger.error("Error while executing async task {}", r.getClass().getName(), e);
                            }
                        } else {
                            logger.warn("Received null task in async queue");
                        }
                    }, true);
                    listenerRegistered = true;
                    logger.info("Async task listener registered on queue '{}'", GLOBAL_QUEUE_NAME);
                }
            }
        }

        logger.debug("Publishing async task: {}", task.getClass().getName());
        queue.publish(new Event("process", null, task));
    }
}
