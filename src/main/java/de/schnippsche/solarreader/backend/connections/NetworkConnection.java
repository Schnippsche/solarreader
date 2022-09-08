package de.schnippsche.solarreader.backend.connections;

import de.schnippsche.solarreader.backend.utils.Pair;
import okhttp3.*;
import org.tinylog.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * The type Network connection.
 */
public class NetworkConnection
{
  // These are the default values
  public static final MediaType MEDIA_TYPE_STRING = MediaType.parse("text/plain");
  public static final OkHttpClient HTTPCLIENT = new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                                                                          .writeTimeout(6, TimeUnit.SECONDS)
                                                                          .readTimeout(4, TimeUnit.SECONDS)
                                                                          .build();
  private String user;
  private String password;

  /**
   * Sets authorization.
   *
   * @param user     the user
   * @param password the password
   */
  public void setAuthorization(String user, String password)
  {
    this.user = user;
    this.password = password;
  }

  /**
   * Send get response result.
   *
   * @param baseUrl the base url
   * @param params  the params
   * @return the response result
   */
  public ResponseResult sendGet(String baseUrl, Map<String, String> params)
  {
    HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(baseUrl)).newBuilder();
    if (params != null)
    {
      params.forEach(urlBuilder::addQueryParameter);
    }
    HttpUrl httpUrl = urlBuilder.build();
    return sendGet(httpUrl);
  }

  /**
   * Send get response result.
   *
   * @param httpUrl the http url
   * @return the response result
   */
  public ResponseResult sendGet(HttpUrl httpUrl)
  {
    Request.Builder requestBuilder = new Request.Builder().url(httpUrl);
    doAuthorization(requestBuilder);
    Request request = requestBuilder.build();
    return send(request);
  }

  /**
   * Send post response result.
   *
   * @param baseUrl    the base url
   * @param postValues the post values
   * @return the response result
   */
  public ResponseResult sendPost(String baseUrl, Map<String, String> postValues)
  {
    HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(baseUrl)).newBuilder().build();
    return sendPost(httpUrl, postValues);
  }

  public ResponseResult sendPost(String baseUrl, Map<String, String> postValues, String postBody)
  {
    HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse(baseUrl)).newBuilder().build();
    return sendPost(httpUrl, postValues);
  }

  /**
   * Send post response result.
   *
   * @param httpUrl    the http url
   * @param postValues the post values
   * @return the response result
   */
  public ResponseResult sendPost(HttpUrl httpUrl, Map<String, String> postValues)
  {
    // form parameters
    FormBody.Builder formBodyBuilder = new FormBody.Builder();
    if (postValues != null)
    {
      postValues.forEach(formBodyBuilder::add);
    }
    RequestBody formBody = formBodyBuilder.build();
    Request.Builder requestBuilder = new Request.Builder().url(httpUrl).addHeader("User-Agent", "Solarreader");
    doAuthorization(requestBuilder);
    Request request = requestBuilder.post(formBody).build();
    return send(request);
  }

  public void doAuthorization(Request.Builder requestBuilder)
  {
    if (this.user != null && !this.user.isEmpty() && this.password != null && !this.password.isEmpty())
    {
      Logger.debug("do Authorization with user {} and pass {}", this.user, this.password);
      requestBuilder.addHeader("Authorization", Credentials.basic(this.user, this.password));
    }
  }

  /**
   * Send response result.
   *
   * @param request the request
   * @return the response result
   */
  public ResponseResult send(Request request)
  {
    if (request == null)
    {
      Logger.error("No valid request");
      return null;
    }
    Logger.debug("send request: {} ", request);
    try (Response tmpResponse = HTTPCLIENT.newCall(request).execute())
    {
      if (!tmpResponse.isSuccessful())
      {
        Logger.error("unexpected code {}", tmpResponse.code());
      } else
      {
        Logger.debug("response succesful");
      }
      return new ResponseResult(tmpResponse);
    } catch (IOException e)
    {
      Logger.error(e);
    }
    return new ResponseResult();
  }

  public Pair testUrl(String testUrl)
  {
    Logger.debug("test url {}", testUrl);
    Request request = new Request.Builder().url(testUrl).build();
    try (Response response = NetworkConnection.HTTPCLIENT.newCall(request).execute())
    {
      Logger.debug("url test gets response code {} and message {}", response.code(), response.message());
      return new Pair("" + response.code(), response.message());

    } catch (IOException e)
    {
      Logger.debug("url test gets error {}", e.getMessage());
      return new Pair("500", e.getMessage());
    }
  }

}
