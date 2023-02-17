package de.schnippsche.solarreader.backend.utils;

import java.util.Objects;

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

  @Override public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    Pair pair = (Pair) o;
    return Objects.equals(key, pair.key);
  }

  @Override public int hashCode()
  {
    return Objects.hash(key);
  }

  @Override public String toString()
  {
    return String.format("Pair{key='%s', value='%s'}", key, value);
  }

}
