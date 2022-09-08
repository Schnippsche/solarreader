package de.schnippsche.solarreader.backend.exporter;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;
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
import java.util.stream.Collectors;

public class InfluxExporter implements Exporter
{

  private static final String TIMESTAMP = "timestamp";
  private final List<Table> tableList;
  private final String infoUrl;
  private final ConfigDatabase configDatabase;
  private String url;

  public InfluxExporter(ConfigDatabase configDatabase)
  {
    this(configDatabase, Collections.emptyList());
  }

  public InfluxExporter(ConfigDatabase configDatabase, List<Table> tableList)
  {
    this.tableList = new ArrayList<>();
    this.configDatabase = configDatabase;
    this.infoUrl = configDatabase.getHost();
    this.tableList.addAll(tableList);
  }

  public String getInfluxVersion()
  {
    String testUrl = String.format("http%s://%s:%s/", (configDatabase.isUseSsl()) ? "s" : "", configDatabase.getHost(), configDatabase.getPort());
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

  @Override public synchronized void export()
  {
    Logger.debug("InfluxExporter export to {} at {} started...", configDatabase.getDescription(), infoUrl);
    configDatabase.setLastCall(LocalDateTime.now());
    StringBuilder builder = new StringBuilder();
    long currentTimestamp = Config.getInstance().getStandardValues().getTimestampSeconds();
    boolean valid = false;
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
          builder.append((timestampColumn == null) ? currentTimestamp : timestampColumn.getValue());
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
    if (version.startsWith("2"))
    {
      return buildInfluxV2Request(data);
    }
    if (version.startsWith("1"))
    {
      return buildInfluxV1Request(data);
    }
    Logger.error("unsupported or unknown influx DB version {}", version);
    return null;
  }

  private Request buildInfluxV1Request(String parameter)
  {
    this.url = String.format("http%s://%s:%s/write?db=%s&precision=s", (configDatabase.isUseSsl()) ? "s" : "", configDatabase.getHost(), configDatabase.getPort(), configDatabase.getDbName());
    Logger.debug("sendPost to Url {}, Params={}", url, parameter);
    RequestBody formBody = RequestBody.create(parameter, NetworkConnection.MEDIA_TYPE_STRING);
    Request.Builder requestBuilder = new Request.Builder().url(url).addHeader("User-Agent", "Solarreader");
    if (configDatabase.getUser() != null && !configDatabase.getUser()
                                                           .isEmpty() && configDatabase.getPassword() != null && !configDatabase.getPassword()
                                                                                                                                .isEmpty())
    {
      Logger.debug("do Authorization with user {} and pass {}", configDatabase.getUser(), configDatabase.getPassword());
      requestBuilder.addHeader("Authorization", Credentials.basic(configDatabase.getUser(), configDatabase.getPassword()));
    }
    return requestBuilder.post(formBody).build();
  }

  private Request buildInfluxV2Request(String parameter)
  {
    this.url = String.format("http%s://%s:%s/api/v2/write?bucket=%s&precision=s&org=%s", (configDatabase.isUseSsl()) ? "s" : "", configDatabase.getHost(), configDatabase.getPort(), configDatabase.getDbName(), configDatabase.getUser());
    Logger.debug("sendPost to Url {}, Params={}", url, parameter);
    RequestBody formBody = RequestBody.create(parameter, NetworkConnection.MEDIA_TYPE_STRING);
    Request.Builder requestBuilder = new Request.Builder().url(url)
                                                          .addHeader("User-Agent", "Solarreader")
                                                          .addHeader("Authorization", "Token " + configDatabase.getPassword());
    return requestBuilder.post(formBody).build();
  }

  private String getColumnsWithoutTimestamp(TableRow tableRow)
  {
    return tableRow.getColumns()
                   .stream()
                   .filter(c -> !TIMESTAMP.equalsIgnoreCase(c.getName()))
                   .map(c -> c.getName() + "=" + c.getTypedValue())
                   .collect(Collectors.joining(","));
  }

  private TableColumn getTimestampColumn(TableRow tableRow)
  {
    return tableRow.getColumns().stream().filter(c -> TIMESTAMP.equalsIgnoreCase(c.getName())).findFirst().orElse(null);
  }

}
