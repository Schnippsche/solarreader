package de.schnippsche.solarreader.backend.serializes.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

public class TimeUnitSerializer implements JsonSerializer<TimeUnit>, JsonDeserializer<TimeUnit>
{

  @Override public JsonElement serialize(TimeUnit timeUnit, Type srcType, JsonSerializationContext context)
  {
    return new JsonPrimitive(timeUnit.toString());
  }

  @Override
  public TimeUnit deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    return TimeUnit.valueOf(json.getAsString());
  }

}
