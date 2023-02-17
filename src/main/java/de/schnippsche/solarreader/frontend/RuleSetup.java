package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.automation.ActionList;
import de.schnippsche.solarreader.backend.automation.CommandType;
import de.schnippsche.solarreader.backend.automation.ExpressionList;
import de.schnippsche.solarreader.backend.automation.actions.*;
import de.schnippsche.solarreader.backend.automation.expressions.*;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigRule;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;
import de.schnippsche.solarreader.frontend.elements.HtmlRadiobuttonList;

import java.util.*;
import java.util.stream.Collectors;

public class RuleSetup
{
  private static final String EMPTY = "";
  private static ConfigRule currentConfigRule;
  private final Map<String, String> formValues;
  private final NumericHelper numericHelper;
  private final Map<String, String> rulemaps;
  private final DialogHelper dialogHelper;

  public RuleSetup(Map<String, String> formValues)
  {
    this.formValues = formValues;
    numericHelper = new NumericHelper();
    rulemaps = new HashMap<>();
    dialogHelper = new DialogHelper();
  }

  public AjaxResult getAjaxCode(String action, String page)
  {
    switch (action)
    {
      case "checkrulestep":
        return checkStep(page);
      case "addnewcondition":
        return addNewCondition(page);
      case "addnewaction":
        return addNewAction(page);
      case "delcondition":
        return removeCondition(page);
      case "delaction":
        return removeAction(page);
      case "getcachedfields":
        return getCachedResultFields(page);
      case "getcommands":
        return getTypes(page);
      case "getchoices":
        return getChoices(page);

      default:
        return null;
    }

  }

  public AjaxResult addNewCondition(String page)
  {
    ExpressionList expressionList = currentConfigRule.getExpressionList();
    Expression newExpression = null;
    switch (page)
    {
      case "1":
        newExpression = new MqttTopicExpression();
        break;
      case "3":
        newExpression = new DeviceValueExpression();
        break;
      case "4":
        newExpression = new PeriodExpression();
        break;
      case "5":
        newExpression = new DayExpression();
        break;
      case "6":
        newExpression = new NightExpression();
        break;
      default:
        return new AjaxResult(false, "unknown id:" + page);
    }
    expressionList.addExpression(newExpression);
    // Last added element
    String html = newExpression.getHtmlCode();
    return new AjaxResult(true, html);

  }

  public AjaxResult addNewAction(String page)
  {
    ActionList actionList = currentConfigRule.getActionList();
    Action newAction = null;
    switch (page)
    {
      case "1":
        newAction = new DeviceAction();
        break;
      case "2":
        newAction = new RelaisAction();
        break;
      case "3":
        newAction = new MqttAction();
        break;
      case "4":
        newAction = new UrlAction();
        break;
      case "5":
        newAction = new DatabaseAction();
        break;
      default:
        return new AjaxResult(false, "unknown id:" + page);
    }
    actionList.addAction(newAction);
    String html = newAction.getHtmlCode();
    return new AjaxResult(true, html);
  }

  private AjaxResult removeCondition(String page)
  {
    ExpressionList expressionList = currentConfigRule.getExpressionList();
    boolean removed = expressionList.getExpressions().removeIf(e -> e.getUuid().equals(page));
    if (removed)
    {
      return new AjaxResult(true);
    }

    return new AjaxResult(false, "invalid id " + page);
  }

  private AjaxResult removeAction(String page)
  {
    ActionList actionList = currentConfigRule.getActionList();
    boolean removed = actionList.getActions().removeIf(a -> a.getUuid().equals(page));
    if (removed)
    {
      return new AjaxResult(true);
    }
    return new AjaxResult(false, "invalid id " + page);
  }

  private AjaxResult getCachedResultFields(String deviceUuid)
  {
    List<Pair> pairs = dialogHelper.getCachedResultFieldsFor(deviceUuid);
    String html = new HtmlOptionList(pairs).getOptions("");
    return new AjaxResult(true, html);

  }

