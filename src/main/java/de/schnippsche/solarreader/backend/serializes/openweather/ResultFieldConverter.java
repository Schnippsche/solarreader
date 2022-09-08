package de.schnippsche.solarreader.backend.serializes.openweather;

import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;

public interface ResultFieldConverter
{
  ResultField getResultField(DeviceField deviceField);

}
