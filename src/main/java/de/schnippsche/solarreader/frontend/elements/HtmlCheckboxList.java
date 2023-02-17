package de.schnippsche.solarreader.frontend.elements;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.HtmlElement;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlCheckboxList
{
  private static HtmlElement element = null;
  private final Map<String, String> map = new HashMap<>();

  public HtmlCheckboxList()
  {
    if (element == null)
    {
      element = new HtmlElement(SolarMain.TEMPLATES_PATH + "checkbox.tpl");
    }
  }

  public String getHtml(List<Pair> values, String groupname)
  {
    return getHtml(values, groupname, Collections.emptyList());
  }

  public String getHtml(List<Pair> values, String groupname, List<String> active)
  {
    StringBuilder html = new StringBuilder();
    int i = 0;
    for (Pair pair : values)
    {
      map.put("[type]", "checkbox");
      map.put("[id]", "id_" + groupname + i);
      map.put("[groupname]", groupname + i);
      map.put("[key]", pair.getKey());
      map.put("[value]", pair.getValue());
      String checked = (active.contains(pair.getKey()) ? "checked" : "");
      map.put("[checked]", checked);
      html.append(element.getHtmlCode(map));
      i++;
    }
    return html.toString();
  }

}
