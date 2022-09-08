package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.fields.TableFieldType;
import de.schnippsche.solarreader.backend.tables.TableColumnType;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableFieldEdit
{
  private final Map<String, String> formValues;

  public TableFieldEdit(Map<String, String> formValues)
  {
    this.formValues = formValues;
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
      case "edittablefield":
        return show();
      case "savetablefield":
        return save();
      default:
        return "";
    }
  }

  public String save()
  {
    return "";
  }

  public AjaxResult check()
  {
    Pair pair = new Pair("200", "");
    return new AjaxResult("200".equals(pair.getKey()), pair.getValue());
  }

  public String show()
  {
    final Map<String, String> map = new HashMap<>();
    final List<Pair> sourcetypes = new ArrayList<>();
    final List<Pair> columntypes = new ArrayList<>();
    final List<Pair> resultfields = new ArrayList<>();
    final List<Pair> standardfields = new ArrayList<>();
    Config.getInstance()
          .getStandardValues()
          .getKeys()
          .stream()
          .sorted()
          .forEach(key -> standardfields.add(new Pair(key, key)));

    for (TableFieldType type : TableFieldType.values())
    {
      sourcetypes.add(new Pair(type.toString(), "{tablefield." + type.toString().toLowerCase() + "}"));
    }
    for (TableColumnType type : TableColumnType.values())
    {
      columntypes.add(new Pair(type.toString(), "{tablefield." + type.toString().toLowerCase() + "}"));
    }

    map.put("[SOURCETYPES]", new HtmlOptionList(sourcetypes).getOptions(""));
    map.put("[COLUMNTYPES]", new HtmlOptionList(columntypes).getOptions(""));
    map.put("[RESULTFIELDVALUES]", new HtmlOptionList(resultfields).getOptions(""));
    map.put("[STANDARDFIELDVALUES]", new HtmlOptionList(standardfields).getOptions(""));
    map.put("[tablename]", "");
    map.put("[columnname]", "");
    map.put("[sourcevalue]", "");
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "edittablefieldmodal.tpl").getHtmlCode(map);
  }

}
