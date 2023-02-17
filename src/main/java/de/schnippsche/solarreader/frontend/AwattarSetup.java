package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigAwattar;
import de.schnippsche.solarreader.backend.connections.NetworkConnection;
import de.schnippsche.solarreader.backend.utils.Pair;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AwattarSetup
{
  private final Map<String, String> formValues;
  private final ConfigAwattar configAwattar;
  private final DialogHelper dialogHelper;

  public AwattarSetup(Map<String, String> formValues)
  {
    this.formValues = formValues;
    configAwattar = Config.getInstance().getConfigAwattar();
    dialogHelper = new DialogHelper();
  }

  public AjaxResult getAjaxCode(String action)
  {
    if ("checkawattar".equals(action))
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
      case "editawattar":
        return show();
      case "saveawattar":
        return save();
      default:
        return "";
    }
  }

  public String save()
  {
    BigDecimal price = new BigDecimal(formValues.getOrDefault("price", "0"));
    configAwattar.setPriceCorrection(price);
    configAwattar.setActivity(dialogHelper.getActivityFromForm(formValues));
    configAwattar.setConfigExport(dialogHelper.getDataExporterFromForm(formValues));
    dialogHelper.saveConfiguration();
    return "";
  }

  public AjaxResult check()
  {
    BigDecimal price = new BigDecimal(formValues.getOrDefault("price", "0"));
    configAwattar.setPriceCorrection(price);
    Pair pair = new NetworkConnection().testUrl(configAwattar.getApiUrl());
    return new AjaxResult("200".equals(pair.getKey()), pair.getValue());
  }

  public String show()
  {
    Map<String, String> map = new HashMap<>();
    map.put("[price]", configAwattar.getPriceCorrection().toString());
    dialogHelper.setActivityValues(map, configAwattar.getActivity());
    dialogHelper.setDataExporter(map, configAwattar.getConfigExport());
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "awattarmodal.tpl").getHtmlCode(map);
  }

}
