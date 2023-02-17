package de.schnippsche.solarreader.backend.automation;

import java.util.Objects;

public class Command implements Comparable<Command>
{
  private final String deviceUuid;
  private final CommandType commandType;
  private final int index;
  private final CommandAction commandAction;
  private Integer seconds;

  public Command(String deviceUuid, CommandType commandType, int index, CommandAction commandAction)
  {
    this(deviceUuid, commandType, index, commandAction, null);
  }

  public Command(String deviceUuid, CommandType commandType, int index, CommandAction commandAction, Integer seconds)
  {
    this.deviceUuid = deviceUuid;
    this.commandType = commandType;
    this.index = index;
    this.commandAction = commandAction;
    this.seconds = seconds;
  }

  public String getDeviceUuid()
  {
    return deviceUuid;
  }

  public CommandType getCommandType()
  {
    return commandType;
  }

  public int getIndex()
  {
    return index;
  }

  public CommandAction getCommandAction()
  {
    return commandAction;
  }

  public Integer getSeconds()
  {
    return seconds;
  }

  public void setSeconds(Integer seconds)
  {
    this.seconds = seconds;
  }

  @Override public int compareTo(Command that)
  {
    String thisWhole =
      this.getDeviceUuid() + "|" + this.getCommandType() + "|" + this.getIndex() + "|" + this.getCommandAction();
    String thatWhole =
      that.getDeviceUuid() + "|" + that.getCommandType() + "|" + that.getIndex() + "|" + that.getCommandAction();
    return thisWhole.compareTo(thatWhole);
  }

  @Override public boolean equals(Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }
    Command command = (Command) o;
    return index == command.index && deviceUuid.equals(command.deviceUuid) && commandType == command.commandType
           && commandAction == command.commandAction;
  }

  @Override public int hashCode()
  {
    return Objects.hash(deviceUuid, commandType, index, commandAction);
  }

  @Override public String toString()
  {
    return "Command{" + "deviceUuid='" + deviceUuid + '\'' + ", commandType=" + commandType + ", index=" + index
           + ", commandAction=" + commandAction + '}';
  }

}
