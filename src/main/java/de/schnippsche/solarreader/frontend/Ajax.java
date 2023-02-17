package de.schnippsche.solarreader.frontend;

import com.google.gson.Gson;
import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.serializes.diskspace.Data;
import de.schnippsche.solarreader.backend.serializes.diskspace.Dataset;
import de.schnippsche.solarreader.backend.utils.FileOperation;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import org.takes.Request;
import org.takes.Response;
import org.takes.Take;
import org.takes.rq.RqHref;
import org.takes.rq.form.RqFormSmart;
import org.takes.rs.RsText;
import org.takes.rs.RsWithBody;
import org.takes.rs.RsWithHeader;
import org.tinylog.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Ajax implements Take
{

  private final Map<String, String> formValues = new HashMap<>();
  private final NumericHelper numericHelper = new NumericHelper();
  private final Gson gson = Config.getInstance().getGson();

  @Override
  public Response act(Request req) throws IOException
  {
    RqHref.Smart smart = new RqHref.Smart(req);
    String action = smart.single("action", "");
    String page = smart.single("page", "");
    RqFormSmart formSmart = new RqFormSmart(req);
    formValues.clear();
    for (final String name : formSmart.names())
    {
      formValues.put(name, formSmart.single(name));
    }
    AjaxResult ajaxResult;
    ajaxResult = new DeviceSetup(formValues).getAjaxCode(action, page);
    if (ajaxResult != null)
    {
      return new RsText(gson.toJson(ajaxResult));
    }

    ajaxResult = new DatabaseSetup(formValues).getAjaxCode(action);
    if (ajaxResult != null)
    {
      return new RsText(gson.toJson(ajaxResult));
    }

    ajaxResult = new MqttSetup(formValues).getAjaxCode(action);
    if (ajaxResult != null)
    {
      return new RsText(gson.toJson(ajaxResult));
    }

    ajaxResult = new OpenWeatherSetup(formValues).getAjaxCode(action);
    if (ajaxResult != null)
    {
      return new RsText(gson.toJson(ajaxResult));
    }

    ajaxResult = new SolarprognoseSetup(formValues).getAjaxCode(action);
    if (ajaxResult != null)
    {
      return new RsText(gson.toJson(ajaxResult));
    }

    ajaxResult = new AwattarSetup(formValues).getAjaxCode(action);
    if (ajaxResult != null)
    {
      return new RsText(gson.toJson(ajaxResult));
    }
    ajaxResult = new RuleSetup(formValues).getAjaxCode(action, page);
    if (ajaxResult != null)
    {
      return new RsText(gson.toJson(ajaxResult));
    }
    // other ajax events
    switch (action)
    {
      case "setlogsize":
        return setLogSize(smart.single("linecount", "100"));
      case "downloadlog":
        return downloadLog();
      case "getstatus":
        return new RsText(new StatusCollector().getAjax());
      case "getrules":
        return new RsText(new RulesCollector().getAjax());
      case "diskspacechart":
        return diskspaceChart();
      case "memoryspacechart":
        return memoryspaceChart();
      default:
        return new RsText(gson.toJson(new AjaxResult(false, "unknown action " + action)));
    }
  }

  private Response memoryspaceChart()
  {
    Runtime rt = Runtime.getRuntime();
    Data mainData = new Data();
    mainData.titletext = SolarMain.languageHelper.replacePlaceholder("{card.memoryspaces.title}");
    Dataset freeSet = new Dataset();
    freeSet.label = SolarMain.languageHelper.replacePlaceholder("{charts.free}");
    freeSet.backgroundColor.add("#198754");
    freeSet.backgroundColor.add("#dc3545");
    mainData.datasets.add(freeSet);
    mainData.labels.add(SolarMain.languageHelper.replacePlaceholder("{charts.free}"));
    mainData.labels.add(SolarMain.languageHelper.replacePlaceholder("{charts.used}"));
    freeSet.data.add("" + (rt.freeMemory() / 1048576f));
    freeSet.data.add("" + (rt.totalMemory() - rt.freeMemory()) / 1048576f);
    return new RsText(gson.toJson(mainData, Data.class));
  }

  private Response diskspaceChart()
  {
    FileSystem fileSystem = FileSystems.getDefault();
    Data mainData = new Data();
    mainData.titletext = SolarMain.languageHelper.replacePlaceholder("{card.diskspaces.title}");
    Dataset freeSet = new Dataset();
    freeSet.label = SolarMain.languageHelper.replacePlaceholder("{charts.free}");
    freeSet.backgroundColor.add("#198754");
    Dataset usedSet = new Dataset();
    usedSet.label = SolarMain.languageHelper.replacePlaceholder("{charts.used}");
    usedSet.backgroundColor.add("#dc3545");
    mainData.datasets.add(usedSet);
    mainData.datasets.add(freeSet);
    Iterable<FileStore> iterable = fileSystem.getFileStores();
    iterable.forEach(store ->
    {
      try
      {
        mainData.labels.add(store.toString());
        freeSet.data.add("" + (store.getUsableSpace() / 1073741824f));
        usedSet.data.add("" + (store.getTotalSpace() - store.getUsableSpace()) / 1073741824f);
      } catch (IOException e)
      {
        e.printStackTrace();
      }
    });
    return new RsText(gson.toJson(mainData, Data.class));
  }

  private Response downloadLog()
  {
    try
    {
      Path path = getCurrentLogPath();
      return new RsWithHeader(new RsWithBody(new ByteArrayInputStream(Files.readAllBytes(path))), "Content-Disposition", String.format("attachment; filename=\"%s\"", "solarreader.log"));
    }
    catch (Exception e)
    {
      Logger.debug(e.getMessage());
    }

    return new RsText("Error");
  }

  private RsText setLogSize(String lineCount)
  {
    AjaxResult result;
    try
    {
      Path path = getCurrentLogPath();
      String logcontent = new FileOperation().lastLinesFromFile(path, numericHelper.getInteger(lineCount));
      result = new AjaxResult(true, logcontent);
    }
    catch (Exception e)
    {
      Logger.debug(e.getMessage());
      result = new AjaxResult(false, e.getMessage());
    }

    return new RsText(gson.toJson(result));
  }

  private Path getCurrentLogPath()
  {
    String log = System.getProperty("tinylog.writer.latest");
    if (log == null)
    {
      String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      log = "solarreader_" + formattedDate + ".log";
    }
    return Paths.get(log);
  }

}
