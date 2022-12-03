package de.schnippsche.solarreader.backend.utils;

/**
 * Class for all Q Commands such as QPIGS, QMOD etc.
 */
public class QCommand
{
  private final String command;
  private final byte[] byteCommand;
  public QCommand(String command)
  {
    this.command = command;
    int ln = command.length();
    byteCommand = new byte[ln + 3];
    for (int i = 0; i < ln; i++)
    {
      byteCommand[i] = (byte) command.charAt(i);
    }
    int crc = new CheckSumHelper().getCrc16Ccitt(command.getBytes());
    byteCommand[ln++] = (byte) ((crc >> 8) & 0xFF);
    byteCommand[ln++] = (byte) (crc & 0xFF);
    byteCommand[ln] = '\r';
  }

  public String getCommand()
  {
    return command;
  }
  public byte[] getByteCommand()
  {
    return byteCommand;
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
    QCommand qCommand = (QCommand) o;
    return command.equals(qCommand.command);
  }
  @Override public int hashCode()
  {
    return command.hashCode();
  }
  @Override public String toString()
  {
    return "QCommand{" + "command='" + command + '\'' + ", byteCommand=" + new NumericHelper().byteArrayToHexString(byteCommand) + '}';
  }

}
