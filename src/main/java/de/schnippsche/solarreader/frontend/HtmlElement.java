package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

public class HtmlElement
{
  private final String htmlCode;

  public HtmlElement(String resource)
  {
    htmlCode = loadHtmlTemplate(resource);
  }

  private String loadHtmlTemplate(String resource)
  {
    InputStream is = HtmlElement.class.getClassLoader().getResourceAsStream(resource);
    if (is != null)
    {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(is)))
      {
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null)
        {
          builder.append(line).append(SolarMain.NEWLINE);
        }
        return builder.toString();
        // This doesnt work in jar file!
        // return reader.lines().collect(Collectors.joining(SolarMain.NEWLINE));
      } catch (Exception e)
      {
        return "<h1>Error" + e.getMessage() + "</h1>";
      }
    }
    return "<h1>Can't find resource " + resource + "</h1>";
  }

  public String getHtmlCode()
  {
    return htmlCode;
  }

  public String getHtmlCode(Map<String, String> values)
  {
    String result = htmlCode;
    for (Map.Entry<String, String> entry : values.entrySet())
    {
      String key = entry.getKey();
      String value = entry.getValue() != null ? entry.getValue() : "";
      result = result.replace(key, value);
    }

    return result;
  }

}
