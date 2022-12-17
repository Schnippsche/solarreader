package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import de.schnippsche.solarreader.backend.utils.CachedQCommand;
import de.schnippsche.solarreader.backend.utils.DayValueWrapper;
import de.schnippsche.solarreader.backend.utils.QCommand;
import de.schnippsche.solarreader.backend.utils.UsbWrapper;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * class for dealing with hidraw devices
 */
public class SimpleUsbQmod extends AbstractLockedDevice
{
  protected static final String NAK = "NAK";
  protected static final String QPI = "QPI";
  protected static final String KNOWN_PROTOCOL = "PI30";
  protected final UsbWrapper usbWrapper;
  protected final DayValueWrapper dayValueWrapper;
  protected final List<CachedQCommand> allCachedCommands;
  private boolean checkProtocol;

  public SimpleUsbQmod(ConfigDevice configDevice)
  {
    super(configDevice);
    usbWrapper = new UsbWrapper(configDevice);
    dayValueWrapper = new DayValueWrapper(configDevice);
    allCachedCommands = new ArrayList<>();
  }

  @Override protected void initialize()
  {
    checkProtocol = true;
    specification = jsonTool.readSpecification(getConfigDevice().getDeviceSpecification());
    List<DeviceField> deviceFields = specification.getDevicefields();
    HashSet<String> onceOnlyCommands = new HashSet<>();
    onceOnlyCommands.add("QMD"); // Device Model Inquiry
    onceOnlyCommands.add("QMN"); // Query model name
    onceOnlyCommands.add("QVFW"); // Inverter CPU Firmware version inquiry
    onceOnlyCommands.add("QVFW2"); // Inverter CPU Firmware version inquiry
    onceOnlyCommands.add("QID"); // device serial number inquiry
    onceOnlyCommands.add("QSID"); // device serial number inquiry
    // Group and build all QCommands
    List<String> commands =
      specification.getDevicefields().stream().map(DeviceField::getCommand).distinct().collect(Collectors.toList());
    for (String cmd : commands)
    {
      List<DeviceField> fieldsForCommand =
        deviceFields.stream().filter(e -> cmd.equals(e.getCommand())).collect(Collectors.toList());
      CachedQCommand cachedQCommand = new CachedQCommand(cmd, onceOnlyCommands.contains(cmd));
      cachedQCommand.setDeviceFields(fieldsForCommand);
      allCachedCommands.add(cachedQCommand);
    }
  }

  @Override protected boolean readLockedDeviceValues()
  {
    if (!usbWrapper.open())
    {
      return false;
    }
    resultFields.clear();
    if (checkProtocol)
    {
      String result = usbWrapper.send(new QCommand(QPI));
      if (!KNOWN_PROTOCOL.equals(result))
      {
        Logger.error("unknown Device Protocol '{}', must be '{}'", result, KNOWN_PROTOCOL);
        usbWrapper.close();
        return false;
      }
      Logger.debug("Protocol '{}' found!", result);
      checkProtocol = false;
    }
    Logger.debug("iterate over {} commands", allCachedCommands.size());
    // Iterate over all commands, filter the appropriate DeviceFields and create ResultFields
    for (CachedQCommand qCommand : allCachedCommands)
    {
      if (qCommand.isCached())
      {
        Logger.debug("return cached values for command {}", qCommand.getCommand());
        resultFields.addAll(qCommand.getCachedResultFields());
      } else
      {
        Logger.debug("get resultfields for command {}", qCommand.getCommand());
        resultFields.addAll(getResultFields(qCommand));
      }
    }
    usbWrapper.close();
    return true;
  }

  private List<ResultField> getResultFields(CachedQCommand qCommand)
  {
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
