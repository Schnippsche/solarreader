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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * The type Serial connection.
 */
public class SimpleSerialConnection implements Connection<String, QCommand>
{
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
    String line = readString();
    Logger.debug("read line {}", line);
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
    serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 3000, 1000);
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
   * Read bytes
   *
   * @return the byte [ ]
   */
  public byte[] readBytes()
  {
    return readBytes(1024);
  }

  /**
   * Read string .
   *
   * @return the string
   */
  public String readString()
  {
    return new String(readBytes(), StandardCharsets.ISO_8859_1).trim();
  }

  /**
   * reads a String with beginning CR and LF at the end
   *
   * @param maxStringLength the maximum String length
   * @return String without CR and LF
   */
  public String readCrStringLf(int maxStringLength)
  {
    return new String(readBytes(maxStringLength + 2), StandardCharsets.ISO_8859_1).trim();
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
