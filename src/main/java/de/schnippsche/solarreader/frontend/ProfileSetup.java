package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;

import java.util.HashMap;
import java.util.Map;

public class ProfileSetup
{
  private final Map<String, String> formValues;

  private final DialogHelper dialogHelper;

  public ProfileSetup(Map<String, String> formValues)
  {
    this.formValues = formValues;
    dialogHelper = new DialogHelper();
  }

  public String getModalCode()
  {
    String step = formValues.getOrDefault("step", "");
    switch (step)
    {
      case "editprofile":
        return show();
      case "saveprofile":
        return save();
      default:
        return "";
    }
  }

  public String save()
  {
    String longitude = formValues.getOrDefault("longitude", "0");
    String latitude = formValues.getOrDefault("latitude", "0");
    Config.getInstance().getConfigGeneral().setLongitude(longitude);
    Config.getInstance().getConfigGeneral().setLatitude(latitude);
    Config.getInstance().getStandardValues().reload();
    dialogHelper.saveConfiguration();
    return "";
  }

  public String show()
  {
    Map<String, String> map = new HashMap<>();
    map.put("[longitude]", Config.getInstance().getConfigGeneral().getLongitude().toPlainString());
    map.put("[latitude]", Config.getInstance().getConfigGeneral().getLatitude().toPlainString());
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "profilemodal.tpl").getHtmlCode(map);
  }

}
