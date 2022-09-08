package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;
import org.takes.Take;
import org.takes.rq.form.RqFormSmart;
import org.takes.rs.RsHtml;
import org.takes.rs.RsWithHeaders;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class MainTkIndex implements Take
{
  private final Map<String, String> formValues = new HashMap<>();

  @Override public org.takes.Response act(org.takes.Request req) throws IOException
  {
    // form parameter
    long ti = System.currentTimeMillis();
    RqFormSmart formSmart = new RqFormSmart(req);
    formValues.clear();
    for (final String name : formSmart.names())
    {
      formValues.put(name, formSmart.single(name));
    }
    if (formValues.containsKey("language"))
    {
      SolarMain.languageHelper.setLanguage(formValues.getOrDefault("language", "de"));
    }
    HtmlElement completeHtmlElement = new HtmlElement("html/index.html");
    Map<String, String> maps = new HashMap<>();
    String mainHtml = "";
    mainHtml += new HtmlElement(SolarMain.TEMPLATES_PATH + "cards.tpl").getHtmlCode();

    // Modal content : test if someone acts for relevant steps
    String modalContent = new DeviceSetup(formValues).getModalCode();
    if (modalContent.isEmpty())
    {
      modalContent = new DatabaseSetup(formValues).getModalCode();
    }
    if (modalContent.isEmpty())
    {
      modalContent = new MqttSetup(formValues).getModalCode();
    }
    if (modalContent.isEmpty())
    {
      modalContent = new OpenWeatherSetup(formValues).getModalCode();
    }
    if (modalContent.isEmpty())
    {
      modalContent = new SolarprognoseSetup(formValues).getModalCode();
    }
    if (modalContent.isEmpty())
    {
      modalContent = new AwattarSetup(formValues).getModalCode();
    }
    if (modalContent.isEmpty())
    {
      modalContent = new TableFieldEdit(formValues).getModalCode();
    }

    // Check for warnings
    String warnings = "";
    String warningHtml = "";
    if (Config.getInstance().getConfigDatabases().isEmpty())
    {
      // no databases, show database hint
      warnings += new HtmlElement(SolarMain.TEMPLATES_PATH + "databasewarning.tpl").getHtmlCode();
    }
    if (Config.getInstance().getConfigDevices().isEmpty())
    {
      // no devices,show device hint
      warnings += new HtmlElement(SolarMain.TEMPLATES_PATH + "devicewarning.tpl").getHtmlCode();
    }
    if (!warnings.isEmpty())
    {
      Map<String, String> warningMap = new HashMap<>();
      warningMap.put("[warnings]", warnings);
      warningHtml = new HtmlElement(SolarMain.TEMPLATES_PATH + "warnings.tpl").getHtmlCode(warningMap);
    }

    maps.put("[footer]", new HtmlFooter().getHtmlCode());
    maps.put("[head]", new HtmlHead().getHtmlCode());
    maps.put("[scripts]", new HtmlScripts().getHtmlCode());
    maps.put("[sidenav]", new HtmlSidenav().getHtmlCode());
    maps.put("[topnav]", new HtmlTopnav().getHtmlCode());
    maps.put("[version]", SolarMain.softwareVersion);
    maps.put("[lang]", SolarMain.languageHelper.getCurrentLanguage());
    maps.put("[modal]", modalContent);
    maps.put("[warnings]", warningHtml);
    maps.put("[main]", mainHtml);
    String html = completeHtmlElement.getHtmlCode(maps);
    // translate
    html = SolarMain.languageHelper.replacePlaceholder(html);
    Logger.debug("Zeit:" + (System.currentTimeMillis() - ti));
    return new RsWithHeaders(new RsHtml(html), "Cache-Control: no-store, no-cache, must-revalidate");
  }

}
