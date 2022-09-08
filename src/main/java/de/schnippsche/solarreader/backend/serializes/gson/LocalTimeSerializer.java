package de.schnippsche.solarreader.backend.serializes.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class LocalTimeSerializer implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime>
{
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

  @Override public JsonElement serialize(LocalTime localTime, Type srcType, JsonSerializationContext context)
  {
    return new JsonPrimitive(formatter.format(localTime));
  }

  @Override
  public LocalTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    return LocalTime.parse(json.getAsString(), formatter);
  }

}
