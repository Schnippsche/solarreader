package de.schnippsche.solarreader.backend.connections;

import okhttp3.Response;

import java.util.Objects;

/**
 * The type Response result.
 */
public class ResponseResult
{
  private final int code;
  private final String body;
  private final String statusMessage;
  private final boolean isSuccessful;

  /**
   * Instantiates a new Response result.
   */
  public ResponseResult()
  {
    this.code = 404;
    this.body = "";
    this.statusMessage = "";
    this.isSuccessful = false;
  }

  /**
   * Instantiates a new Response result.
   *
   * @param response the response
   */
  public ResponseResult(Response response)
  {
    this.code = response.code();
    this.isSuccessful = response.isSuccessful();
    String tmpBody;
    try
    {
      tmpBody = Objects.requireNonNull(response.body()).string();
    } catch (Exception e)
    {
      tmpBody = "";
    }
    this.body = tmpBody;
    this.statusMessage = response.message();
  }

  /**
   * Gets code.
   *
   * @return the code
   */
  public int getCode()
  {
    return this.code;
  }

  /**
   * Gets body.
   *
   * @return the body
   */
  public String getBody()
  {
    return this.body;
  }

  public boolean isSuccessful()
  {
    return this.isSuccessful;
  }

  /**
   * Gets status message.
   *
   * @return the status message
   */
  public String getStatusMessage()
  {
    return this.statusMessage;
  }

  @Override public String toString()
  {
    return "ResponseResult{" + "code=" + this.code + ", statusMessage='" + this.statusMessage + '\'' + ", body='"
           + this.body + '\'' + '}';
  }

}
