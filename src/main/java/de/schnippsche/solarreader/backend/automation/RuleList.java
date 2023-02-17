package de.schnippsche.solarreader.backend.automation;

import de.schnippsche.solarreader.backend.configuration.ConfigRule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RuleList
{
  final List<Rule> rules;

  public RuleList()
  {
    rules = Collections.synchronizedList(new ArrayList<>());
  }

  public void addRule(Rule rule)
  {
    rules.add(rule);
  }

  public void removeRule(Rule rule)
  {
    rule.cleanup();
    rules.remove(rule);
  }

  public void remove(ConfigRule configRule)
  {
    Rule rule = find(configRule);
    if (rule == null)
    {
      return;
    }
    removeRule(rule);
  }

  public Rule addOrReplace(ConfigRule configRule)
  {
    Rule rule = find(configRule);
    if (rule != null)
    {
      rule.cleanup();
      rule.setConfigRule(configRule);
      rule.initialize();
      return rule;
    }
    rule = new Rule(configRule);
    rules.add(rule);
    rule.initialize();
    return rule;
  }

  public Rule find(ConfigRule configRule)
  {
    for (Rule rule : rules)
    {
      if (rule.getConfigRule().getUuid().equals(configRule.getUuid()))
      {
        return rule;
      }
    }
    return null;
  }

  public List<Rule> getRules()
  {
    return rules;
  }

}
