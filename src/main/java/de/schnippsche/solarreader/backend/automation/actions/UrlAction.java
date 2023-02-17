package de.schnippsche.solarreader.backend.automation.actions;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.connections.NetworkConnection;
import de.schnippsche.solarreader.frontend.HtmlElement;

import java.util.Map;

public class UrlAction extends Action
{
  private String url;
  private transient NetworkConnection networkConnection;

  @Override public void doAction()
  {
    if (networkConnection == null)
    {
      networkConnection = new NetworkConnection();
    }
    networkConnection.testUrl(url);
  }

  @Override protected String getHtml(Map<String, String> infomap)
  {
    if (template == null)
    {
      template = new HtmlElement(SolarMain.TEMPLATES_PATH + "actionsendurl.tpl");
    }

    infomap.put("[URL]", url);
    return SolarMain.languageHelper.replacePlaceholder(template.getHtmlCode(infomap));

  }

  @Override public void setValuesFromMap(Map<String, String> newValues)
  {
    url = newValues.getOrDefault("url_" + getUuid(), "");
  }

  @Override public String getSummary()
  {
    String formatter = SolarMain.languageHelper.replacePlaceholder("{rulesetup.action.url.summary}");
    return String.format(formatter, url);
  }

}
