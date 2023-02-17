package de.schnippsche.solarreader.backend.utils;

import de.schnippsche.solarreader.backend.fields.FieldType;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.regex.Pattern;

/**
 * Helper class for some numeric and String functions
 */
public class NumericHelper
{
  private static final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

  /**
   * get a substring from a string in a safe way, pretending IndexOutOfBoundsException
   *
   * @param line       the complete String
   * @param beginIndex the beginning index, inclusive
   * @param length     the length of the string
   * @return the specified substring
   */
  public String getSafeSubstring(String line, int beginIndex, int length)
  {
    Logger.debug("getSafeSubstring with beginIndex {}, length {}", beginIndex, length);
    if (beginIndex >= line.length())
    {
      return "";
    }
    int maxLen = (beginIndex + length >= line.length()) ? line.length() - beginIndex : length;
    return line.substring(beginIndex, beginIndex + maxLen);
  }

  /**
   * test if this object value can be convert to a numeric value
   *
   * @param value the value to test
   * @return true if this matches the numeric value pattern
   */
  public boolean isNumericValue(Object value)
  {
    if (value == null)
    {
      return false;
    }
    return pattern.matcher(value.toString()).matches();
  }

  /**
   * get an Integer value
   *
   * @param value the object
   * @return Integer value
   */
  public Integer getInteger(Object value)
  {
    if (value == null)
    {
      return null;
    }
    if (value instanceof BigDecimal)
    {
      return ((BigDecimal) value).intValue();
    }
    String v = value.toString().trim();
    if (v.isEmpty())
    {
      return null;
    }
    try
    {
      return new BigDecimal(v).intValue();
    } catch (NumberFormatException exception)
    {
      Logger.error("no valid integer:{}", v);
      return null;
    }
  }

  /**
   * get a BigDecimal value of a string
   *
   * @param value the string
   * @return BigDecimal value
   */
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