  private AjaxResult getTypes(String deviceUuid)
  {
    List<Pair> pairs = dialogHelper.getTypesFor(deviceUuid);
    String html = new HtmlOptionList(pairs).getOptions("");
    return new AjaxResult(true, html);
  }

  private AjaxResult getChoices(String art)
  {
    String[] arr = art.split("@");
    if (arr.length == 3)
    {
      String deviceUuid = arr[0];
      CommandType commandType = CommandType.valueOf(arr[1]);
      int index = numericHelper.getInteger(arr[2], 1);
      List<Pair> pairs = dialogHelper.getActionsFor(deviceUuid, commandType, index);
      String html = new HtmlOptionList(pairs).getOptions("");
      return new AjaxResult(true, html);
    }
    return new AjaxResult(false, "no valid identifier");
  }

  public synchronized String getModalCode()
  {
    String step = formValues.getOrDefault("step", EMPTY);
    int page = numericHelper.getInteger(formValues.getOrDefault("page", "0"));
    switch (step)
    {
      case "newrule":
        currentConfigRule = new ConfigRule();
        currentConfigRule.setTitle(SolarMain.languageHelper.replacePlaceholder("{rulesetup.newrule.title}"));
        return showRuleStep(1);
      case "saverule":
        return saveRule(page);
      case "showrule":
        return showRuleStep(page);
      case "editrule":
        // Make a deep copy!
        currentConfigRule =
          new ConfigRule(Config.getInstance().getConfigRuleFromUuid(formValues.getOrDefault("id", "0")));
        return showRuleStep(1);
      case "confirmdeleterule":
        return confirmDelete();
      case "deleterule":
        Config.getInstance().removeRule(currentConfigRule);
        return EMPTY;
      default:
        return EMPTY;
    }
  }

  private String saveRule(int page)
  {
    switch (page)
    {
      case 1:
        return saveStep1();
      case 2:
        return saveStep2();
      case 3:
        return saveStep3();
      case 4:
        return saveStep4();
      default:
        return EMPTY;
    }
  }

  private String showRuleStep(int page)
  {
    switch (page)
    {
      case 1:
        return showStep1();
      case 2:
        return showStep2();
      case 3:
        return showStep3();
      case 4:
        return showStep4();
      default:
        return EMPTY;
    }
  }

