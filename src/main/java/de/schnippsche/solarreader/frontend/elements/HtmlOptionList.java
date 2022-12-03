package de.schnippsche.solarreader.frontend.elements;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlOptionList
{
  //private final List<Pair> values;
  private final Map<String, List<Pair>> entries;

  public HtmlOptionList(List<Pair> values)
  {
    this.entries = new HashMap<>();
    this.entries.put("", values);
  }

  public HtmlOptionList(Map<String, List<Pair>> values)
  {
    this.entries = values;
  }

  public String getOptions(String selected)
  {
    StringBuilder options = new StringBuilder();
    for (String key : entries.keySet())
    {
      if (!key.isEmpty())
      {
        options.append("<optgroup label=\"").append(key).append("\">");
      }
      List<Pair> values = entries.get(key);
      for (Pair pair : values)
      {
        if (options.length() > 0)
        {
          options.append(SolarMain.NEWLINE);
        }
        options.append(" <option");
        if (pair.getKey() != null && pair.getKey().equalsIgnoreCase(selected))
        {
          options.append(" selected");
        }
        options.append(" value=\"").append(pair.getKey()).append("\">");
        options.append(pair.getValue()).append("</option>");
      }
    }
    return options.toString();
  }

}
