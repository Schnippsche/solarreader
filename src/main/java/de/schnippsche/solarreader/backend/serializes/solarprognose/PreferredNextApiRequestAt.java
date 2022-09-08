package de.schnippsche.solarreader.backend.serializes.solarprognose;

public class PreferredNextApiRequestAt
{
  private Integer secondOfHour;
  private Long epochTimeUtc;

  public Integer getSecondOfHour()
  {
    return secondOfHour;
  }

  public void setSecondOfHour(Integer secondOfHour)
  {
    this.secondOfHour = secondOfHour;
  }

  public Long getEpochTimeUtc()
  {
    return epochTimeUtc;
  }

  public void setEpochTimeUtc(Long epochTimeUtc)
  {
    this.epochTimeUtc = epochTimeUtc;
  }

  @Override public String toString()
  {
    return "PreferredNextApiRequestAt{" + "secondOfHour=" + secondOfHour + ", epochTimeUtc=" + epochTimeUtc + '}';
  }

}
