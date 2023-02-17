package de.schnippsche.solarreader.backend.pusher;

import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public abstract class AbstractPushBuffer<T> implements Runnable
{
  private final int maxSeconds;
  private final ConcurrentLinkedQueue<PushValue<T>> pushValues;
  private boolean busy;

  protected AbstractPushBuffer(int maximumSeconds)
  {
    busy = false;
    this.pushValues = new ConcurrentLinkedQueue<>();
    this.maxSeconds = Math.max(10, maximumSeconds);
    Logger.debug("PushBuffer created with max {} seconds to hold", this.maxSeconds);
  }

  public void addPushValue(PushValue<T> pushValue)
  {
    this.pushValues.add(pushValue);
    Logger.debug("added push value, current queue size = {}", pushValues.size());
  }

  protected void open()
  {
    // can be overwritten
  }

  protected void close()
  {
    // can be overwritten
  }

  public boolean isBusy()
  {
    return busy;
  }

  @Override public void run()
  {
    if (pushValues.isEmpty())
    {
      return;
    }
    busy = true;
    // open connections if necessary, can take some time...
    open();
    // now 900 milliseconds maximum time for work
    long start = System.currentTimeMillis();
    final long end = start + 900; // 900 milliseconds maximum time for work
    final ArrayList<PushValue<T>> retryEntries = new ArrayList<>();
    int success = 0;
    int faults = 0;
    while (!pushValues.isEmpty() && start < end)
    {
      Logger.debug("start publishing {} push values...", pushValues.size());
      PushValue<T> pushValue = pushValues.poll();
      if (pushValue != null)
      {
        PushResult status = push(pushValue);
        switch (status)
        {
          case SUCCESFUL:
            Logger.debug("publishing successful");
            success++;
            break;
          case REMOVE:
            Logger.error("request is invalid and was removed from queue...");
            faults++;
            break;
          case RETRY:
            faults++;
            long seconds = (start - pushValue.getStartMillis()) / 1000;
            if (seconds < maxSeconds)
            {
              Logger.debug("fails, must retry, {} seconds gone, {} seconds before throwing...", seconds,
                maxSeconds - seconds);
              retryEntries.add(pushValue);
            } else
            {
              Logger.warn("fails, push timeout reached, throw pushValue {}", pushValue);
            }
        }
      }
      start = System.currentTimeMillis();
    }
    close();
    Logger.debug("packet finished, {} success and {} faults, retry packet size = {}", success, faults, retryEntries.size());

    // Take retry entries back to the loop
    if (!retryEntries.isEmpty())
    {
      pushValues.addAll(retryEntries);
    }
    busy = false;
  }

  /**
   * push the value and remove it from the queue; if this method returns false the push value is
   * added again at the end of the queue
   *
   * @param pushValue the value for pushing
   * @return the status code from the response;
   */
  public abstract PushResult push(PushValue<T> pushValue);

}
