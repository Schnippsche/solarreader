package de.schnippsche.solarreader;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.worker.ShutdownHook;
import de.schnippsche.solarreader.backend.worker.ThreadHelper;
import de.schnippsche.solarreader.frontend.HttpServer;
import de.schnippsche.solarreader.frontend.LanguageHelper;
import org.tinylog.Logger;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class SolarMain
{

  public static final String NEWLINE = System.getProperty("line.separator");
  public static final String TEMPLATES_PATH = "html/templates/";
  public static LanguageHelper languageHelper;
  public static String softwareVersion;
  public static boolean updateAvailable = false;

  public static void main(String[] args)
  {
    readVersionFromProperties();
    Logger.info("Start Solarreader " + softwareVersion);
    if (Files.exists(Paths.get(Config.CONFIG_FILE)))
    {
      Config.getInstance().readConfiguration();
    }
    Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    languageHelper = new LanguageHelper();
    ThreadHelper.startMainThreads();
    HttpServer server = new HttpServer();
    int port = 8080;
    server.startAtPort(port);
  }

  public static void readVersionFromProperties()
  {
    try
    {
      InputStream is = SolarMain.class.getClassLoader().getResourceAsStream("solarreader.properties");
      Properties properties = new Properties();
      properties.load(is);
      softwareVersion = properties.getProperty("software.version", "?");
    } catch (Exception e)
    {
      Logger.error("could not determine software version:", e.getMessage());
      softwareVersion = "?";
    }
  }

}
