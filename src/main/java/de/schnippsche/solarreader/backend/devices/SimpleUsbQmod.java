package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.Config;
import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import de.schnippsche.solarreader.backend.utils.*;
import org.tinylog.Logger;

import java.util.*;

/**
 * class for dealing with hidraw devices
 */
public class SimpleUsbQmod extends AbstractLockedDevice
{
  protected static final String NAK = "NAK";
  protected static final String ACK = "ACK";
  protected static final String QPI = "QPI";
  protected static final String KNOWN_PROTOCOL = "PI30";
  protected final UsbWrapper usbWrapper;
  protected final DayValueWrapper dayValueWrapper;
  protected final List<CachedQCommand> allCachedCommands;
  protected int sleepMilliseconds;

  public SimpleUsbQmod(ConfigDevice configDevice)
  {
    super(configDevice);
    usbWrapper = new UsbWrapper(configDevice);
    dayValueWrapper = new DayValueWrapper(configDevice);
    allCachedCommands = new ArrayList<>();
  }

  @Override protected void initialize()
  {
    specification = jsonTool.readSpecification(getConfigDevice().getDeviceSpecification());
    sleepMilliseconds = specification.getAdditionalParameterAsInteger(AdditionalParameter.SLEEP_MILLISECONDS, 0);
    List<DeviceField> deviceFields = specification.getDevicefields();
    HashSet<String> onceOnlyCommands = new HashSet<>();
    onceOnlyCommands.add("QMD"); // Device Model Inquiry
    onceOnlyCommands.add("QMN"); // Query model name
    onceOnlyCommands.add("QVFW"); // Inverter CPU Firmware version inquiry
    onceOnlyCommands.add("QVFW2"); // Inverter CPU Firmware version inquiry
    onceOnlyCommands.add("QID"); // device serial number inquiry
    onceOnlyCommands.add("QSID"); // device serial number inquiry
    // Group and build all QCommands
    List<String> commands = new ArrayList<>();
    Set<String> uniqueValues = new HashSet<>();
    for (DeviceField deviceField : specification.getDevicefields())
    {
      String command = deviceField.getCommand();
      if (uniqueValues.add(command))
      {
        commands.add(command);
      }
    }
    for (String cmd : commands)
    {
      List<DeviceField> fieldsForCommand = new ArrayList<>();
      for (DeviceField e : deviceFields)
      {
        if (cmd.equals(e.getCommand()))
        {
          fieldsForCommand.add(e);
        }
      }
      CachedQCommand cachedQCommand = new CachedQCommand(cmd, onceOnlyCommands.contains(cmd));
      cachedQCommand.setDeviceFields(fieldsForCommand);
      allCachedCommands.add(cachedQCommand);
    }
  }

  @Override protected void sendCheckedCommandsToDevice(List<ExpiringCommand> commands)
  {
    if (!usbWrapper.open())
    {
      return;
    }
    for (ExpiringCommand command : commands)
    {
      String result = usbWrapper.send(new QCommand(command.getCommand()));
      switch (result)
      {
        case NAK:
          Logger.error("command '{}' is unknown!", command);
          break;
        case ACK:
          Logger.info("command '{}' accepted!", command);
          break;
        default:
          Logger.info("unknown response '{}' from command '{}'", result, command);
          break;
      }
      Config.getInstance().removeExpiringCommand(command);
    }
    usbWrapper.close();
  }

  @Override public boolean checkConnection()
  {
    if (usbWrapper.open())
    {
      usbWrapper.close();
      return true;
    }
    return false;
  }

  @Override protected boolean readLockedDeviceValues()
  {
    if (!usbWrapper.open())
    {
      return false;
    }
    resultFields.clear();
    // check first protocol and if device is ready

    String result = usbWrapper.send(new QCommand(QPI));
    if (result == null)
    {
      Logger.error("device didn't answer, abort reading...");
      // reset all cached result fields
      for (CachedQCommand qCommand : allCachedCommands)
      {
        if (qCommand.isCacheable())
        {
          qCommand.setCachedResultFields(null);
        }
      }
      usbWrapper.close();
      return false;
    }
    if (!KNOWN_PROTOCOL.equals(result))
    {
      Logger.error("unknown device protocol '{}', must be '{}'", result, KNOWN_PROTOCOL);
      usbWrapper.close();
      return false;
    }
    Logger.debug("protocol '{}' found!", result);
    Logger.debug("iterate over {} commands", allCachedCommands.size());
    // Iterate over all commands, filter the appropriate DeviceFields and create ResultFields
    for (CachedQCommand qCommand : allCachedCommands)
    {
      List<ResultField> newFields;
      if (qCommand.isCached())
      {
        newFields = qCommand.getCachedResultFields();
        Logger.debug("return cached values for command {}:{}", qCommand.getCommand(), newFields);
      } else
      {
        newFields = getResultFields(qCommand);
        Logger.debug("get result fields for command {}:{}", qCommand.getCommand(), newFields);
      }
      resultFields.addAll(newFields);
    }

    usbWrapper.close();
    return true;
  }

  private List<ResultField> getResultFields(CachedQCommand qCommand)
  {
    numericHelper.sleep(sleepMilliseconds);
    String cmd = qCommand.getCommand();
    String result = usbWrapper.send(qCommand);
    if (result != null)
    {
      List<ResultField> newFields;
      if (NAK.equals(result))
      {
        Logger.warn("command '{}' returns '{}'", cmd, NAK);
        newFields = Collections.emptyList();
      } else
      {
        newFields = generateResultFields(qCommand.getDeviceFields(), result);
      }
      // cache NAK also!
      if (qCommand.isCacheable())
      {
        qCommand.setCachedResultFields(newFields);
      }
      return newFields;
    }
    return Collections.emptyList();
  }

  private List<ResultField> generateResultFields(List<DeviceField> fieldsForCommand, String result)
  {
    final List<ResultField> resultFieldList = new ArrayList<>();
    Logger.debug("generateResultFields from String '{}'", result);
    for (DeviceField deviceField : fieldsForCommand)
    {
      String value = numericHelper.getSafeSubstring(result, deviceField.getOffset(), deviceField.getCount()).trim();
      ResultField resultField = new ResultField(deviceField.getName(), ResultFieldStatus.VALID, value);
      Logger.debug(resultField);
      resultFieldList.add(resultField);
    }
    return resultFieldList;
  }

}
