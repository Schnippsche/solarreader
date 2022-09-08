package de.schnippsche.solarreader.backend.utils;

public class CheckSumHelper
{

  public int getCrc16Ccitt(byte[] bytes)
  {
    return getCrc16Ccitt(bytes, 0, bytes.length);
  }

  public int getCrc16Ccitt(byte[] bytes, int offset, int length)
  {
    int max = Math.min(bytes.length, length);
    int remainder = 0; // initial value
    int polynomial = 0x1021; // 0001 0000 0010 0001  (0, 5, 12)
    int remMsb; // MSB bit of remainer i.e. current crc calue
    int bitvalue;
    for (int i = offset; i < max; i++)
    {
      byte b = bytes[i];
      for (int count = 0; count < 8; count++)
      { // for each bit from MSB to LSB
        bitvalue = ((b >>> (7 - count)) & 1); // one bit at a time from MSB side
        remMsb = ((remainder & 0x8000) >>> 15) & 1;
        remainder = remainder << 1; // be ready for next round of division
        // check if XOR of both data bit and remainder bit is true
        if ((bitvalue ^ remMsb) == 1)
        {
          // if yes, do XOR of CRC with Polynomial
          remainder = remainder ^ polynomial;
        }
      }
    }

    remainder = remainder & 0xFFFF; // truncate to 16 bits only
    return remainder;
  }

}
