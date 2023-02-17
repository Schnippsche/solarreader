package de.schnippsche.solarreader.backend.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.connections.NetworkConnection;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.tinylog.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
  public <V> V getObjectFromUrl(String url, String credentials, final Class<V> clazz)
  {
    JsonElement element = getJsonFromUrl(url, credentials);
    if (element == null)
    {
      return null;
    }
    try
    {
      return Config.getInstance().getGson().fromJson(element, clazz);
    } catch (Exception e)
    {
      Logger.error("can't parse json, {}", e.getMessage());
      return null;
    }
  }

  /**
   * read some json from an url and convert it into a linked map
   *
   * @param url the url which returns json
   * @return Map with linked objects or empty Map
   */
  public Map<String, Object> getLinkedMapFromUrl(String url, String credentials)
  {
    JsonElement element = getJsonFromUrl(url, credentials);
    if (element == null)
    {
      return Collections.emptyMap();
    }
    Type mapType = new TypeToken<Map<String, Object>>()
    {
    }.getType();
    try
    {
      return Config.getInstance().getGson().fromJson(element, mapType);
    } catch (Exception e)
    {
      Logger.error("can't parse json, {}", e.getMessage());
      return Collections.emptyMap();
    }
  }

  /**
   * convert a json string to a linked map for testing purpose
   *
   * @param data the string with valid json data
   * @return Map with linked objects or empty Map
   */
  public Map<String, Object> getLinkedMapFromString(String data)
  {
    Type mapType = new TypeToken<Map<String, Object>>()
    {
    }.getType();
    try
    {
      return Config.getInstance().getGson().fromJson(data, mapType);
    } catch (Exception e)
    {
      Logger.error("can't parse json, {}", e.getMessage());
      return Collections.emptyMap();
    }

  }

  /**
   * read some json from an url and convert it into a simple map
   * <p>- boolean values are converted from true/false into 1 / 0</p>
   * <p>- double values are converted into BigDecimal</p>
   * <p>- array[0], array[1] are converted into _0_ , _1_ and so on</p>
   * json example:
   * <pre>
   * {
   * "Status": {
   * "Module": 4,
   * "DeviceName": "Tasmota",
   * "FriendlyName": [
   * "Tasmota"
   * ]
   * }
   * }</pre>
   * will be converted into map with following keys:
   * Status_Module
   * Status_DeviceName
   * Status_FriendlyName_0
   *
   * @param url the url which returns json
   * @return Map with simple objects or empty Map
   */
  public Map<String, Object> getSimpleMapFromUrl(String url, String credentials)
  {
    HashMap<String, Object> simpleMap = new HashMap<>();
    Map<String, Object> linkedMap = getLinkedMapFromUrl(url, credentials);
    convertTree(simpleMap, null, linkedMap);
    return simpleMap;
  }

  public Map<String, Object> getSimpleMapFromString(String data)
  {
    HashMap<String, Object> simpleMap = new HashMap<>();
    Map<String, Object> linkedMap = getLinkedMapFromString(data);
    convertTree(simpleMap, null, linkedMap);
    return simpleMap;
  }

  /**
   * read some json from an url and convert it into a ResultField List
   *
   * @param url the url which returns json
   * @return List with valid ResultField objects or empty List
   */
  public List<ResultField> getResultFieldsFromUrl(String url, String credentials)
  {
    List<ResultField> list = new ArrayList<>();
    for (Map.Entry<String, Object> e : getSimpleMapFromUrl(url, credentials).entrySet())
    {
      if (e.getValue() != null)
      {
        ResultField resultField = new ResultField(e.getKey(), ResultFieldStatus.VALID, e.getValue());
        list.add(resultField);
      }
    }
    return list;
  }

  public List<ResultField> getResultFieldsFromSimpleMap(Map<String, Object> map)
  {
    List<ResultField> list = new ArrayList<>();
    for (Map.Entry<String, Object> e : map.entrySet())
    {
      if (e.getValue() != null)
      {
        ResultField resultField = new ResultField(e.getKey(), ResultFieldStatus.VALID, e.getValue());
        list.add(resultField);
      }
    }
    return list;
  }

  /**
   * convert some json from a string and convert it into a ResultField List
   *
   * @param data the string with valid json
   * @return List with valid ResultField objects or empty List
   */
  public List<ResultField> getResultFieldsFromString(String data)
  {
    HashMap<String, Object> simpleMap = new HashMap<>();
    Map<String, Object> linkedMap = getLinkedMapFromString(data);
    convertTree(simpleMap, null, linkedMap);
    List<ResultField> list = new ArrayList<>();
    for (Map.Entry<String, Object> e : simpleMap.entrySet())
    {
      if (e.getValue() != null)
      {
        ResultField resultField = new ResultField(e.getKey(), ResultFieldStatus.VALID, e.getValue());
        list.add(resultField);
      }
    }
    return list;
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
        } catch (Exception e)
        {
          Logger.error("Can't read file {}:{}", path.getFileName(), e.getMessage());
        }
      } else
      {
        Logger.warn("can't find file {}", path.getFileName());
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
    } catch (Exception e)
    {
      Logger.error("couldn't write file {}:{}", path.getFileName(), e.getMessage());
    }
    return false;
  }

  /**
   * read a json string from url
   *
   * @param url the url to connect
   * @return JsonElement or null if error occur
   */
  public JsonElement getJsonFromUrl(String url, String credentials)
  {
    Request.Builder requestBuilder = new Request.Builder().url(url);
    if (credentials != null)
    {
      requestBuilder.addHeader("Authorization", credentials);
    }

    Request request = requestBuilder.build();
    try (Response response = NetworkConnection.HTTPCLIENT.newCall(request).execute())
    {
      if (response.code() == 401)
      {
        Logger.error("url {} gets unauthorized!", url);
        return null;
      }
      ResponseBody responseBody = response.body();
      if (responseBody != null)
      {
        String resp = responseBody.string();
        Logger.debug("response:'{}'", resp);
        return JsonParser.parseString(resp);
      }
    } catch (Exception e)
    {
      Logger.error("couldn't read json from url {}, reason: {}", url, e.getMessage());
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
        } catch (Exception e)
        {
          Logger.error("couldn't load resource {}:{}", resource, e.getMessage());
        }
      } else
      {
        Logger.error("couldn't find resource {}", SPECIFICATION_FOLDER + resource + DOT_JSON);
      }
    }
    return specification == null ? new Specification() : specification;
  }

  private void convertTree(Map<String, Object> simpleMap, String prefix, Map<String, Object> treeMap)
  {
    if (treeMap != null)
    {
      for (Map.Entry<String, Object> entry : treeMap.entrySet())
      {
        convertObject(simpleMap, (prefix != null) ? prefix + "_" + entry.getKey() : entry.getKey(), entry.getValue());
      }
    }
  }

  @SuppressWarnings("unchecked") private void convertObject(Map<String, Object> simpleMap, String prefix, Object obj)
  {
    prefix = convertPrefix(prefix);
    if (obj instanceof ArrayList)
    {
      ArrayList<Object> arrayList = (ArrayList<Object>) obj;
      for (int i = 0; i < arrayList.size(); i++)
      {
        convertObject(simpleMap, ((prefix != null) ? prefix + "_" + i : String.valueOf(i)), arrayList.get(i));
      }
    } else if (obj instanceof LinkedTreeMap)
    {
      convertTree(simpleMap, prefix, (LinkedTreeMap<String, Object>) obj);
    } else if (obj instanceof Double)
    {
      simpleMap.put(prefix, BigDecimal.valueOf((Double) obj));
    } else if (obj instanceof Boolean)
    {
      simpleMap.put(prefix, ((boolean) obj) ? BigDecimal.ONE : BigDecimal.ZERO);
    } else
    {
      simpleMap.put(prefix, obj);
    }
  }

  private String convertPrefix(String oldName)
  {
    String newName = oldName;
    if (oldName != null)
    {
      newName = oldName.replaceAll("[^a-zA-Z0-9]+", "_");
    }
    return newName;
  }

}
