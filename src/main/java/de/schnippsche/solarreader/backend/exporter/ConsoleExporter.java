package de.schnippsche.solarreader.backend.exporter;

import de.schnippsche.solarreader.backend.tables.Table;
import de.schnippsche.solarreader.backend.tables.TableColumn;
import de.schnippsche.solarreader.backend.tables.TableRow;
import org.tinylog.Logger;

import java.util.List;

public class ConsoleExporter implements Exporter
{
  private final List<Table> tableList;

  public ConsoleExporter(List<Table> tableList)
  {
    this.tableList = tableList;
  }

  @Override public void export()
  {
    Logger.info("Konsolenlogger zum Testen");
    for (Table table : tableList)
    {
      Logger.debug("Tabelle " + table.getTableName());
      for (TableRow rows : table.getTableRows())
      {
        Logger.debug(" Tabellenzeile:");
        for (TableColumn column : rows.getColumns())
        {
          Logger.debug("  " + column);
        }
      }
    }
  }

}
