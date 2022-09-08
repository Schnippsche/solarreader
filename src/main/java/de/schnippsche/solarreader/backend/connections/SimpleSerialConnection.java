package de.schnippsche.solarreader.backend.connections;

import com.fazecast.jSerialComm.SerialPort;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import de.schnippsche.solarreader.backend.utils.Pair;
import org.tinylog.Logger;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The type Serial connection.
 */
public class SimpleSerialConnection
{
  private final NumericHelper numericHelper;

  public SimpleSerialConnection()
  {
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

  /**
   * Send bytes
   *
   * @param bytes the bytes
   */
  public void send(SerialPort serialPort, byte[] bytes)
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
  public void sendCommand(SerialPort serialPort, String string)
  {
    Logger.debug("try to send command '{}' to serial port {}...", string.trim(), serialPort.getSystemPortName());
    byte[] bytes = string.getBytes(StandardCharsets.ISO_8859_1);
    int result = serialPort.writeBytes(bytes, bytes.length);
    Logger.debug("sended {} bytes to serial port {}", result, serialPort.getSystemPortName());
  }

  /**
   * Open.
   */
  public void open(SerialPort serialPort)
  {
    Logger.debug("open serial port {}", serialPort.getSystemPortName());
    boolean ok = serialPort.openPort(200);
    if (!ok)
    {
      Logger.error("can't open Port {} , error {}", serialPort.getSystemPortName(), serialPort.getLastErrorCode());
    }
    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 3000, 1000);
  }

  /**
   * Read bytes byte [ ].
   *
   * @param maxByte the max byte
   * @return the byte [ ]
   */
  public byte[] readBytes(SerialPort serialPort, int maxByte)
  {
    Logger.debug("try to read {} bytes from {}", maxByte, serialPort.getSystemPortName());
    byte[] readBuffer = new byte[maxByte];
    int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
    Logger.debug("read from {} returns {} bytes {}", serialPort.getSystemPortName(), numRead, numRead > 0 ? numericHelper.byteArrayToHexString(readBuffer) : "");
    if (numRead >= 0)
    {
      return Arrays.copyOfRange(readBuffer, 0, numRead);
    }

    return new byte[0];
  }

  /**
   * Read bytes
   *
   * @return the byte [ ]
   */
  public byte[] readBytes(SerialPort serialPort)
  {
    return readBytes(serialPort, 1024);
  }

  /**
   * Read string .
   *
   * @return the string
   */
  public String readString(SerialPort serialPort)
  {
    return new String(readBytes(serialPort), StandardCharsets.ISO_8859_1).trim();
  }

  /**
   * Read string .
   *
   * @param maxLen the max len
   * @return the string
   */
  public String readString(SerialPort serialPort, int maxLen)
  {
    return new String(readBytes(serialPort, maxLen), StandardCharsets.ISO_8859_1);
  }
  /**
   * reads a String with beginning CR and LF at the end
   *
   * @param maxStringLength the maximum String length
   * @return String without CR and LF
   */
  public String readCrStringLf(SerialPort serialPort, int maxStringLength)
  {
    return new String(readBytes(serialPort, maxStringLength + 2), StandardCharsets.ISO_8859_1).trim();
  }
  /**
   * Read string .
   *
   * @param charset the charset
   * @return the string
   */
  public String readString(SerialPort serialPort, Charset charset)
  {
    return new String(readBytes(serialPort), charset);
  }

  /**
   * Close.
   */
  public void close(SerialPort serialPort)
  {
    if (serialPort != null)
    {
      Logger.debug("close port from {}", serialPort.getSystemPortName());
      serialPort.closePort();
    }
  }

}
