package de.schnippsche.solarreader.backend.tables;

import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import de.schnippsche.solarreader.backend.fields.TableField;
import de.schnippsche.solarreader.backend.utils.JsonTools;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StatistikTable
{
  private final List<TableField> tableFields;
  private BigDecimal wattTotalToday;

  public StatistikTable()
  {
    tableFields = new JsonTools().readSpecification("statistics").getDatabasefields();
  }

  public void setWattTotalToday(BigDecimal wattTotalToday)
  {
    this.wattTotalToday = wattTotalToday;
  }

  public List<Table> createTable()
  {
    List<ResultField> resultFields = new ArrayList<>();
    resultFields.add(new ResultField("wattperday", ResultFieldStatus.VALID, FieldType.NUMBER, wattTotalToday));
    return new ExportTables().convert(resultFields, tableFields);
  }

}
