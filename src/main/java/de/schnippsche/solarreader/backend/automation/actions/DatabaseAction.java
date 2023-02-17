package de.schnippsche.solarreader.backend.automation.actions;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigDatabase;
import de.schnippsche.solarreader.backend.exporter.InfluxExporter;
import de.schnippsche.solarreader.backend.tables.Table;
import de.schnippsche.solarreader.backend.tables.TableColumn;
import de.schnippsche.solarreader.backend.tables.TableColumnType;
import de.schnippsche.solarreader.backend.tables.TableRow;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.HtmlElement;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseAction extends Action
{
  private String databaseUuid;
  private String table;
  private String column;
  private String value;

  @Override public void doAction()
  {
    ConfigDatabase configDatabase = Config.getInstance().getConfigDatabaseFromUuid(databaseUuid);
    if (configDatabase == null)
    {
      Logger.error("no existing database with uuid {}, abort action", databaseUuid);
      return;
    }
    if (!configDatabase.isEnabled())
    {
      Logger.warn("action skipped because database is disabled");
      return;
    }
    InfluxExporter influxExporter = new InfluxExporter(configDatabase);
    Table exportTable = new Table(table);
    TableRow tableRow = new TableRow();
    TableColumnType tableColumnType =
      numericHelper.isNumericValue(value) ? TableColumnType.NUMBER : TableColumnType.STRING;
    TableColumn tableColumn = new TableColumn(column, tableColumnType, value);
    tableRow.putColumn(tableColumn);
    exportTable.addTableRow(tableRow);
    influxExporter.setTable(exportTable);
    influxExporter.export();

  }

  @Override protected String getHtml(Map<String, String> infomap)
  {
    if (template == null)
    {
      template = new HtmlElement(SolarMain.TEMPLATES_PATH + "actionsenddatabase.tpl");
    }
    List<Pair> deviceList = new ArrayList<>();
    for (ConfigDatabase database : Config.getInstance().getConfigDatabases())
    {
      deviceList.add(new Pair(database.getUuid(), database.getDescription()));
    }
    infomap.put("[databaselist]", new HtmlOptionList(deviceList).getOptions(databaseUuid));
    infomap.put("[TABLE]", table);
    infomap.put("[COLUMN]", column);
    infomap.put("[VALUE]", value);
    return SolarMain.languageHelper.replacePlaceholder(template.getHtmlCode(infomap));

  }

  @Override public void setValuesFromMap(Map<String, String> newValues)
  {
    databaseUuid = newValues.getOrDefault("database_" + getUuid(), "");
    table = newValues.getOrDefault("table_" + getUuid(), "");
    column = newValues.getOrDefault("column_" + getUuid(), "");
    value = newValues.getOrDefault("value_" + getUuid(), "");
  }

  @Override public String getSummary()
  {
    String database = Config.getInstance().getConfigDatabaseFromUuid(databaseUuid).getDescription();
    String formatter = SolarMain.languageHelper.replacePlaceholder("{rulesetup.action.database.summary}");
    return String.format(formatter, database, table, column, value);
  }

}
