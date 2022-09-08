package de.schnippsche.solarreader.backend.utils;

public class Pair
{
  private final String key;
  private final String value;

  public Pair(String key, String value)
  {
    this.key = key;
    this.value = value;
  }

  public String getKey()
  {
    return key;
  }

  public String getValue()
  {
    return value;
  }

  @Override public String toString()
  {
    return String.format("Pair{key='%s', value='%s'}", key, value);
  }

}