  private String confirmDelete()
  {
    Map<String, String> infomap = new HashMap<>();
    infomap.put("[description]", currentConfigRule.getTitle());
    infomap.put("[devicename]", currentConfigRule.getUuid());
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "confirmdeleterulemodal.tpl").getHtmlCode(infomap);
  }

  private String showStep1()
  {
    List<Pair> pairs = new ArrayList<>();
    List<ConfigRule> rules = Config.getInstance().getConfigRules();
    rules.sort(Comparator.comparing(ConfigRule::getTitle));
    for (ConfigRule rule : rules)
    {
      pairs.add(new Pair(rule.getUuid(), rule.getTitle()));
    }
    pairs.add(new Pair("new", SolarMain.languageHelper.replacePlaceholder("{rulesetup.newrule.title}")));
    HtmlRadiobuttonList choiceList = new HtmlRadiobuttonList();
    List<String> selected = new ArrayList<>();
    selected.add("new");
    String html = choiceList.getHtml(pairs, "rule", selected);
    rulemaps.put("[rules]", html);
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "rulemodal1.tpl").getHtmlCode(rulemaps);
  }

  private String showStep2()
  {
    ExpressionList expressionList = currentConfigRule.getExpressionList();
    String conditions =
      expressionList.getExpressions().stream().map(Expression::getHtmlCode).collect(Collectors.joining());
    rulemaps.put("[conditions]", conditions);
    rulemaps.put("[rulename]", currentConfigRule.getTitle());
    rulemaps.put("[enabledelete]", "");
    String html = new HtmlElement(SolarMain.TEMPLATES_PATH + "rulemodal2.tpl").getHtmlCode(rulemaps);
    return SolarMain.languageHelper.replacePlaceholder(html);
  }

  private String showStep3()
  {
    ActionList actionList = currentConfigRule.getActionList();
    String actions = actionList.getActions().stream().map(Action::getHtmlCode).collect(Collectors.joining());
    rulemaps.put("[actions]", actions);
    // there must be at least one relais for adding RelaisAction
    rulemaps.put("[relaisvisible]", dialogHelper.getFirstCommand() == null ? "d-none" : "");
    String html = new HtmlElement(SolarMain.TEMPLATES_PATH + "rulemodal3.tpl").getHtmlCode(rulemaps);
    return SolarMain.languageHelper.replacePlaceholder(html);
  }

  private String showStep4()
  {
    // Create summary
    ActionList actionList = currentConfigRule.getActionList();
    StringBuilder builder = new StringBuilder();
    ExpressionList expressionList = currentConfigRule.getExpressionList();
    boolean isFirst = true;
    for (Expression expression : expressionList.getExpressions())
    {
      if (!isFirst)
      {
        builder.append(expression.getConditionalOperator()).append(" ");
      }
      builder.append(expression.getSummary()).append("<br>");
      isFirst = false;
    }

    String summaryAction = actionList.getActions().stream().map(Action::getSummary).collect(Collectors.joining("<br>"));
    rulemaps.put("[ruletitle]", currentConfigRule.getTitle());
    rulemaps.put("[summarycondition]", builder.toString());
    rulemaps.put("[summaryaction]", summaryAction);
    rulemaps.put("[RULEENABLED]", currentConfigRule.isEnabled() ? "checked" : "");
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "rulemodal4.tpl").getHtmlCode(rulemaps);
  }

  private String saveStep1()
  {
    String uuid = formValues.getOrDefault("rule", "");
    // Work on copy
    currentConfigRule = new ConfigRule(Config.getInstance().getConfigRuleFromUuid(uuid));

    return showStep2();
  }

  private String saveStep2()
  {
    ExpressionList expressionList = currentConfigRule.getExpressionList();
    for (Expression expression : expressionList.getExpressions())
    {
      expression.setValuesFromMap(formValues);
    }
    currentConfigRule.setTitle(formValues.get("rulename"));
    return showStep3();
  }

  private String saveStep3()
  {
    ActionList actionList = currentConfigRule.getActionList();
    for (Action action : actionList.getActions())
    {
      action.setValuesFromMap(formValues);
    }
    return showStep4();
  }

  private String saveStep4()
  {
    currentConfigRule.setEnabled("on".equals(formValues.getOrDefault("enable", "off")));
    List<ConfigRule> rules = Config.getInstance().getConfigRules();
    int index = rules.indexOf(currentConfigRule);
    if (index != -1)
    {
      rules.set(index, currentConfigRule);
    } else
    {
      rules.add(currentConfigRule);
    }
    dialogHelper.saveConfiguration();
    Config.getInstance().getRuleList().addOrReplace(currentConfigRule);

    return EMPTY;
  }

  private AjaxResult checkStep(String page)
  {
    if ("2".equals(page))
    {
      final String newName = formValues.getOrDefault("rulename", EMPTY);
      boolean present = false;
      for (ConfigRule cd : Config.getInstance().getConfigRules())
      {
        if (cd.getTitle().equalsIgnoreCase(newName) && !cd.getUuid().equals(currentConfigRule.getUuid()))
        {
          present = true;
          break;
        }
      }
      if (present)
      {
        return new AjaxResult(false, SolarMain.languageHelper.replacePlaceholder("{rulesetup.name.exists}"));
      }
      boolean ok = false;
      for (Expression expression : currentConfigRule.getExpressionList().getExpressions())
      {
        if (!expression.isOnlyAdditional())
        {
          ok = true;
          break;
        }
      }
      if (!ok)
      {
        return new AjaxResult(false, SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.mainexpression.error}"));
      }
    }
    if ("3".equals(page))
    {
      // one action
      if (currentConfigRule.getActionList().getSize() == 0)
      {
        return new AjaxResult(false, SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.emptyaction.error}"));
      }
    }
    return new AjaxResult(true);
  }

}
