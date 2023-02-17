package de.schnippsche.solarreader.backend.configuration;

import de.schnippsche.solarreader.backend.automation.ActionList;
import de.schnippsche.solarreader.backend.automation.ExpressionList;

import java.util.Objects;
import java.util.UUID;

public class ConfigRule
{
  private final String uuid;
  private final ExpressionList expressionList;
  private final ActionList actionList;
  private String title;
  private boolean enabled;

  public ConfigRule()
  {
    this("");
  }

  public ConfigRule(String title)
  {
    this(title, UUID.randomUUID().toString());
  }

  public ConfigRule(String title, String uuid)
  {
    this.title = title;
    this.uuid = uuid;
    expressionList = new ExpressionList();
    actionList = new ActionList();
    enabled = true;
  }

  public ConfigRule(ConfigRule rule)
  {
    this(rule.getTitle(), rule.getUuid());
    this.expressionList.getExpressions().addAll(rule.expressionList.getExpressions());
    this.actionList.getActions().addAll(rule.actionList.getActions());
    this.enabled = rule.enabled;
  }

  public String getUuid()
  {
    return uuid;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public ExpressionList getExpressionList()
  {
    return expressionList;
  }

  public ActionList getActionList()
  {
    return actionList;
  }

  public boolean isEnabled()
  {
    return enabled;
  }

  public void setEnabled(boolean enabled)
  {
    this.enabled = enabled;
  }

  @Override public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    final ConfigRule that = (ConfigRule) o;
    return uuid.equals(that.uuid);
  }

  @Override public int hashCode()
  {
    return Objects.hash(uuid);
  }

}
