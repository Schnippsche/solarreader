package de.schnippsche.solarreader.backend.devices;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.facade.AbstractModbusMaster;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.FieldType;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.utils.ModbusWrapper;
import org.tinylog.Logger;

public class GoodweWR extends AbstractLockedDevice
{
  private ModbusWrapper modbusWrapper;
  private String modelShort;

  public GoodweWR(ConfigDevice configDevice)
  {
    super(configDevice);
  }

  @Override protected void initialize()
  {
    modbusWrapper = new ModbusWrapper(getConfigDevice());
  }

  // 52 = Goodwe Wechselrichter der Serien ES , EM und SBP (mit Batterie)
  // 67 = Goodwe Wechselrichter XS Serie
  @Override protected boolean readLockedDeviceValues()
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
      checkModel();

      resultFields.addAll(modbusWrapper.readFields(specification.getDevicefields()));
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

  private void checkModel() throws ModbusException
  {
    if (modelShort != null)
    {
      return;
    }
    Logger.debug("check Model");
    String model = modbusWrapper.readRegisterAsString(3, 528, 5);
    // XS Serie ?
    Logger.info("Goodwe Model {}", model);
    modelShort = model.substring(model.length() - 2, 2).toUpperCase();
    if ("XS".equals(modelShort))
    {
      specification = jsonTool.readSpecification("goodwe_wr_xs");
    } else
    {
      specification = jsonTool.readSpecification("goodwe_wr");
    }
  }

  @Override protected void correctValues()
  {
    ResultField errorBit = getValidResultField("Diag_Status");
    if (errorBit != null)
    {
      int errorValue = errorBit.getNumericValue().intValue();
      errorBit.setValue(numericHelper.getBinary16String(errorValue));
      errorBit.setType(FieldType.STRING);
    }
    // WattstundenGesamtHeute
    if ("XS".equals(modelShort))
    {
      setWattTotalResultField("WattstundenGesamtHeute");
    } else
    {
      ResultField bezug = getValidResultField("BezugHeute");
      ResultField einspeisung = getValidResultField("EinspeisungHeute");
      if (bezug != null && einspeisung != null)
      {
        setWattTotalToday(bezug.getNumericValue().subtract(einspeisung.getNumericValue()));
      }
    }
  }

  @Override public boolean checkConnection()
  {
    return modbusWrapper.checkConnection();
  }

}
