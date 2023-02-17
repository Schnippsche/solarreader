package de.schnippsche.solarreader.backend.automation;

import de.schnippsche.solarreader.backend.automation.expressions.Expression;
import de.schnippsche.solarreader.backend.configuration.ConfigRule;
import org.tinylog.Logger;

import java.time.LocalDateTime;

public class Rule
{
  private ConfigRule configRule;
  private boolean active;
  private LocalDateTime lastActivity;

  public Rule(ConfigRule configRule)
  {
    this.configRule = configRule;
    lastActivity = null;
  }

  /**
   * add all listeners to the expressions
   */
  public void initialize()
  {
    for (Expression expression : this.configRule.getExpressionList().getExpressions())
    {
      expression.initialize(this);
    }
  }

  /**
   * remove all listeners from the expressions
   */
  public void cleanup()
  {
    Logger.debug("cleanup rule ");
    for (Expression expression : this.configRule.getExpressionList().getExpressions())
    {
      expression.cleanup();
    }
  }

  /**
   * clears the values if rule actions executed
   */
  public void clear()
  {
    Logger.debug("clear rule ");
    for (Expression expression : this.configRule.getExpressionList().getExpressions())
    {
      expression.clear();
    }
  }

  public ConfigRule getConfigRule()
  {
    return configRule;
  }

  public void setConfigRule(ConfigRule configRule)
  {
    this.configRule = configRule;
  }

  public boolean isActive()
  {
    return active;
  }

  public LocalDateTime getLastActivity()
  {
    return lastActivity;
  }

  public void doRule()
  {
    Logger.debug("check rule {} ...", configRule.getTitle());
    if (!configRule.isEnabled())
    {
      Logger.debug("rule is disabled");
      return;
    }
    if (active)
    {
      Logger.debug("rule is active!");
      return;
    }
    if (configRule.getExpressionList().isTrue())
    {
      Logger.debug("rule is true, process actions from rule {}", configRule.getTitle());
      active = true;
      lastActivity = LocalDateTime.now();
      configRule.getActionList().doActions();
      clear();
      active = false;
    } else
    {
      Logger.debug("rule is false");
    }
  }

}
