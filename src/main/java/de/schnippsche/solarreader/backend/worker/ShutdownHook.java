package de.schnippsche.solarreader.backend.worker;

public class ShutdownHook extends Thread
{

  @Override public void run()
  {
    ThreadHelper.stopThreads();
  }

}
