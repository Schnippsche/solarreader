package de.schnippsche.solarreader.backend.connections;
public interface Connection<T, V>
{
  boolean open();

  void close();

  T send(V value);

}
