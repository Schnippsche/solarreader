package de.schnippsche.solarreader.frontend;

import org.takes.facets.fork.FkRegex;
import org.takes.facets.fork.TkFork;
import org.takes.http.Exit;
import org.takes.http.FtBasic;
import org.takes.tk.TkClasspath;
import org.takes.tk.TkWithHeader;
import org.takes.tk.TkWithType;
import org.tinylog.Logger;

import java.io.IOException;

public final class HttpServer
{
  private static final String CACHE_ENABLED = "Cache-Control:max-age=31536000, immutable";
  private static final String CACHE_DISABLED = "Cache-Control:no-cache";
  private final TkFork tkFork;

  public HttpServer()
  {
    final TkClasspath classpath = new TkClasspath("/html");
    tkFork = new TkFork(new FkRegex("/css/.+", new TkWithHeader(new TkWithType(classpath, "text/css"), CACHE_ENABLED)), new FkRegex("/js/.+", new TkWithHeader(new TkWithType(classpath, "text/javascript"), CACHE_ENABLED)), new FkRegex("/img/.+jpg", new TkWithHeader(new TkWithType(classpath, "image/jpeg"), CACHE_ENABLED)), new FkRegex("/img/.+png", new TkWithHeader(new TkWithType(classpath, "image/png"), CACHE_ENABLED)), new FkRegex("/fonts/.+woff2", new TkWithHeader(new TkWithType(classpath, "application/font-woff2"), CACHE_ENABLED)), new FkRegex("/fonts/.+woff", new TkWithHeader(new TkWithType(classpath, "application/font-woff"), CACHE_ENABLED)), new FkRegex("/lang/table.+json", new TkWithType(classpath, "text/json")), new FkRegex("/ajax", new TkWithType(new Ajax(), "text/json")), new FkRegex("/.*", new TkWithHeader(new MainTkIndex(), CACHE_DISABLED)));
  }

  public void startAtPort(int port)
  {
    try
    {
      new FtBasic(tkFork, port).start(Exit.NEVER);
    } catch (IOException e)
    {
      Logger.error("HTTP Server couldn't start at port {} :", port, e.getMessage());
    }
  }

}
