package de.schnippsche.solarreader.backend.automation;

import de.schnippsche.solarreader.backend.automation.expressions.Expression;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;

public class ExpressionList
{
  private final List<Expression> expressions;

  public ExpressionList()
  {
    expressions = new ArrayList<>();
  }

  public void addExpression(Expression expression)
  {
    this.expressions.add(expression);
  }

  public int getSize()
  {
    return this.expressions.size();
  }

  public List<Expression> getExpressions()
  {
    return this.expressions;
  }

  public boolean isInTimeRange()
  {
    for (Expression expression : expressions)
    {
      if (expression.isOnlyAdditional() && !expression.isTrue())
      {
        return false;
      }
    }
    return true;
  }

  public boolean isTrue()
  {
    if (!isInTimeRange())
    {
      Logger.debug("is not in time range");
      return false;
    }
    boolean result = true;
    for (Expression expression : expressions)
    {
      boolean exprResult = expression.isTrue();
      Logger.debug("expression {} is {}", expression, exprResult);
      if (expression.getConditionalOperator().equals(ConditionalOperator.AND))
      {
        result = result && exprResult;
      } else
      {
        result = result || exprResult;
      }
    }
    Logger.debug("result is {}", result);
    return result;
  }

}
