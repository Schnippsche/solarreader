package de.schnippsche.solarreader.frontend;

public class AjaxResult
{
  private final boolean success;
  private final String message;

  public AjaxResult(boolean success)
  {
    this(success, "");
  }

  public AjaxResult(boolean success, String message)
  {
    this.success = success;
    this.message = message;
  }

  public boolean isSuccess()
  {
    return success;
  }

  public String getMessage()
  {
    return message;
  }

}
