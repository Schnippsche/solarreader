package de.schnippsche.solarreader.backend.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.connections.NetworkConnection;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.tinylog.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class JsonTools
{

  private static final String DOT_JSON = ".json";
  private static final String SPECIFICATION_FOLDER = "specifications/";

  /**
   * read some json from an url and convert it to the mapper class
   *
   * @param url   the url which returns json
   * @param clazz the class which is generated from the json data
   * @param <V>   clazz
   * @return instance of the clazz
   */
  public <V> V getObjectFromUrl(String url, final Class<V> clazz)
  {
    JsonElement element = getJsonFromUrl(url);
    if (element == null)
    {
      return null;
    }
    return Config.getInstance().getGson().fromJson(element, clazz);
  }

  /**
   * read some json from a file and convert it to the mapper class
   *
   * @param path  the path to the file
   * @param clazz the class which is generated from the json data
   * @param <V>   clazz
   * @return instance of the clazz
   */
  public <V> V getObjectFromFile(Path path, final Class<V> clazz)
  {
    if (path != null)
    {
      if (Files.exists(path))
      {
        try (Reader reader = Files.newBufferedReader(path))
        {
          return Config.getInstance().getGson().fromJson(reader, clazz);
        } catch (IOException e)
        {
          Logger.error("Can't read file {}:{}", path.getFileName(), e.getMessage());
        }
      } else
      {
        Logger.error("can't find file {}", path.getFileName());
      }
    }
    return null;
  }

  /**
   * save an object as json file
   *
   * @param path   the path to the file
   * @param object the object
   * @return true if successful or false
   */
  public boolean saveObjectToFile(Path path, final Object object)
  {
    try (Writer writer = Files.newBufferedWriter(path))
    {
      Config.getInstance().getGson().toJson(object, writer);
      return true;
    } catch (IOException e)
    {
      Logger.error("Can't write file {}:{}", path.getFileName(), e.getMessage());
    }
    return false;
  }

  public <V> List<V> buildMappings(String resource, final Class<V> clazz)
  {
    return buildFromJson(SPECIFICATION_FOLDER + resource, "mappings", clazz);
  }

  public <V> List<V> buildFromJson(String resource, String treename, final Class<V> clazz)
  {
    Logger.info("Read json {} from {}", treename, resource);
    InputStream inputStream = JsonTools.class.getClassLoader().getResourceAsStream(resource + DOT_JSON);
    if (inputStream == null)
    {
      Logger.error("resourcefile {}.json not found!", resource);
      return Collections.emptyList();
    }
    try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
    {
      JsonObject jsonObject = JsonParser.parseReader(br).getAsJsonObject();
      JsonElement relevantElement = jsonObject.get(treename);
      if (relevantElement == null)
      {
        return Collections.emptyList();
      }
      JsonArray relevant = relevantElement.getAsJsonArray();
      Type deviceListType = TypeToken.getParameterized(List.class, clazz).getType();
      return Config.getInstance().getGson().fromJson(relevant, deviceListType);
    } catch (IOException exception)
    {
      Logger.error(exception);
    }

    return Collections.emptyList();
  }

  public JsonElement getJsonFromUrl(String myurl)
  {
    Request request = new Request.Builder().url(myurl).build();
    try (Response response = NetworkConnection.HTTPCLIENT.newCall(request).execute())
    {
      ResponseBody responseBody = response.body();
      if (responseBody != null)
      {
        return JsonParser.parseString(responseBody.string());
      }
    } catch (IOException e)
    {
      Logger.error(e);
    }
    return null;
  }

  public Specification readSpecification(String resource)
  {
    // First look at the folder; if there is a user defined file then load this instead the standard
    Path userPath = Paths.get(resource + DOT_JSON);
    Specification specification = null;
    if (Files.exists(userPath))
    {
      Logger.info("use user defined specification {}", resource);
      specification = getObjectFromFile(userPath, Specification.class);
    } else
    {
      InputStream inputStream =
        JsonTools.class.getClassLoader().getResourceAsStream(SPECIFICATION_FOLDER + resource + DOT_JSON);

      if (inputStream != null)
      {
        try (Reader reader = new InputStreamReader(inputStream))
        {
          specification = Config.getInstance().getGson().fromJson(reader, Specification.class);
        } catch (IOException e)
        {
          Logger.error("can't load resource {}:{}", resource, e.getMessage());
        }
      } else
      {
        Logger.error("can't find resource {}", SPECIFICATION_FOLDER + resource + DOT_JSON);
      }
    }
    return specification == null ? new Specification() : specification;
  }

}
