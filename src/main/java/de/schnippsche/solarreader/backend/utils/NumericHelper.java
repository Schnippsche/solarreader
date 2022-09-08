package de.schnippsche.solarreader.backend.utils;

import de.schnippsche.solarreader.backend.fields.FieldType;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.regex.Pattern;

public class NumericHelper
{
  private static final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

  public boolean isNumericValue(Object value)
  {
    if (value == null)
    {
      return false;
    }
    return pattern.matcher(value.toString()).matches();
  }

  public Integer getInteger(Object value)
  {
    if (value == null)
    {
      return null;
    }
    String v = value.toString().trim();
    if (v.isEmpty())
    {
      return null;
    }
    try
    {
      return Integer.parseInt(v);
    } catch (NumberFormatException exception)
    {
      return null;
    }
  }

  public BigDecimal getBigDecimal(String value)
  {
    if (value == null || value.isEmpty())
    {
      return BigDecimal.ZERO;
    }
    try
    {
      return new BigDecimal(value);
    } catch (NumberFormatException e)
    {
      Logger.error("no valid number:{}", value);
    }
    return BigDecimal.ZERO;
  }

  /**
   * convert a string value into an integer or gets the default value if value is null or not a
   * number
   *
   * @param value        a string or null
   * @param defaultValue the default value if the string is null or not a number
   * @return integer or default value
   */
  public int getInteger(Object value, int defaultValue)
  {
    Integer result = getInteger(value);
    return result == null ? defaultValue : result;
  }

  public Long getLong(Object value)
  {
    if (value == null)
    {
      return null;
    }
    String v = value.toString().trim();
    if (v.isEmpty())
    {
      return null;
    }

    try
    {
      return Long.parseLong(v);
    } catch (NumberFormatException exception)
    {
      return null;
    }
  }

  public String byteArrayToHexString(byte[] bytes)
  {
    if (bytes == null || bytes.length == 0)
    {
      return "";
    }
    StringBuilder builder = new StringBuilder(bytes.length * 2);
    char[] hexDigits = new char[2];
    for (byte b : bytes)
    {
      hexDigits[0] = Character.forDigit((b >> 4) & 0xF, 16);
      hexDigits[1] = Character.forDigit((b & 0xF), 16);
      builder.append(hexDigits);
    }
    return builder.toString();
  }

  /**
   * converts an integer into a binary 16 bit string example: value 23 returns "0000000000010111"
   *
   * @param value the int value
   * @return the binary presentation as string
   */
  public String getBinary16String(int value)
  {
    return String.format("%16s", Integer.toBinaryString(value)).replace(" ", "0");
  }

