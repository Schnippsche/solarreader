package de.schnippsche.solarreader.backend.utils;

import com.ghgande.j2mod.modbus.Modbus;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.ModbusIOException;
import com.ghgande.j2mod.modbus.facade.AbstractModbusMaster;
import com.ghgande.j2mod.modbus.facade.ModbusSerialMaster;
import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.net.AbstractSerialConnection;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.util.SerialParameters;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.configuration.ConfigDeviceField;
import de.schnippsche.solarreader.backend.fields.*;
import org.tinylog.Logger;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModbusWrapper
{
  private final AbstractModbusMaster modbusMaster;
  private final int unitId;
  private final String infoText;
  private final NumericHelper numericHelper;
  private int sleepMs;

  public ModbusWrapper(ConfigDevice configDevice)
  {
    unitId = configDevice.getIntParamOrDefault(ConfigDeviceField.DEVICE_ADDRESS, 1);
    numericHelper = new NumericHelper();
    sleepMs = 0;
    if (configDevice.isParamEnabled(ConfigDeviceField.HF2211_ENABLED))
    {
      String ip = configDevice.getParamOrDefault(ConfigDeviceField.HF2211_IP, "127.0.0.1");
      int port = configDevice.getIntParamOrDefault(ConfigDeviceField.HF2211_PORT, 502);
      modbusMaster = new ModbusTCPMaster(ip, port);
      infoText = String.format("url %s, port %s", ip, port);
    } else if (configDevice.containsField(ConfigDeviceField.COM_PORT)
               && !configDevice.getParam(ConfigDeviceField.COM_PORT).isEmpty())
    {
      SerialParameters serialParameters = createParameter(configDevice);
      Logger.debug(serialParameters);
      modbusMaster = new ModbusSerialMaster(serialParameters);
      infoText = String.format("portName %s", serialParameters.getPortName());
    } else if (configDevice.containsField(ConfigDeviceField.DEVICE_IP)
               && configDevice.containsField(ConfigDeviceField.DEVICE_PORT))
    {
      String ip = configDevice.getParam(ConfigDeviceField.DEVICE_IP);
      int port = configDevice.getIntParamOrDefault(ConfigDeviceField.DEVICE_PORT, 502);
      modbusMaster = new ModbusTCPMaster(ip, port);
      infoText = String.format("url %s, port %s", ip, port);
    } else
    {
      modbusMaster = null;
      infoText = "unkown connection";
      Logger.error("incorrect configuration, missing {} or {}", ConfigDeviceField.HF2211_IP, ConfigDeviceField.COM_PORT);
    }
  }

  public void setSleepMilliseconds(int sleepMilliseconds)
  {
    this.sleepMs = sleepMilliseconds;
  }

  public SerialParameters createParameter(ConfigDevice configDevice)
  {
    SerialParameters serialParameters = new SerialParameters();
    serialParameters.setBaudRate(configDevice.getIntParamOrDefault(ConfigDeviceField.BAUDRATE, 9600));
    serialParameters.setDatabits(configDevice.getIntParamOrDefault(ConfigDeviceField.DATABITS, 8)); // 8
    serialParameters.setParity(configDevice.getIntParamOrDefault(ConfigDeviceField.PARITY, AbstractSerialConnection.NO_PARITY)); // NO PARITY
    serialParameters.setStopbits(configDevice.getIntParamOrDefault(ConfigDeviceField.STOPBITS, AbstractSerialConnection.ONE_STOP_BIT)); // 1
    serialParameters.setEcho(configDevice.isParamEnabled((ConfigDeviceField.ECHO)));
    serialParameters.setOpenDelay(configDevice.getIntParamOrDefault(ConfigDeviceField.OPENDELAY, AbstractSerialConnection.OPEN_DELAY));
    serialParameters.setFlowControlIn(configDevice.getIntParamOrDefault(ConfigDeviceField.FLOWCONTROLIN, AbstractSerialConnection.FLOW_CONTROL_DISABLED));
    serialParameters.setFlowControlOut(configDevice.getIntParamOrDefault(ConfigDeviceField.FLOWCONTROLOUT, AbstractSerialConnection.FLOW_CONTROL_DISABLED));
    serialParameters.setEncoding(configDevice.getParamOrDefault(ConfigDeviceField.ENCODING, Modbus.SERIAL_ENCODING_RTU));
    serialParameters.setPortName(configDevice.getParamOrDefault(ConfigDeviceField.COM_PORT, "ttyUSB0"));
    return serialParameters;
  }

  public AbstractModbusMaster getModbusMaster()
  {
    return modbusMaster;
  }

  public String getInfoText()
  {
    return infoText;
  }

  public List<ResultField> readFields(List<DeviceField> deviceFieldList)
  {
    List<ResultField> resultFields = new ArrayList<>();
    int errorCounter = 0;
    int maxErrors = deviceFieldList.size() / 10 + 1;
    for (DeviceField deviceField : deviceFieldList)
    {
      ResultField resultField = readField(deviceField);
      resultFields.add(resultField);
      if (resultField.getStatus() == ResultFieldStatus.READERROR)
      {
        errorCounter++;
      }
      if (errorCounter > maxErrors)
      {
        Logger.error("Too many read errors {}, maximum error counter = {}, stop reading", errorCounter, maxErrors);
        break;
      }
    }
    return resultFields;
  }

  public ResultField readField(DeviceField deviceField)
  {
    byte[] result = null;
    try
    {
      InputRegister[] register = readRegister(deviceField);
      if (register == null || register.length == 0)
      {
        throw new ModbusIOException("register is empty");
      }

      result = convertRegisterToByteArray(register);
      Object value = numericHelper.convertByteArray(result, deviceField.getType());
      return deviceField.createResultField(value);
    } catch (NumberFormatException e)
    {
      Logger.warn("Can't convert bytes from field {} into type {}: {}", deviceField.getName(), deviceField.getType(), numericHelper.byteArrayToHexString(result));
      return new ResultField(deviceField, ResultFieldStatus.INVALIDNUMBER, null);
    } catch (Exception e)
    {
      Logger.error("Can't read device field {}: {}", deviceField.getName(), e.getMessage());
    }
    return new ResultField(deviceField, ResultFieldStatus.READERROR, null);
  }

  public List<ResultField> readFieldBlocks(List<DeviceFieldBlock> deviceFieldBlockList) throws ModbusException
  {
    List<ResultField> resultFields = new ArrayList<>();
    for (DeviceFieldBlock deviceFieldBlock : deviceFieldBlockList)
    {
      resultFields.addAll(readDeviceFieldBlock(deviceFieldBlock));
    }
    return resultFields;
  }

  public List<ResultField> readDeviceFieldBlock(DeviceFieldBlock deviceFieldBlock) throws ModbusException
  {
    Logger.debug(" readDeviceFieldBlock with {} devicefields", deviceFieldBlock.getOriginalDeviceFields().size());
    InputRegister[] register = readRegister(deviceFieldBlock.getBlockDeviceField());
    if (register == null || register.length == 0)
    {
      throw new ModbusException("registerblock is empty");
    }
    List<ResultField> resultFields = deviceFieldBlock.convertToResultFields(register, numericHelper);
    Logger.debug("end of readDevicefieldBlock with {} resultfields", resultFields.size());

    return resultFields;
  }

  /**
   * read all device fields from device and store the result in resultfield list
   *
   * @param deviceFields List of device fields
   * @return List of resultfields
   */
  public List<ResultField> readAllFields(List<DeviceField> deviceFields)
  {
    if (modbusMaster == null || deviceFields == null)
    {
      return Collections.emptyList();
    }
    List<ResultField> resultFields = new ArrayList<>(deviceFields.size());
    try
    {
      Logger.info("try to connect to {}", getInfoText());
      modbusMaster.connect();
      Logger.info("connected");

      resultFields.addAll(readFields(deviceFields));
      // log for debugging
      for (ResultField field : resultFields)
      {
        Logger.debug(field);
      }
    } catch (Exception e)
    {
      Logger.error(e);
      return Collections.emptyList();

    } finally
    {
      modbusMaster.disconnect();
      Logger.debug("disconnected from {}", getInfoText());
    }

    return resultFields;
  }

  /**
   * read all device field blocks from device and store the result in resultfield list
   *
   * @param deviceFieldBlocks List of device fields
   * @return List of resultfields
   */
  public List<ResultField> readAllBlocks(List<DeviceFieldBlock> deviceFieldBlocks)
  {
    if (modbusMaster == null)
    {
      return Collections.emptyList();
    }
    List<ResultField> resultFields;
    try
    {
      Logger.info("try to connect to {}", getInfoText());
      modbusMaster.connect();
      Logger.info("connected");
      resultFields = readFieldBlocks(deviceFieldBlocks);
      // Ausgeben
      for (ResultField field : resultFields)
      {
        Logger.debug(field);
      }
    } catch (ModbusException e)
    {
      Logger.error(e.getMessage());
      return Collections.emptyList();
    } catch (Exception e)
    {
      Logger.error(e);
      return Collections.emptyList();

    } finally
    {
      modbusMaster.disconnect();
      Logger.debug("disconnected from {}", getInfoText());
    }

    return resultFields;
  }

  public boolean checkConnection()
  {
    if (modbusMaster == null)
    {
      return false;
    }
    try
    {
      modbusMaster.connect();
      modbusMaster.disconnect();
      return true;
    } catch (Exception e)
    {
      Logger.error("can't connect to {}", getInfoText());
    }
    return false;
  }

  /**
   * convert an array of InputRegister into a string and trims all whitespaces und zero chars
   *
   * @param register array of InputRegister
   * @return trimmed string
   */
  public String registerToString(InputRegister[] register)
  {
    byte[] bytes = convertRegisterToByteArray(register);
    return String.valueOf(numericHelper.convertByteArray(bytes, FieldType.STRING));
  }

  /**
   * convert an array of InputRegister into a BigDecimal
   *
   * @param register  array of InputRegister
   * @param fieldType for the numeric value representation
   * @return BigDecimal
   */
  public BigDecimal registerToNumber(InputRegister[] register, FieldType fieldType)
  {
    byte[] bytes = convertRegisterToByteArray(register);
    try
    {
      Object object = numericHelper.convertByteArray(bytes, fieldType);
      if (object instanceof BigDecimal)
      {
        return (BigDecimal) object;
      }
    } catch (NumberFormatException e)
    {
      Logger.warn("Can't convert bytes from '{}' into type {}", numericHelper.byteArrayToHexString(bytes), fieldType);
    }
    return BigDecimal.ZERO;
  }

  public byte[] convertRegisterToByteArray(InputRegister[] inputRegisters)
  {
    byte[] result = new byte[inputRegisters.length * 2];
    for (int i = 0; i < inputRegisters.length; i++)
    {
      byte[] source = inputRegisters[i].toBytes();
      result[i * 2] = source[0];
      result[i * 2 + 1] = source[1];
    }
    return result;
  }

  public InputRegister[] readRegister(int functionCode, int offset, int count) throws ModbusException
  {
    numericHelper.sleep(sleepMs);
    Logger.debug("read register, function code {}, offset {}, count {}", functionCode, offset, count);
    if (functionCode == 3)
    {
      return modbusMaster.readMultipleRegisters(unitId, offset, count);
    }
    if (functionCode == 4)
    {
      return modbusMaster.readInputRegisters(unitId, offset, count);
    }
    throw new ModbusException("unsupported function code " + functionCode);
  }

  public InputRegister[] readRegister(DeviceField deviceField) throws ModbusException
  {
    return readRegister(deviceField.getRegister(), deviceField.getOffset(), deviceField.getCount());
  }

  public String readRegisterAsString(int functionCode, int offset, int count) throws ModbusException
  {
    return registerToString(readRegister(functionCode, offset, count));
  }

  public BigDecimal readRegisterAsNumber(int functionCode, int offset, int count, FieldType fieldType) throws ModbusException
  {
    return registerToNumber(readRegister(functionCode, offset, count), fieldType);
  }

}
