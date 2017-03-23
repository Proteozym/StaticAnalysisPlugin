package utility;

/**
 * Created by prote on 05.03.2017.
 */
public class NotifyRunnable implements Runnable {
    public void run() {
        ProjectData instance = ProjectData.getInstance();
        Object lock = instance.getLock();

        synchronized (lock) {
            //listsDone = true;
            //notifyAll();
            //isFin = true;

            lock.notifyAll();
        }
    }
}