  public BigDecimal getBigDecimal(Object value)
  {
    if (value instanceof BigDecimal)
    {
      return (BigDecimal) value;
    }
    if (value == null)
    {
      return BigDecimal.ZERO;
    }
    return getBigDecimal(String.valueOf(value));
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

  /**
   * get a Long value
   *
   * @param value the value
   * @return Long value
   */
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

  /**
   * convert a byte array to a hexadecimal string
   *
   * @param bytes the byte array
   * @return String hexadecimal
   */
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
    StringBuilder builder = new StringBuilder();
    switch (type)
    {
      case STRING:
        for (byte aByte : bytes)
        {
          if (aByte >= 32)
          {
            builder.append((char) aByte);
          } else if (aByte == 0)
          {
            break;
          }
        }
        return builder.toString().trim();
      case STRING_LITTLE_ENDIAN:
        for (int i = bytes.length - 1; i >= 0; i--)
        {
          if (bytes[i] >= 32)
          {
            builder.append((char) bytes[i]);
          } else if (bytes[i] == 0)
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
        return BigDecimal.valueOf(shiftUnsigned(bytes[0], 24) | shiftUnsigned(bytes[1], 16) | shiftUnsigned(bytes[2], 8)
                                  | shiftUnsigned(bytes[3], 0));
      case U32_BIG_ENDIAN_LOW_HIGH:
        checkMinimumByteCount(bytes, 4);
        return BigDecimal.valueOf(shiftUnsigned(bytes[2], 24) | shiftUnsigned(bytes[3], 16) | shiftUnsigned(bytes[0], 8)
                                  | shiftUnsigned(bytes[1], 0));

      case U32_MIXED_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return BigDecimal.valueOf(shiftUnsigned(bytes[1], 24) | shiftUnsigned(bytes[0], 16) | shiftUnsigned(bytes[3], 8)
                                  | shiftUnsigned(bytes[2], 0));
      case U32_LITTLE_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return BigDecimal.valueOf(shiftUnsigned(bytes[3], 24) | shiftUnsigned(bytes[2], 16) | shiftUnsigned(bytes[1], 8)
                                  | shiftUnsigned(bytes[0], 0));
      case I32_BIG_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return BigDecimal.valueOf(shiftSigned(bytes[0], 24) | shiftUnsigned(bytes[1], 16) | shiftUnsigned(bytes[2], 8)
                                  | shiftUnsigned(bytes[3], 0));
      case I32_MIXED_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return BigDecimal.valueOf(shiftSigned(bytes[1], 24) | shiftUnsigned(bytes[0], 16) | shiftUnsigned(bytes[3], 8)
                                  | shiftUnsigned(bytes[2], 0));
      case I32_LITTLE_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return BigDecimal.valueOf(shiftSigned(bytes[3], 24) | shiftUnsigned(bytes[2], 16) | shiftUnsigned(bytes[1], 8)
                                  | shiftUnsigned(bytes[0], 0));
      case FLOAT_BIG_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return new BigDecimal(String.valueOf(ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
                                                       .getFloat()), MathContext.DECIMAL32);
      case FLOAT_LITTLE_ENDIAN:
        checkMinimumByteCount(bytes, 4);
        return new BigDecimal(String.valueOf(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
                                                       .getFloat()), MathContext.DECIMAL32);
      case DOUBLE_BIG_ENDIAN:
        checkMinimumByteCount(bytes, 8);
        return new BigDecimal(String.valueOf(ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
                                                       .getDouble()), MathContext.DECIMAL64);
      case DOUBLE_LITTLE_ENDIAN:
        checkMinimumByteCount(bytes, 8);
        return new BigDecimal(String.valueOf(ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
                                                       .getDouble()), MathContext.DECIMAL64);
      case I64_BIG_ENDIAN:
        checkMinimumByteCount(bytes, 8);
        return BigDecimal.valueOf(shiftSigned(bytes[0], 56) | shiftUnsigned(bytes[1], 48) | shiftUnsigned(bytes[2], 40)
                                  | shiftUnsigned(bytes[3], 32) | shiftUnsigned(bytes[4], 24)
                                  | shiftUnsigned(bytes[5], 16) | shiftUnsigned(bytes[6], 8)
                                  | shiftUnsigned(bytes[7], 0));
      case U64_BIG_ENDIAN:
        checkMinimumByteCount(bytes, 8);
        return BigDecimal.valueOf(
          shiftUnsigned(bytes[0], 56) | shiftUnsigned(bytes[1], 48) | shiftUnsigned(bytes[2], 40)
          | shiftUnsigned(bytes[3], 32) | shiftUnsigned(bytes[4], 24) | shiftUnsigned(bytes[5], 16)
          | shiftUnsigned(bytes[6], 8) | shiftUnsigned(bytes[7], 0));
      case SCALEFACTOR_BIG_ENDIAN:
        checkMinimumByteCount(bytes, 2);
        int n = (int) (shiftSigned(bytes[0], 8) | shiftUnsigned(bytes[1], 0));
        return BigDecimal.TEN.pow(n, MathContext.DECIMAL64);
      default:
        Logger.warn("type '{}' is not allowed for byte array conversion!", type);
    }
    return BigDecimal.ZERO;
  }

  /**
   * convert a hexadecimal String into a byte array
   *
   * @param hex the hexadecimal string
   * @return byte array
   */
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

  /**
   * sleep milliseconds
   *
   * @param ms milliseconds to sleppe
   */
  public void sleep(int ms)
  {
    if (ms <= 0)
    {
      return;
    }
    try
    {
      Logger.debug("wait {} milliseconds..", ms);
      Thread.sleep(ms);
    } catch (InterruptedException e)
    {
      throw new RuntimeException(e);
    }

  }

  /**
   * checks if the size of a byte array is smaller than byteCount bytes
   *
   * @param bytes     the byte array
   * @param byteCount the minimum byte counter
   * @throws NumberFormatException if the array size is smaller than byteCount bytes
   */
  private void checkMinimumByteCount(byte[] bytes, int byteCount) throws NumberFormatException
  {
    if (bytes.length < byteCount)
    {
      throw new NumberFormatException(String.format("size of byte array (%d) is smaller than expected (%d)", bytes.length, byteCount));
    }
  }

  /**
   * shift a signed byteValue to the left
   *
   * @param byteValue the signed byte value
   * @param length    the shift length
   * @return the shifted long value
   */
  private long shiftSigned(byte byteValue, int length)
  {
    return ((long) byteValue) << length;
  }

  /**
   * shift an unsigned byteValue to the left
   *
   * @param byteValue the unsigned byte value
   * @param length    the shift length
   * @return the shifted long value
   */
  private long shiftUnsigned(byte byteValue, int length)
  {
    return (0xFFL & byteValue) << length;
  }

}
