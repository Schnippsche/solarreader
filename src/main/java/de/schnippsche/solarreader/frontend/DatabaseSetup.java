package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigDatabase;
import de.schnippsche.solarreader.backend.exporter.InfluxExporter;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import de.schnippsche.solarreader.backend.utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class DatabaseSetup
{
  private static ConfigDatabase currentDatabase;
  private final Map<String, String> formValues;
  private final DialogHelper dialogHelper;

  public DatabaseSetup(Map<String, String> formValues)
  {
    this.formValues = formValues;
    dialogHelper = new DialogHelper();
  }

  public AjaxResult getAjaxCode(String action)
  {

    if ("checkdatabase".equals(action))
    {
      return checkDatabase();
    }
    return null;
  }

  public synchronized String getModalCode()
  {
    String step = formValues.getOrDefault("step", "");
    if (step.isEmpty())
    {
      return "";
    }
    switch (step)
    {
      case "newdatabase":
        currentDatabase = new ConfigDatabase();
        return showDatabase();
      case "savedatabase":
        return saveDatabase();
      case "editdatabase":
        currentDatabase = Config.getInstance().getDatabaseFromUuid(formValues.getOrDefault("id", "0"));
        return showDatabase();
      case "confirmdeletedatabase":
        return confirmDelete();
      case "deletedatabase":
        Config.getInstance().removeDatabase(currentDatabase);
        return "";
      default:
        return "";
    }
  }

  private String confirmDelete()
  {
    Map<String, String> infomap = new HashMap<>();
    infomap.put("[description]", currentDatabase.getDescription());
    infomap.put("[dbhost]", currentDatabase.getHost());
    infomap.put("[dbport]", "" + currentDatabase.getPort());
    infomap.put("[dbname]", currentDatabase.getDbName());
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "confirmdeletedatabasemodal.tpl").getHtmlCode(infomap);
  }

  public String showDatabase()
  {
    Map<String, String> databasemaps = new HashMap<>();
    databasemaps.put("[dbtitle]", currentDatabase.getDescription());
    databasemaps.put("[dbhost]", currentDatabase.getHost());
    databasemaps.put("[dbport]", "" + currentDatabase.getPort());
    databasemaps.put("[dbname]", currentDatabase.getDbName());
    databasemaps.put("[dbuser]", currentDatabase.getUser());
    databasemaps.put("[dbpassword]", currentDatabase.getPassword());
    databasemaps.put("[dbenablechecked]", currentDatabase.isEnabled() ? "checked" : "");
    databasemaps.put("[dbusesslchecked]", currentDatabase.isUseSsl() ? "checked" : "");
    databasemaps.put("[enabledelete]", currentDatabase.getDescription().isEmpty() ? "d-none" : "");
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "databasemodal.tpl").getHtmlCode(databasemaps);
  }

  public String saveDatabase()
  {
    if (!Config.getInstance().getConfigDatabases().contains(currentDatabase))
    {
      Config.getInstance().getConfigDatabases().add(currentDatabase);
    }
    dialogHelper.saveConfiguration();
    return "";
  }

  private AjaxResult checkDatabase()
  {
    final String newName = formValues.getOrDefault("dbtitle", "");
    long present = Config.getInstance()
                         .getConfigDatabases()
                         .stream()
                         .filter(cd -> cd.getDescription().equalsIgnoreCase(newName))
                         .filter(cd -> !cd.getUuid().equals(currentDatabase.getUuid()))
                         .count();
    if (present > 0)
    {
      return new AjaxResult(false, SolarMain.languageHelper.replacePlaceholder("{databasesetup.name.exists}"));
    }
    currentDatabase.setDescription(newName);
    currentDatabase.setHost(formValues.getOrDefault("dbhost", ""));
    currentDatabase.setPort(new NumericHelper().getInteger(formValues.getOrDefault("dbport", "8086")));
    currentDatabase.setDbName(formValues.getOrDefault("dbname", ""));
    currentDatabase.setUser(formValues.getOrDefault("dbuser", ""));
    currentDatabase.setPassword(formValues.getOrDefault("dbpassword", ""));
    currentDatabase.setEnabled(formValues.getOrDefault("dbenable", "off").equals("on"));
    currentDatabase.setUseSsl(formValues.getOrDefault("dbusessl", "off").equals("on"));
    InfluxExporter exporter = new InfluxExporter(currentDatabase);
    Pair result = exporter.test();
    String code = result.getKey();
    if ("200".equals(code))
    {
      currentDatabase.setVersion(exporter.getInfluxVersion());
      return new AjaxResult(true);
    }
    String text = SolarMain.languageHelper.replacePlaceholder(result.getValue());
    return new AjaxResult(false, text);
  }

}
