package de.schnippsche.solarreader.backend.tables;

import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.TableField;
import de.schnippsche.solarreader.backend.utils.MathEvalBigDecimal;
import org.tinylog.Logger;

import java.util.*;

public class ExportTables
{

  private final MathEvalBigDecimal mathEval;

  public ExportTables()
  {
    mathEval = new MathEvalBigDecimal();
  }

  public List<Table> convert(List<ResultField> resultFields, List<TableField> tableFields)
  {
    final List<Table> resultTables = new ArrayList<>();
    if (resultFields == null)
    {
      Logger.debug("no valid result fields!");
      return resultTables;
    }
    if (tableFields == null || tableFields.isEmpty())
    {
      Logger.debug("empty table fields, skip converting...");
      return resultTables;
    }
    mathEval.setResultFieldsAsVariables(resultFields);
    // group tables by tablename
    Map<String, Set<TableField>> newTableMap = new HashMap<>();
    for (TableField field : tableFields)
    {
      newTableMap.computeIfAbsent(field.getTablename(), k -> new HashSet<>()).add(field);
    }
    // iterate over all groups
    for (Map.Entry<String, Set<TableField>> entry : newTableMap.entrySet())
    {
      Table table = new Table(entry.getKey());
      TableRow tableRow = new TableRow();
      // create TableColumn from field and value
      for (TableField tableField : entry.getValue())
      {
        Object value = mathEval.calculateValue(tableField.getSourcetype(), resultFields, tableField.getSourcevalue());
        if (value != null)
        {
          TableColumn tableColumn = new TableColumn(tableField.getColumnname(), tableField.getColumntype(), value);
          tableRow.putColumn(tableColumn);
        } else
        {
          Logger.debug("field '{}' with type '{}' is null or invalid!", tableField.getSourcevalue(), tableField.getSourcetype());
        }
      }
      if (!tableRow.getColumns().isEmpty())
      {
        resultTables.add(table);
        table.addTableRow(tableRow);
      }
    }
    return resultTables;
  }

}
