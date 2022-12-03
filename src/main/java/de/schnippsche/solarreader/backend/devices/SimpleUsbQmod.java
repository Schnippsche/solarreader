package de.schnippsche.solarreader.backend.devices;

import de.schnippsche.solarreader.backend.configuration.ConfigDevice;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractLockedDevice;
import de.schnippsche.solarreader.backend.fields.DeviceField;
import de.schnippsche.solarreader.backend.fields.ResultField;
import de.schnippsche.solarreader.backend.fields.ResultFieldStatus;
import de.schnippsche.solarreader.backend.utils.UsbWrapper;
import de.schnippsche.solarreader.backend.utils.DayValueWrapper;
import de.schnippsche.solarreader.backend.utils.QCommand;
import org.tinylog.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * class for dealing with hidraw devices
 */
public class SimpleUsbQmod extends AbstractLockedDevice
{
  protected static final String NAK = "NAK";
  protected final UsbWrapper usbWrapper;
  protected final DayValueWrapper dayValueWrapper;
  private final List<QCommand> commands;
  protected List<DeviceField> deviceFields;

  public SimpleUsbQmod(ConfigDevice configDevice)
  {
    super(configDevice);
    usbWrapper = new UsbWrapper(configDevice);
    commands = new ArrayList<>();
    dayValueWrapper = new DayValueWrapper(configDevice);
  }

  @Override protected void initialize()
  {
    specification = jsonTool.readSpecification(getConfigDevice().getDeviceSpecification());
    deviceFields = specification.getDevicefields();
    // Group and build all QCommands
    specification.getDevicefields().stream().map(DeviceField::getCommand).distinct()
                 .forEach(cmd -> commands.add(new QCommand(cmd)));
  }

  @Override protected boolean readLockedDeviceValues()
  {
    if (!usbWrapper.open())
    {
      return false;
    }
    // Iterate over all commands, filter the appropriate DeviceFields and create ResultFields
    for (QCommand qCommand : commands)
    {
      String result = usbWrapper.send(qCommand);
      if (result != null)
      {
        if (NAK.equals(result))
        {
          Logger.warn("hidraw returns '{}' for command '{}'", NAK, qCommand.getCommand());
        } else
        {
          List<DeviceField> fieldsForCommand =
            deviceFields.stream().filter(e -> qCommand.getCommand().equals(e.getCommand()))
                        .collect(Collectors.toList());
          for (DeviceField deviceField : fieldsForCommand)
          {
            String value =
              numericHelper.getSafeSubstring(result, deviceField.getOffset(), deviceField.getCount()).trim();

            ResultField resultField = new ResultField(deviceField.getName(), ResultFieldStatus.VALID, value);
            Logger.debug(resultField);
            resultFields.add(resultField);
          }
        }
      }
    }
    usbWrapper.close();
    return true;
  }

}