  /**
   * convert a byte array into the field type
   *
   * @param bytes the byte array
   * @param type  the field type
   * @return Object
   * @throws NumberFormatException if byte array can not converted in specific field type
   */
  public Object convertByteArray(byte[] bytes, FieldType type) throws NumberFormatException
  {

    switch (type)
    {
      case STRING:
        StringBuilder builder = new StringBuilder();
        for (byte aByte : bytes)
        {
          if (aByte >= 32)
          {
            builder.append((char) aByte);
          }
          if (aByte == 0)
          {
            break;
          }
        }
        return builder.toString().trim();
      case BINARY:
        return bytes;
      case U8:
        checkMinimumByteCount(bytes, 1);
        return BigDecimal.valueOf(0xFF & bytes[0]);
      case I8:
        checkMinimumByteCount(bytes, 1);
        return BigDecimal.valueOf(bytes[0]);
      case U16_BIG_ENDIAN:
        checkMinimumByteCount(bytes, 2);
        return BigDecimal.valueOf(shiftUnsigned(bytes[0], 8) | shiftUnsigned(bytes[1], 0));
      case U16_LITTLE_ENDIAN:
        checkMinimumByteCount(bytes, 2);
        return BigDecimal.valueOf(shiftUnsigned(bytes[1], 8) | shiftUnsigned(bytes[0], 0));
      case I16_BIG_ENDIAN:
        checkMinimumByteCount(bytes, 2);
        return BigDecimal.valueOf(shiftSigned(bytes[0], 8) | shiftUnsigned(bytes[1], 0));
      case I16_LITTLE_ENDIAN:
        checkMinimumByteCount(bytes, 2);
        return BigDecimal.valueOf(shiftSigned(bytes[1], 8) | shiftUnsigned(bytes[0], 0));
      case U32_BIG_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return BigDecimal.valueOf(shiftUnsigned(bytes[0], 24) | shiftUnsigned(bytes[1], 16) | shiftUnsigned(bytes[2], 8) | shiftUnsigned(bytes[3], 0));
      case U32_MIXED_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return BigDecimal.valueOf(shiftUnsigned(bytes[1], 24) | shiftUnsigned(bytes[0], 16) | shiftUnsigned(bytes[3], 8) | shiftUnsigned(bytes[2], 0));
      case U32_LITTLE_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return BigDecimal.valueOf(shiftUnsigned(bytes[3], 24) | shiftUnsigned(bytes[2], 16) | shiftUnsigned(bytes[1], 8) | shiftUnsigned(bytes[0], 0));
      case I32_BIG_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return BigDecimal.valueOf(shiftSigned(bytes[0], 24) | shiftUnsigned(bytes[1], 16) | shiftUnsigned(bytes[2], 8) | shiftUnsigned(bytes[3], 0));
      case I32_MIXED_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return BigDecimal.valueOf(shiftSigned(bytes[1], 24) | shiftUnsigned(bytes[0], 16) | shiftUnsigned(bytes[3], 8) | shiftUnsigned(bytes[2], 0));
      case I32_LITTLE_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return BigDecimal.valueOf(shiftSigned(bytes[3], 24) | shiftUnsigned(bytes[2], 16) | shiftUnsigned(bytes[1], 8) | shiftUnsigned(bytes[0], 0));
      case FLOAT_BIG_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return new BigDecimal(String.valueOf(ByteBuffer.wrap(bytes)
                                                       .order(ByteOrder.BIG_ENDIAN)
                                                       .getFloat()), MathContext.DECIMAL32);
      case FLOAT_LITTLE_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return new BigDecimal(String.valueOf(ByteBuffer.wrap(bytes)
                                                       .order(ByteOrder.LITTLE_ENDIAN)
                                                       .getFloat()), MathContext.DECIMAL32);
      case DOUBLE_BIG_ENDIAN:
        checkMinimumByteCount(bytes, 8);
        return new BigDecimal(String.valueOf(ByteBuffer.wrap(bytes)
                                                       .order(ByteOrder.BIG_ENDIAN)
                                                       .getDouble()), MathContext.DECIMAL64);
      case DOUBLE_LITTLE_ENDIAN:
        checkMinimumByteCount(bytes, 8);
        return new BigDecimal(String.valueOf(ByteBuffer.wrap(bytes)
                                                       .order(ByteOrder.LITTLE_ENDIAN)
                                                       .getDouble()), MathContext.DECIMAL64);
      default:
        Logger.warn("type '{}' is not allowed for byte array conversion!", type);
    }
    return BigDecimal.ZERO;
  }

  public byte[] convertHexToByteArray(String hex)
  {
    if (hex.length() % 2 > 0)
    {
      throw new NumberFormatException("Hexadecimal input string must have an even length.");
    }
    byte[] r = new byte[hex.length() / 2];
    for (int i = hex.length(); i > 0; i -= 2)
    {
      r[i / 2 - 1] = (byte) (Character.digit(hex.charAt(i - 1), 16) | (Character.digit(hex.charAt(i - 2), 16) << 4));
    }
    return r;
  }

  private void checkMinimumByteCount(byte[] bytes, int byteCount) throws NumberFormatException
  {
    if (bytes.length < byteCount)
    {
      throw new NumberFormatException(String.format("size of byte array (%d) is smaller than expected (%d)", bytes.length, byteCount));
    }
  }

  private long shiftSigned(byte byteValue, int length)
  {
    return ((long) byteValue) << length;
  }

  private long shiftUnsigned(byte byteValue, int length)
  {
    return (0xFFL & byteValue) << length;
  }

}
