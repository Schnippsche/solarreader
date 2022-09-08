package de.schnippsche.solarreader.backend.pusher;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.connections.NetworkConnection;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;
import org.tinylog.Logger;

public class InfluxPusher extends AbstractPushBuffer<Request>
{

  public InfluxPusher()
  {
    super(Config.getInstance().getConfigGeneral().getInfluxPushSecondsTimeout());
  }

  @Override public PushResult push(PushValue<Request> pushValue)
  {
    Logger.debug("push influx message {}", pushValue);
    Call call = NetworkConnection.HTTPCLIENT.newCall(pushValue.getSource());
    try (Response response = call.execute())
    {
      Logger.debug("Response: Code={}, Message={} {}", response.code(), response.message(), (response.body() != null ? response.body()
                                                                                                                               .string() : ""));
      switch (response.code())
      {
        case 200:
        case 204:
          return PushResult.SUCCESFUL;
        case 400:
          Logger.error("400 Bad request");
          return PushResult.REMOVE;
        case 401:
          Logger.error("401 Unauthorized");
          return PushResult.REMOVE;
        case 404:
          Logger.error("404 Resource not found: {}", response.message());
          return PushResult.REMOVE;
        case 413:
          Logger.error("413 Request entity too large");
          return PushResult.REMOVE;
        case 422:
          Logger.error("422 Unprocessible entity");
          return PushResult.REMOVE;
        default:
          return PushResult.RETRY;
      }
    } catch (Exception e)
    {
      Logger.error("can't connect to {}:{}", pushValue.getSource().url(), e.getMessage());
    }
    return PushResult.RETRY;
  }

}
