package de.schnippsche.solarreader.backend.serializes.sonoff;
import com.google.gson.*;
import de.schnippsche.solarreader.backend.configuration.Config;
import org.tinylog.Logger;

import java.lang.reflect.Type;
import java.util.Map;

public class StatusSNSDeserializer implements JsonDeserializer<StatusSNS>
{
  @Override
  public StatusSNS deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
  {
    JsonObject jsonObject = jsonElement.getAsJsonObject();
    StatusSNS result = new StatusSNS();
    Map<String, JsonElement> map = jsonObject.asMap();
    for (Map.Entry<String, JsonElement> entry : map.entrySet())
    {
      JsonElement value = entry.getValue();
      if (value.isJsonPrimitive())
      {
        switch (entry.getKey())
        {
          case "Time":
            result.time = value.getAsString();
            break;
          case "Temperature":
            result.temperature = value.getAsBigDecimal();
            break;
          case "Humidity":
            result.humidity = value.getAsBigDecimal();
            break;
          case "Light":
            result.light = value.getAsBigDecimal();
            break;
          case "Noise":
            result.noise = value.getAsBigDecimal();
            break;
          case "AirQuality":
            result.airQuality = value.getAsBigDecimal();
            break;
          case "TempUnit":
            result.tempUnit = value.getAsString();
            break;
          default:
            Logger.warn("unknown element '{}' in StatusSNS", entry.getKey());
        }
      }
      if (value.isJsonObject())
      {
        SensorValue sensorValue = Config.getInstance().getGson().fromJson(value, SensorValue.class);
        sensorValue.sensorName = entry.getKey();
        result.sensors.put(entry.getKey(), sensorValue);
      }
    }
    return result;
  }

}