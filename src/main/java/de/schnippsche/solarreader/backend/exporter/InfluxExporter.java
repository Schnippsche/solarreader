package de.schnippsche.solarreader.backend.exporter;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.ConfigDatabase;
import de.schnippsche.solarreader.backend.connections.NetworkConnection;
import de.schnippsche.solarreader.backend.pusher.PushValue;
import de.schnippsche.solarreader.backend.tables.Table;
import de.schnippsche.solarreader.backend.tables.TableColumn;
import de.schnippsche.solarreader.backend.tables.TableRow;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.backend.worker.ThreadHelper;
import okhttp3.*;
import org.tinylog.Logger;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

public class InfluxExporter implements Exporter
{

  private static final String TIMESTAMP = "timestamp";
  private final List<Table> tableList;
  private final String infoUrl;
  private final ConfigDatabase configDatabase;
  private final long startTimestamp;
  private String url;

  public InfluxExporter(ConfigDatabase configDatabase)
  {
    this(configDatabase, Collections.emptyList(), System.currentTimeMillis());
  }

  public InfluxExporter(ConfigDatabase configDatabase, List<Table> tableList, long startTimestamp)
  {
    this.tableList = new ArrayList<>();
    this.configDatabase = configDatabase;
    this.infoUrl = configDatabase.getHost();
    this.tableList.addAll(tableList);
    this.startTimestamp = startTimestamp;
  }

  public String getInfluxVersion()
  {
    String testUrl =
      String.format("http%s://%s:%s/", (configDatabase.isUseSsl()) ? "s" : "", configDatabase.getHost(), configDatabase.getPort());
    Request request = new Request.Builder().url(testUrl).build();
    Call call = NetworkConnection.HTTPCLIENT.newCall(request);
    try (Response response = call.execute())
    {
      String version = response.header("X-Influxdb-Version", "unknown");
      Logger.debug("influx Version={}", version);
      return version;
    } catch (Exception e)
    {
      Logger.error("Can't connect to {}:{}", testUrl, e.getMessage());
    }

    return null;
  }

  public Pair test()
  {
    Pair faultPair = new Pair("500", SolarMain.languageHelper.replacePlaceholder("{database.error.noconnection}"));
    String version = getInfluxVersion();
    if (version == null)
    {
      Logger.error("no connection");
      return faultPair;
    }
    // since Influx 2.1 the return value starts with a 'v' e.g. v2.1
    if (version.toLowerCase().startsWith("v"))
    {
      version = version.substring(1);
    }
    if (version.startsWith("1") || version.startsWith("2"))
    {
      configDatabase.setVersion(version);
      // Send empty request
      Request request = buildRequest("");
      if (request == null)
      {
        return faultPair;
      }
      Call call = NetworkConnection.HTTPCLIENT.newCall(request);
      try (Response response = call.execute())
      {
        Logger.debug("response of url test: code: {} message:{}", response.code(), response.message());
        if (!response.isSuccessful())
        {
          return faultPair;
        }

      } catch (Exception e)
      {
        return faultPair;
      }
      return new Pair("200", "");
    }

    Logger.error("unsupported or unknown influx DB version {}", version);
    return new Pair("500", SolarMain.languageHelper.replacePlaceholder("{database.error.versionnotsupported}"));
  }

  public void setTables(List<Table> tableList)
  {
    this.tableList.clear();
    this.tableList.addAll(tableList);
  }

  public void setTable(Table table)
  {
    this.tableList.clear();
    this.tableList.add(table);
  }

