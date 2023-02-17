package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigSolarprognose;
import de.schnippsche.solarreader.backend.connections.NetworkConnection;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SolarprognoseSetup
{
  private final Map<String, String> formValues;
  private final ConfigSolarprognose configSolarprognose;
  private final DialogHelper dialogHelper;

  public SolarprognoseSetup(Map<String, String> formValues)
  {
    this.formValues = formValues;
    configSolarprognose = Config.getInstance().getConfigSolarprognose();
    dialogHelper = new DialogHelper();
  }

  public AjaxResult getAjaxCode(String action)
  {
    if ("checksolarprognose".equals(action))
    {
      return check();
    }
    return null;
  }

  public String getModalCode()
  {
    String step = formValues.getOrDefault("step", "");
    switch (step)
    {
      case "editsolarprognose":
        return show();
      case "savesolarprognose":
        return save();
    }
    return "";
  }

  public String save()
  {
    configSolarprognose.setAccessToken(formValues.getOrDefault("token", ""));
    configSolarprognose.setApiId(formValues.getOrDefault("elementid", ""));
    dialogHelper.getActivityFromForm(formValues);
    configSolarprognose.setActivity(dialogHelper.getActivityFromForm(formValues));
    configSolarprognose.setApiAlgorythm(formValues.getOrDefault("algorithm", ""));
    configSolarprognose.setApiItem(formValues.getOrDefault("item", "plant"));
    configSolarprognose.setConfigExport(dialogHelper.getDataExporterFromForm(formValues));
    dialogHelper.saveConfiguration();
    return "";
  }

  public AjaxResult check()
  {
    configSolarprognose.setAccessToken(formValues.getOrDefault("token", ""));
    configSolarprognose.setApiId(formValues.getOrDefault("elementid", ""));
    configSolarprognose.setApiAlgorythm(formValues.getOrDefault("algorithm", ""));
    configSolarprognose.setApiItem(formValues.getOrDefault("item", "plant"));
    Pair pair = new NetworkConnection().testUrl(configSolarprognose.getApiUrl());
    return new AjaxResult("200".equals(pair.getKey()), pair.getValue());
  }

  public String show()
  {
    Map<String, String> map = new HashMap<>();
    map.put("[token]", configSolarprognose.getAccessToken());
    map.put("[elementid]", configSolarprognose.getApiId());
    map.put("[item]", configSolarprognose.getApiItem());
    List<Pair> algorithms = new ArrayList<>();
    algorithms.add(new Pair("", ""));
    algorithms.add(new Pair("own-v1", "own-v1"));
    algorithms.add(new Pair("mosmix", "mosmix"));
    map.put("[ALGORITHM]", new HtmlOptionList(algorithms).getOptions(configSolarprognose.getApiAlgorythm()));
    dialogHelper.setActivityValues(map, configSolarprognose.getActivity());
    dialogHelper.setDataExporter(map, configSolarprognose.getConfigExport());
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "solarprognosemodal.tpl").getHtmlCode(map);
  }

}
