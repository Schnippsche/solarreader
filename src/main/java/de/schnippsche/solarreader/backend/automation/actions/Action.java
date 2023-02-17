package de.schnippsche.solarreader.backend.automation.actions;

import de.schnippsche.solarreader.backend.utils.NumericHelper;
import de.schnippsche.solarreader.frontend.HtmlElement;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Action
{
  protected final transient NumericHelper numericHelper;
  private final String uuid;
  private final transient Map<String, String> infomap;
  protected transient HtmlElement template;

  protected Action()
  {
    this.uuid = UUID.randomUUID().toString();
    this.template = null;
    this.infomap = new HashMap<>();
    this.numericHelper = new NumericHelper();
  }

  public String getUuid()
  {
    return uuid;
  }

  public abstract void doAction();

  public String getHtmlCode()
  {
    infomap.put("[id]", this.uuid);
    return getHtml(infomap);
  }

  protected abstract String getHtml(Map<String, String> infomap);

  public abstract void setValuesFromMap(Map<String, String> newValues);

  public abstract String getSummary();

}