  @Override public synchronized void export()
  {
    Logger.debug("InfluxExporter export to {} at {} started...", configDatabase.getDescription(), infoUrl);
    configDatabase.setLastCall(LocalDateTime.now());
    StringBuilder builder = new StringBuilder();
    long currentTimestampSeconds = startTimestamp / 1000;
    boolean valid = false;
    // tables with same name must be summarized in rows
    for (int t1 = 0; t1 < tableList.size(); t1++)
    {
      for (int t2 = 0; t2 < tableList.size(); t2++)
      {
        if (t1 != t2 && tableList.get(t1).getTableName().equals(tableList.get(t2).getTableName()))
        {
          tableList.get(t1).getTableRows().addAll(tableList.get(t2).getTableRows());
          tableList.get(t2).getTableRows().clear();
        }
      }
    }
    //
    for (Table table : tableList)
    {
      for (TableRow row : table.getTableRows())
      {
        builder.append(table.getTableName());
        builder.append(' ');
        String rowData = getColumnsWithoutTimestamp(row);
        if (!rowData.isEmpty())
        {
          builder.append(rowData).append(" ");
          TableColumn timestampColumn = getTimestampColumn(row);
          builder.append((timestampColumn == null) ? currentTimestampSeconds : timestampColumn.getValue());
          // leave it to \n for influx
          builder.append("\n");
          valid = true;
        }
      }
    }
    if (valid)
    {
      sendRequest(builder.toString());
    } else
    {
      Logger.warn("empty table(s), skip export");
    }
    Logger.debug("InfluxExporter export to {} finished", configDatabase.getDescription());
  }

  private void sendRequest(String data)
  {
    Request request = buildRequest(data);
    if (request != null)
    {
      PushValue<Request> pushValue = new PushValue<>(request);
      ThreadHelper.getInfluxPusher().addPushValue(pushValue);
    }
  }

  private Request buildRequest(String data)
  {
    if (configDatabase.getVersion() == null)
    {
      configDatabase.setVersion(getInfluxVersion());
    }
    String version = configDatabase.getVersion();
    if (version == null)
    {
      Logger.error("influx version is null");
      return null;
    }
    if (version.startsWith("2") || version.startsWith("v2"))
    {
      return buildInfluxV2Request(data);
    }
    if (version.startsWith("1") || version.startsWith("v1"))
    {
      return buildInfluxV1Request(data);
    }
    Logger.error("unsupported or unknown influx DB version {}", version);
    return null;
  }

  private Request buildInfluxV1Request(String parameter)
  {
    this.url =
      String.format("http%s://%s:%s/write?db=%s&precision=s", (configDatabase.isUseSsl()) ? "s" : "", configDatabase.getHost(), configDatabase.getPort(), configDatabase.getDbName());
    Logger.debug("sendPost to Url {}, Params={}", url, parameter);
    RequestBody formBody = RequestBody.create(parameter, NetworkConnection.MEDIA_TYPE_STRING);
    Request.Builder requestBuilder = new Request.Builder().url(url).addHeader("User-Agent", "Solarreader");
    if (configDatabase.getUser() != null && !configDatabase.getUser().isEmpty() && configDatabase.getPassword() != null
        && !configDatabase.getPassword().isEmpty())
    {
      Logger.debug("do Authorization with user {} and pass {}", configDatabase.getUser(), configDatabase.getPassword());
      requestBuilder.addHeader("Authorization", Credentials.basic(configDatabase.getUser(), configDatabase.getPassword()));
    }
    return requestBuilder.post(formBody).build();
  }

  private Request buildInfluxV2Request(String parameter)
  {
    this.url =
      String.format("http%s://%s:%s/api/v2/write?bucket=%s&precision=s&org=%s", (configDatabase.isUseSsl()) ? "s" : "", configDatabase.getHost(), configDatabase.getPort(), configDatabase.getDbName(), configDatabase.getUser());
    Logger.debug("sendPost to Url {}, Params={}", url, parameter);
    RequestBody formBody = RequestBody.create(parameter, NetworkConnection.MEDIA_TYPE_STRING);
    Request.Builder requestBuilder = new Request.Builder().url(url).addHeader("User-Agent", "Solarreader")
                                                          .addHeader("Authorization",
                                                            "Token " + configDatabase.getPassword());
    return requestBuilder.post(formBody).build();
  }

  private String getColumnsWithoutTimestamp(TableRow tableRow)
  {
    StringJoiner joiner = new StringJoiner(",");
    for (TableColumn c : tableRow.getColumns())
    {
      if (!TIMESTAMP.equalsIgnoreCase(c.getName()))
      {
        String s = c.getName() + "=" + c.getTypedValue();
        joiner.add(s);
      }
    }
    return joiner.toString();
  }

  private TableColumn getTimestampColumn(TableRow tableRow)
  {
    for (TableColumn c : tableRow.getColumns())
    {
      if (TIMESTAMP.equalsIgnoreCase(c.getName()))
      {
        return c;
      }
    }
    return null;
  }

}
