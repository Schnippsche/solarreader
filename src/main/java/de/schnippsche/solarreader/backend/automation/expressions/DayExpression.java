package de.schnippsche.solarreader.backend.automation.expressions;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.automation.ConditionalOperator;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.StandardValues;
import de.schnippsche.solarreader.frontend.HtmlElement;

import java.time.ZonedDateTime;
import java.util.Map;

public class DayExpression extends Expression
{
  public DayExpression()
  {
    super(true);
  }

  @Override public boolean isTrue()
  {
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime sunrise = (ZonedDateTime) Config.getInstance().getStandardValues().getValue(StandardValues.SUNRISE);
    ZonedDateTime sunset = (ZonedDateTime) Config.getInstance().getStandardValues().getValue(StandardValues.SUNSET);
    return now.isAfter(sunrise) && now.isBefore(sunset);
  }

  @Override public String getHtml(Map<String, String> infomap)
  {
    if (template == null)
    {
      template = new HtmlElement(SolarMain.TEMPLATES_PATH + "conditionday.tpl");
    }

    return SolarMain.languageHelper.replacePlaceholder(template.getHtmlCode(infomap));
  }

  @Override public void setValuesFromMap(Map<String, String> newValues)
  {
    setConditionalOperator(ConditionalOperator.valueOf(newValues.getOrDefault(
      "compare_" + getUuid(), ConditionalOperator.AND.toString())));

  }

  @Override public String getSummary()
  {
    return SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.day.summary}");
  }

}
