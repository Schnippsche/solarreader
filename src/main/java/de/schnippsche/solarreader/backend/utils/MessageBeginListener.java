package de.schnippsche.solarreader.backend.utils;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortEvent;
import com.fazecast.jSerialComm.SerialPortMessageListener;

public class MessageBeginListener implements SerialPortMessageListener
{
  private boolean ok = false;
  private String hexData = null;

  @Override public int getListeningEvents()
  {
    return SerialPort.LISTENING_EVENT_DATA_RECEIVED;
  }

  @Override public byte[] getMessageDelimiter()
  {
    return new byte[]{(byte) 0x1B, (byte) 0x1B, (byte) 0x1B, (byte) 0x1B, (byte) 0x01, (byte) 0x01, (byte) 0x01};
  }

  @Override public boolean delimiterIndicatesEndOfMessage()
  {
    return false;
  }

  @Override public void serialEvent(SerialPortEvent event)
  {
    byte[] msg = event.getReceivedData();
    hexData = new NumericHelper().byteArrayToHexString(msg);
    ok = msg.length > 200;
  }

  public boolean isOk()
  {
    return ok;
  }

  public String getHexData()
  {
    return hexData;
  }

}
