package de.schnippsche.solarreader.backend.fields;

public enum FieldType
{
  STRING, // String with any chars
  STRING_LITTLE_ENDIAN,
  NUMBER, // any numeric value
  BINARY, // byte array
  U8, // Unsigned 8 bit
  I8, // Signed 8 bit
  U16_BIG_ENDIAN, // Unsigned 16 bit, MSB 0 BIG ENDIAN
  I16_BIG_ENDIAN, // Signed 16 bit, MSB 0  BIG ENDIAN
  U16_LITTLE_ENDIAN, // Unsigned 16 bit, MSB 0 LITTLE ENDIAN
  I16_LITTLE_ENDIAN, // Signed 16 bit, MSB = 0 LITTLE ENDIAN
  U32_BIG_ENDIAN, // Unsigned 32 bit, MSB 0, LSB = 3 BIG ENDIAN 1 2 3 4
  I32_BIG_ENDIAN, // Signed 32 bit, MSB 0, LSB = 3   BIG ENDIAN 1 2 3 4
  U32_MIXED_ENDIAN, // Unsigned 32 bit, MSB 0, LSB = 3 MIXED ENDIAN 2 1 4 3
  I32_MIXED_ENDIAN, // Signed 32 bit, MSB 0, LSB = 3   MIXED ENDIAN 2 1 4 3
  U32_LITTLE_ENDIAN, // Unsigned 32 bit, MSB = 0, LSB = 3 LITTLE ENDIAN 4 3 2 1
  I32_LITTLE_ENDIAN, // Signed 32 bit, MSB 0, LSB = 3  LITTLE ENDIAN  4 3 2 1
  FLOAT_BIG_ENDIAN, // Float 32 IEEE754 Big Endian
  FLOAT_LITTLE_ENDIAN, // Float 32 IEEE754 Little Endian
  DOUBLE_BIG_ENDIAN, // Float 64 IEEE754 Big Endian
  DOUBLE_LITTLE_ENDIAN, // Float 64 IEEE754 Little Endian
  SCALEFACTOR_BIG_ENDIAN // like I16_BIG_ENDIAN but automatically calculated from 10 ^ value
}
