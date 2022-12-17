package de.schnippsche.solarreader.backend.connections;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.backend.utils.QCommand;
import org.tinylog.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The type Serial connection.
 */
public class SimpleSerialConnection implements Connection<String, QCommand>
{
  private static final char CARRIAGE_RETURN = '\r';
  private final NumericHelper numericHelper;
  private final ConfigDevice configDevice;
  private SerialPort serialPort;

  public SimpleSerialConnection(ConfigDevice configDevice)
  {
    this.configDevice = configDevice;
    numericHelper = new NumericHelper();
  }

  public List<Pair> getAvailablePorts()
  {
    final SerialPort[] ports = SerialPort.getCommPorts();
    final List<Pair> comports = new ArrayList<>();
    Logger.info("get available ports...");
    for (final SerialPort port : ports)
    {
      comports.add(new Pair(port.getSystemPortName(), port.getSystemPortName() + " " + port.getDescriptivePortName()));
      Logger.info("port found: {}", port.getSystemPortName() + " " + port.getDescriptivePortName());
    }
    return comports;
  }

  public void addDataListener(SerialPortDataListener dataListener)
  {
    serialPort.addDataListener(dataListener);
  }

  public void removeDataListener()
  {
    serialPort.removeDataListener();
  }

  /**
   * Send bytes
   *
   * @param bytes the bytes
   */
  public void send(byte[] bytes)
  {
    Logger.debug("try to send {} bytes to serial port {}, hex bytes are {}", bytes.length, serialPort.getSystemPortName(), numericHelper.byteArrayToHexString(bytes));
    int result = serialPort.writeBytes(bytes, bytes.length);
    Logger.debug("sended {} bytes to serial port {}", result, serialPort.getSystemPortName());
  }

  /**
   * Send command string.
   *
   * @param string the string
   */
  public void send(String string)
  {
    Logger.debug("try to send command '{}' to serial port {}...", string.trim(), serialPort.getSystemPortName());
    byte[] bytes = string.getBytes(StandardCharsets.ISO_8859_1);
    int result = serialPort.writeBytes(bytes, bytes.length);
    Logger.debug("sended {} bytes to serial port {}", result, serialPort.getSystemPortName());
  }

  public String send(QCommand command)
  {
    Logger.info("try to send command {} to device ...", command.getCommand());
    byte[] bytes = command.getByteCommand();
    int bytesWritten = serialPort.writeBytes(bytes, bytes.length);
    if (bytesWritten <= 0)
    {
      Logger.error("could not write, reason = {}", serialPort.getLastErrorCode());
      return null;
    }
    String line = readStringUntilCR();
    Logger.debug("read line {}", line);
    if (line.length() > 2 && line.startsWith("("))
    {
      line = line.substring(1, line.length() - 2).trim(); // crc at the end
    }
    Logger.debug("normalized line {}", line);

    return line;
  }

  /**
   * Open.
   */
  public boolean open()
  {
    String port = configDevice.getParamOrDefault(ConfigDeviceField.COM_PORT, "");
    try
    {
      serialPort = SerialPort.getCommPort(port);
    } catch (SerialPortInvalidPortException e)
    {
      Logger.error("can't open serial port {} ", port);
      return false;
    }
    serialPort.setBaudRate(configDevice.getIntParamOrDefault(ConfigDeviceField.BAUDRATE, 9600));
    serialPort.setParity(configDevice.getIntParamOrDefault(ConfigDeviceField.PARITY, SerialPort.NO_PARITY)); // NO PARITY
    serialPort.setNumDataBits(configDevice.getIntParamOrDefault(ConfigDeviceField.DATABITS, 8)); // 8
    serialPort.setNumStopBits(configDevice.getIntParamOrDefault(ConfigDeviceField.STOPBITS, AbstractSerialConnection.ONE_STOP_BIT)); // 1
    serialPort.setFlowControl(configDevice.getIntParamOrDefault(ConfigDeviceField.FLOWCONTROLIN, SerialPort.FLOW_CONTROL_DISABLED));
    Logger.debug("open serial port {} with baud rate {}", serialPort.getSystemPortName(), serialPort.getBaudRate());
    boolean ok = serialPort.openPort(200);
    if (!ok)
    {
      Logger.error("can't open Port {} , error {}", serialPort.getSystemPortName(), serialPort.getLastErrorCode());
      return false;
    }
    serialPort.setComPortTimeouts(
      SerialPort.TIMEOUT_READ_SEMI_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 5000, 1000);
    return true;
  }

  /**
   * Read bytes byte [ ].
   *
   * @param maxByte the max byte
   * @return the byte [ ]
   */
  public byte[] readBytes(int maxByte)
  {
    Logger.debug("try to read {} bytes from {}", maxByte, serialPort.getSystemPortName());
    byte[] readBuffer = new byte[maxByte];
    int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
    Logger.debug("read from {} returns {} bytes {}", serialPort.getSystemPortName(), numRead,
      numRead > 0 ? numericHelper.byteArrayToHexString(readBuffer) : "");
    if (numRead >= 0)
    {
      return Arrays.copyOfRange(readBuffer, 0, numRead);
    }

    return new byte[0];
  }

  /**
   * Reads a line of text. A line is considered to be terminated by a carriage return ('\r') or by reaching the end-of-file (EOF).
   *
   * @return A String containing the contents of the line, not including line-termination characters,
   * or null if the end of the stream has been reached without reading any characters
   */
  public String readStringUntilCR()
  {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(serialPort.getInputStream(), StandardCharsets.ISO_8859_1)))
    {
      StringBuilder stringBuilder = new StringBuilder();
      int singleChar;
      while ((singleChar = reader.read()) != -1)
      {
        char character = (char) singleChar;
        if (character == CARRIAGE_RETURN)
        {
          break;
        }
        stringBuilder.append(character);
      }
      return stringBuilder.toString();
    } catch (IOException e)
    {
      Logger.error("couldn't read string from serial Port: {}", e.getMessage());
    }
    return null;
  }

  /**
   * reads a String with beginning LF and CR at the end
   *
   * @return String without CR and LF
   */
  public String readCrStringLf()
  {
    String result = readStringUntilCR();
    if (result == null)
    {
      return "";
    }
    return result.startsWith("\n") ? result.substring(1) : result;
  }

  /**
   * Close.
   */
  public void close()
  {
    if (serialPort != null)
    {
      Logger.debug("close port from {}", serialPort.getSystemPortName());
      serialPort.closePort();
    }
  }

}
