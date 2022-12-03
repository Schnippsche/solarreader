package de.schnippsche.solarreader.backend.devices;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.AbstractModbusMaster;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.TableFieldType;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;
import de.schnippsche.solarreader.backend.utils.Specification;
import org.tinylog.Logger;

public class Solaredge extends AbstractLockedDevice
{
  private static final String SOLAREDGEMETER = "solaredgemeter";
  private ModbusWrapper modbusWrapper;
  private Integer offset;
  private Specification mpptSpecification;
  private Specification meterModel0Specification;
  private Specification meterModel1Specification;
  private Specification meterModel2Specification;

  public Solaredge(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void initialize()
  {
    modbusWrapper = new ModbusWrapper(getConfigDevice());
    offset = null;
  }
  //  Solaredge Sunspec
  //  @Override
  protected boolean readLockedDeviceValues()
  {

    AbstractModbusMaster modbusMaster = modbusWrapper.getModbusMaster();
    if (modbusMaster == null)
    {
      return false;
    }

    try
    {
      Logger.info("try to connect to {}", modbusWrapper.getInfoText());
      modbusMaster.connect();
      Logger.info("connected");
      if (checkOffsetOnFirstRun() == null)
      {
        return false;
      }

      resultFields.addAll(modbusWrapper.readFields(specification.getDevicefields()));
      readMppt();
      readMeterModels();
      // log for debugging
      for (ResultField field : resultFields)
      {
        Logger.debug(field);
      }
    } catch (ModbusException e)
    {
      Logger.error("can't read from {}", modbusWrapper.getInfoText());
      return false;
    } catch (Exception e)
    {
      Logger.error(e);
      return false;

    } finally
    {
      modbusMaster.disconnect();
      Logger.debug("disconnected from {}", modbusWrapper.getInfoText());
    }

    return true;
  }

  private Integer checkOffsetOnFirstRun() throws ModbusException
  {
    if (offset != null)
    {
      return offset;
    }
    String offset0 = modbusWrapper.readRegisterAsString(3, 0, 2);
    String offset1 = modbusWrapper.readRegisterAsString(3, 1, 2);
    if ("SunS".equals(offset0))
    {
      Logger.info("Sunspec ID found at offset 0");
      offset = 0;
    } else if ("SunS".equals(offset1))
    {
      Logger.info("Sunspec ID found at offset 1");
      offset = 1;
    } else
    {
      Logger.error("No Sunspec ID found");
      offset = null;
      return offset;
    }
    specification = jsonTool.readSpecification("solaredge");
    specification.getDevicefields().forEach(df -> df.setOffset(df.getOffset() + offset));
    // check if mppt is present or meter modules
    int start0 = 121 + offset;
    int value = modbusWrapper.readRegister(3, start0, 1)[0].getValue();
    if (value == 160)
    {
      mpptSpecification = jsonTool.readSpecification("solaredgemppt");
      mpptSpecification.getDevicefields().forEach(df -> df.setOffset(df.getOffset() + offset));
    } else if (value == 1) // Meter module 0 present
    {
      meterModel0Specification = jsonTool.readSpecification(SOLAREDGEMETER);
      changeMeterSpecification(meterModel0Specification, start0, "M1_");
    }
    int start1 = 295 + offset;
    value = modbusWrapper.readRegister(3, start1, 1)[0].getValue();
    if (value == 1) // Meter module 1 present
    {
      meterModel1Specification = jsonTool.readSpecification(SOLAREDGEMETER);
      changeMeterSpecification(meterModel1Specification, start1, "M1_");
    }
    int start2 = 469 + offset;
    value = modbusWrapper.readRegister(3, start2, 1)[0].getValue();
    if (value == 1) // Meter module 1 present
    {
      meterModel2Specification = jsonTool.readSpecification(SOLAREDGEMETER);
      changeMeterSpecification(meterModel2Specification, start2, "M2_");
    }
    // Check Meter modules if present
    return offset;
  }

  private void changeMeterSpecification(Specification oldSpecification, int startOffset, String prefix)
  {
    oldSpecification.getDevicefields().forEach(df ->
    {
      df.setOffset(df.getOffset() + startOffset);
      df.setName(prefix + df.getName());
    });
    oldSpecification.getDatabasefields()
                    .stream()
                    .filter(tf -> TableFieldType.RESULTFIELD == tf.getSourcetype())
                    .forEach(tf -> tf.setSourcevalue(prefix + tf.getSourcevalue()));
  }

  private void readMppt()
  {
    if (mpptSpecification != null)
    {
      resultFields.addAll(modbusWrapper.readFields(mpptSpecification.getDevicefields()));
    }
  }

  private void readMeterModels()
  {
    if (meterModel0Specification != null)
    {
      resultFields.addAll(modbusWrapper.readFields(meterModel0Specification.getDevicefields()));
    }
    if (meterModel1Specification != null)
    {
      resultFields.addAll(modbusWrapper.readFields(meterModel1Specification.getDevicefields()));
    }
    if (meterModel2Specification != null)
    {
      resultFields.addAll(modbusWrapper.readFields(meterModel2Specification.getDevicefields()));
    }
  }

}
