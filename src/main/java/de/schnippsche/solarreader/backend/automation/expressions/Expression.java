package de.schnippsche.solarreader.backend.automation.expressions;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.automation.CompareOperator;
import de.schnippsche.solarreader.backend.automation.ConditionalOperator;
import de.schnippsche.solarreader.backend.automation.Rule;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.HtmlElement;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;

import java.math.BigDecimal;
import java.util.*;

public abstract class Expression
{
  private final String uuid;
  private final transient Map<String, String> infomap;
  private final transient NumericHelper numericHelper;
  private final transient boolean onlyAdditional;
  protected transient HtmlElement template;
  private ConditionalOperator conditionalOperator;

  protected Expression(boolean onlyAdditional)
  {
    this.conditionalOperator = ConditionalOperator.AND;
    this.uuid = UUID.randomUUID().toString();
    this.template = null;
    this.infomap = new HashMap<>();
    this.numericHelper = new NumericHelper();
    this.onlyAdditional = onlyAdditional;
  }

  protected static String getComparatorHtml(CompareOperator compareOperator)
  {
    List<Pair> comparators = new ArrayList<>();
    comparators.add(new Pair("GREATER", SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.greater}")));
    comparators.add(new Pair("LESS", SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.less}")));
    comparators.add(new Pair("EQUAL", SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.equal}")));
    comparators.add(new Pair("UNEQUAL", SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.unequal}")));
    String html = new HtmlOptionList(comparators).getOptions(compareOperator.toString());
    return SolarMain.languageHelper.replacePlaceholder(html);
  }

  public void initialize(Rule rule)
  {
    // can be overwritten
  }

  public void cleanup()
  {
    // can be overwritten
  }

  public void clear()
  {
    // can be overwritten
  }

  public ConditionalOperator getConditionalOperator()
  {
    return conditionalOperator;
  }

  public void setConditionalOperator(ConditionalOperator conditionalOperator)
  {
    this.conditionalOperator = conditionalOperator;
  }

  public String getUuid()
  {
    return uuid;
  }

  public abstract boolean isTrue();

  public String getTranslatedCompareOperator(CompareOperator compareOperator)
  {
    switch (compareOperator)
    {
      case EQUAL:
        return SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.equal}");
      case LESS:
        return SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.less}");
      case GREATER:
        return SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.greater}");
      case UNEQUAL:
        return SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.unequal}");
    }
    return "";
  }

  public String getHtmlCode()
  {
    infomap.put("[id]", this.uuid);
    List<Pair> conditionals = new ArrayList<>();
    conditionals.add(new Pair("AND", SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.and}")));
    conditionals.add(new Pair("OR", SolarMain.languageHelper.replacePlaceholder("{rulesetup.expression.or}")));
    infomap.put("[comparelist]", new HtmlOptionList(conditionals).getOptions(conditionalOperator.toString()));
    return getHtml(infomap);
  }

  protected abstract String getHtml(Map<String, String> infomap);

  public abstract void setValuesFromMap(Map<String, String> newValues);

  public abstract String getSummary();

  public boolean isOnlyAdditional()
  {
    return onlyAdditional;
  }

  public boolean compare(String s1, CompareOperator compareOperator, String s2)
  {
    if (s1 == null || s2 == null)
    {
      return false;
    }
    int compareResult = compareObjects(s1, s2);
    switch (compareOperator)
    {
      case EQUAL:
        return compareResult == 0;
      case UNEQUAL:
        return compareResult != 0;
      case LESS:
        return compareResult < 0;
      case GREATER:
        return compareResult > 0;
      default:
        return false;
    }
  }

  private int compareObjects(String o1, String o2)
  {
    if (numericHelper.isNumericValue(o1) && numericHelper.isNumericValue(o2))
    {
      BigDecimal bd1 = numericHelper.getBigDecimal(o1);
      BigDecimal bd2 = numericHelper.getBigDecimal(o2);
      return bd1.compareTo(bd2);
    }
    return o1.compareTo(o2);
  }

  @Override public String toString()
  {
    return "Expression{" + "uuid='" + uuid + '\'' + ", onlyAdditional=" + onlyAdditional + ", conditionalOperator="
           + conditionalOperator + '}';
  }

}
