package de.schnippsche.solarreader.backend.automation.expressions;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.automation.ConditionalOperator;
import de.schnippsche.solarreader.frontend.HtmlElement;
import org.tinylog.Logger;

import java.time.LocalTime;
import java.util.Map;

public class PeriodExpression extends Expression
{
  private LocalTime from;
  private LocalTime to;

  public PeriodExpression()
  {
    super(true);
    from = null;
    to = null;
  }

  public boolean setFrom(String from)
  {
    try
    {
      this.from = LocalTime.parse(from);
      return true;
    } catch (Exception e)
    {
      this.from = null;
    }
    return false;
  }

  public boolean setTo(String to)
  {
    try
    {
      this.to = LocalTime.parse(to);
      return true;
    } catch (Exception e)
    {
      this.to = null;
    }
    return false;
  }

  public String getFrom()
  {
    return from != null ? from.toString() : "";
  }

  public String getTo()
  {
    return to != null ? to.toString() : "";
  }

  @Override public boolean isTrue()
  {
    LocalTime now = LocalTime.now();
    boolean ok = from != null && to != null && now.isAfter(from) && now.isBefore(to);
    Logger.debug("now = {}, from = {}, to = {}, isTrue = {}", now, from, to, ok);
    return ok;
  }

  @Override public String getHtml(Map<String, String> infomap)
  {
    if (template == null)
    {
      template = new HtmlElement(SolarMain.TEMPLATES_PATH + "conditionperiod.tpl");
    }
    infomap.put("[TIMEFROM]", getFrom());
    infomap.put("[TIMETO]", getTo());
    String html = template.getHtmlCode(infomap);
    return SolarMain.languageHelper.replacePlaceholder(html);
  }

  @Override public void setValuesFromMap(Map<String, String> newValues)
  {
    setFrom(newValues.getOrDefault("timefrom_" + getUuid(), ""));
    setTo(newValues.getOrDefault("timeto_" + getUuid(), ""));
    setConditionalOperator(ConditionalOperator.valueOf(newValues.getOrDefault(
      "compare_" + getUuid(), ConditionalOperator.AND.toString())));
  }

  @Override public String getSummary()
  {
    String formatter = SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.period.summary}");
    return String.format(formatter, getFrom(), getTo());
  }

}
