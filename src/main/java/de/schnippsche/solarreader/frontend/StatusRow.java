package de.schnippsche.solarreader.frontend;

public class StatusRow
{
  private String icon;
  private String element;
  private String statustext;
  private String statusclass;
  private String activity;
  private String info;

  public StatusRow()
  {
    this.info = "";
  }

  public String getIcon()
  {
    return icon;
  }

  public void setIcon(String icon)
  {
    this.icon = icon;
  }

  public String getElement()
  {
    return element;
  }

  public void setElement(String element)
  {
    this.element = element;
  }

  public String getStatustext()
  {
    return statustext;
  }

  public void setStatustext(String statustext)
  {
    this.statustext = statustext;
  }

  public String getStatusclass()
  {
    return statusclass;
  }

  public void setStatusclass(String statusclass)
  {
    this.statusclass = statusclass;
  }

  public String getActivity()
  {
    return activity;
  }

  public void setActivity(String activity)
  {
    this.activity = activity;
  }

  public String getInfo()
  {
    return info;
  }

  public void setInfo(String info)
  {
    this.info = info;
  }

}
