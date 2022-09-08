package de.schnippsche.solarreader.backend.serializes.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate>
{
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  @Override public JsonElement serialize(LocalDate localDate, Type srcType, JsonSerializationContext context)
  {
    return new JsonPrimitive(formatter.format(localDate));
  }

  @Override
  public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
  {
    return LocalDate.parse(json.getAsString(), formatter);
  }

}
