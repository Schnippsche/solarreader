package de.schnippsche.solarreader.backend.pusher;

public class PushValue<T>
{
  private final T source;
  private final long startMillis;
  private boolean success;

  public PushValue(T source)
  {
    this.source = source;
    this.startMillis = System.currentTimeMillis();
  }

  public boolean isSuccess()
  {
    return success;
  }

  public void setSuccess(boolean success)
  {
    this.success = success;
  }

  public long getStartMillis()
  {
    return startMillis;
  }

  public T getSource()
  {
    return source;
  }

  @Override public String toString()
  {
    return "PushValue{" + "source=" + source + ", success=" + success + ", dateTime=" + startMillis + '}';
  }

}
