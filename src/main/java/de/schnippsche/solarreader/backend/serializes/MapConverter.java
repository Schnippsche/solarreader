package de.schnippsche.solarreader.backend.serializes;

import java.util.Map;

public interface MapConverter
{
  void convertIntoMap(Map<String, Object> map, String optionalPrefix);

}
