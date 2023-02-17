package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.automation.Rule;
import de.schnippsche.solarreader.backend.automation.RuleList;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigRule;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class RulesCollector
{
  private static final String SUCCESS_CLASS = "bg-success";
  private static final String WARNING_CLASS = "bg-warning";
  private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
  private final String enabledText;
  private final String disabledText;
  private final List<StatusRow> rows;

  public RulesCollector()
  {
    rows = new ArrayList<>();
    enabledText = SolarMain.languageHelper.replacePlaceholder("{statusrow.enabled.text}");
    disabledText = SolarMain.languageHelper.replacePlaceholder("{statusrow.disabled.text}");
  }

  public String getAjax()
  {
    rows.clear();
    StatusRow row;
    // Devices

    RuleList ruleList = Config.getInstance().getRuleList();
    for (Rule rule : ruleList.getRules())
    {
      ConfigRule configRule = rule.getConfigRule();
      row = new StatusRow();
      row.setElement(configRule.getTitle());
      row.setIcon("img/inverter16.png");
      setEnabledStatus(configRule.isEnabled(), row);
      setActivity(rule.getLastActivity(), row);
      row.setInfo("");
      rows.add(row);
    }

    return Config.getInstance().getGson().toJson(rows);
  }

  private void setActivity(LocalDateTime ldt, StatusRow row)
  {
    row.setActivity(ldt == null ? "" : ldt.format(formatter));
  }

  private void setEnabledStatus(boolean status, StatusRow row)
  {
    if (status)
    {
      row.setStatusclass(SUCCESS_CLASS);
      row.setStatustext(enabledText);
    } else
    {
      row.setStatusclass(WARNING_CLASS);
      row.setStatustext(disabledText);
    }
  }

}
