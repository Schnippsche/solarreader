package de.schnippsche.solarreader.frontend.elements;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.utils.Pair;

import java.util.List;

public class HtmlOptionList
{
  private final List<Pair> values;

  public HtmlOptionList(List<Pair> values)
  {
    this.values = values;
  }

  public String getOptions(String selected)
  {
    StringBuilder options = new StringBuilder();
    for (Pair pair : values)
    {
      if (options.length() > 0)
      {
        options.append(SolarMain.NEWLINE);
      }
      options.append(" <option");
      if (selected.equalsIgnoreCase(pair.getKey()))
      {
        options.append(" selected");
      }
      options.append(" value=\"").append(pair.getKey()).append("\">");
      options.append(pair.getValue()).append("</option>");
    }
    return options.toString();
  }

}
