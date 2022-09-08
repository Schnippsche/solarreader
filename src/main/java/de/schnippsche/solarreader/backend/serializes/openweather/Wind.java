package de.schnippsche.solarreader.backend.serializes.openweather;

import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;

public class Wind implements ResultFieldConverter
{
  private Double speed;
  private Double deg;
  private Double gust;

  // Getter Methods

  public Double getSpeed()
  {
    return speed;
  }

  public void setSpeed(Double speed)
  {
    this.speed = speed;
  }

  public Double getDeg()
  {
    return deg;
  }

  // Setter Methods
  public void setDeg(Double deg)
  {
    this.deg = deg;
  }

  public Double getGust()
  {
    return gust;
  }

  public void setGust(Double gust)
  {
    this.gust = gust;
  }

  @Override public String toString()
  {
    return "Wind{" + "speed=" + speed + ", deg=" + deg + ", gust=" + gust + '}';
  }

  @Override public ResultField getResultField(DeviceField deviceField)
  {

    switch (deviceField.getName())
    {
      case "windspeed":
        return new ResultField(deviceField, speed);
      case "winddirection":
        return new ResultField(deviceField, deg);
      case "windgust":
        return new ResultField(deviceField, gust);
      default:
        return null;
    }
  }

}
