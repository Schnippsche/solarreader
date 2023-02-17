package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;

import java.util.HashMap;
import java.util.Map;

public class HtmlScripts extends HtmlElement
{
  public HtmlScripts()
  {
    super(SolarMain.TEMPLATES_PATH + "scripts.tpl");
  }

  @Override public String getHtmlCode()
  {
    Map<String, String> values = new HashMap<>();
    values.put("[version]", SolarMain.softwareVersion);
    return super.getHtmlCode(values);
  }

}
