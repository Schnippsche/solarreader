package de.schnippsche.solarreader.frontend;

import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.*;
import de.schnippsche.solarreader.backend.connections.SimpleHidrawConnection;
import de.schnippsche.solarreader.backend.connections.SimpleSerialConnection;
import de.schnippsche.solarreader.backend.devices.abstracts.AbstractDevice;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;
import org.tinylog.Logger;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DeviceSetup
{
  private static final String COMPORTALTERNATIV = "comportalternativ";
  private static final String CONNECTION_ERROR = "{devicesetup.connection.error}";
  private static final String DEVICE_IP = "device_ip";
  private static final String DEVICE_PORT = "device_port";
  private static final String HF2211PORT = "hf2211port";
  private static final String HF2211IP = "hf2211ip";
  private static final String EMPTY = "";

  private static ConfigDevice currentDevice;
  private final Map<String, String> formValues;
  private final NumericHelper numericHelper;
  private final Map<String, String> devicemaps;
  private final DialogHelper dialogHelper;

  public DeviceSetup(Map<String, String> formValues)
  {
    this.formValues = formValues;
    numericHelper = new NumericHelper();
    devicemaps = new HashMap<>();
    dialogHelper = new DialogHelper();
  }

  public AjaxResult getAjaxCode(String action, String page)
  {
    switch (action)
    {
      case "checkdevicestep":
        return checkStep(page);
      case "searchdeviceprops":
        break;
      case "reloadcomports":
        return reloadComports();
      case "reloadhidraws":
        return reloadHidraws();
      case "testconnection":
        return testConnection();
      default:
        return null;
    }
    return null;
  }

  public synchronized String getModalCode()
  {
    String step = formValues.getOrDefault("step", EMPTY);
    int page = numericHelper.getInteger(formValues.getOrDefault("page", "0"));
    switch (step)
    {
      case "newdevice":
        currentDevice = new ConfigDevice();
        return showDeviceStep(1);
      case "savedevice":
        return saveDevice(page);
      case "showdevice":
        return showDeviceStep(page);
      case "editdevice":
        currentDevice = new ConfigDevice(Config.getInstance().getConfigDeviceFromUuid(formValues.getOrDefault("id", "0")));
        return showDeviceStep(1);
      case "confirmdeletedevice":
        return confirmDelete();
      case "deletedevice":
        Config.getInstance().removeDevice(currentDevice);
        return EMPTY;
      default:
        return EMPTY;
    }
  }

  private String saveDevice(int page)
  {
    switch (page)
    {
      case 1:
        return saveStep1();
      case 2:
        return saveStep2();
      case 3:
        return saveStep3();
      case 4:
        return saveStep4();
      default:
        return EMPTY;
    }
  }

  private String showDeviceStep(int page)
  {
    switch (page)
    {
      case 1:
        return showStep1();
      case 2:
        return showStep2();
      case 3:
        return showStep3();
      case 4:
        return showStep4();
      default:
        return EMPTY;
    }
  }

  private String confirmDelete()
  {
    Map<String, String> infomap = new HashMap<>();
    infomap.put("[description]", currentDevice.getDescription());
    infomap.put("[devicename]", currentDevice.getDeviceName());
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "confirmdeletedevicemodal.tpl").getHtmlCode(infomap);
  }

  private String showStep1()
  {
    String deviceOptions = Config.getInstance().getAppInfo().getDeviceOptionList().getOptions(currentDevice.getDeviceInfoId());
    devicemaps.put("[deviceoptions]", deviceOptions);
    devicemaps.put("[disabledeviceselection]", currentDevice.getDeviceInfoId().isEmpty() ? "" : "disabled");
    devicemaps.put("[description]", currentDevice.getDescription());
    devicemaps.put("[enabledelete]", currentDevice.getDescription().isEmpty() ? "d-none" : EMPTY);
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "devicemodal1.tpl").getHtmlCode(devicemaps);
  }

  private String showStep2()
  {
    DeviceInfo deviceInfo = Config.getInstance().getAppInfo().getDeviceInfo(currentDevice.getDeviceInfoId());
    final HashSet<DeviceInfoField> deviceInfoFields = deviceInfo.getPrompts();
    // If new device, copy all standard defaults
    if (currentDevice.getParams().isEmpty())
    {
      currentDevice.setParams(deviceInfo.getDefaults());
    }
    String checked = currentDevice.getParamOrDefault(ConfigDeviceField.HF2211_ENABLED, EMPTY).equalsIgnoreCase("true") ? "checked" : EMPTY;
    devicemaps.put("[hf2211enabled]", checked);
    devicemaps.put("[HF2211IP]", currentDevice.getParamOrDefault(ConfigDeviceField.HF2211_IP, EMPTY));
    devicemaps.put("[HF2211PORT]", currentDevice.getParamOrDefault(ConfigDeviceField.HF2211_PORT, EMPTY));
    devicemaps.put("[DEVICEIP]", currentDevice.getParamOrDefault(ConfigDeviceField.DEVICE_IP, EMPTY));
    devicemaps.put("[DEVICEPORT]", currentDevice.getParamOrDefault(ConfigDeviceField.DEVICE_PORT, EMPTY));
    devicemaps.put("[DEVICEADDRESS]", currentDevice.getParamOrDefault(ConfigDeviceField.DEVICE_ADDRESS, EMPTY));
    devicemaps.put("[BAUDRATE]", currentDevice.getParamOrDefault(ConfigDeviceField.BAUDRATE, EMPTY));
    devicemaps.put("[SERIALNUMBER]", currentDevice.getParamOrDefault(ConfigDeviceField.SERIALNUMBER, EMPTY));
    devicemaps.put("[COMPORTALTERNATIV]", currentDevice.getParamOrDefault(ConfigDeviceField.COM_PORT, EMPTY));
    devicemaps.put("[USER]", currentDevice.getParamOrDefault(ConfigDeviceField.OPTIONAL_USER, EMPTY));
    devicemaps.put("[PASSWORD]", currentDevice.getParamOrDefault(ConfigDeviceField.OPTIONAL_PASSWORD, EMPTY));
    devicemaps.put("[comportoptions]", getAvailableUsbPorts());
    String divaddress = (deviceInfoFields.contains(DeviceInfoField.ADDRESS)) ? new HtmlElement(SolarMain.TEMPLATES_PATH + "divaddress.tpl").getHtmlCode(devicemaps) : EMPTY;
    String divbaudrate = (deviceInfoFields.contains(DeviceInfoField.BAUDRATE)) ? new HtmlElement(SolarMain.TEMPLATES_PATH + "divbaudrate.tpl").getHtmlCode(devicemaps) : EMPTY;
    String divhf2211 = (deviceInfoFields.contains(DeviceInfoField.HF2211)) ? new HtmlElement(SolarMain.TEMPLATES_PATH + "divhf2211.tpl").getHtmlCode(devicemaps) : EMPTY;
    String divlan = (deviceInfoFields.contains(DeviceInfoField.LAN) || deviceInfoFields.contains(DeviceInfoField.LAN_MQTT) || deviceInfoFields.contains(DeviceInfoField.LAN_MODBUS)) ? new HtmlElement(SolarMain.TEMPLATES_PATH + "divlan.tpl").getHtmlCode(devicemaps) : EMPTY;
    String divserial = (deviceInfoFields.contains(DeviceInfoField.SERIALNUMBER)) ? new HtmlElement(SolarMain.TEMPLATES_PATH + "divserial.tpl").getHtmlCode(devicemaps) : EMPTY;
    String divusb = (deviceInfoFields.contains(DeviceInfoField.SERIELL) || deviceInfoFields.contains(DeviceInfoField.HIDRAW)) ? new HtmlElement(SolarMain.TEMPLATES_PATH + "divusb.tpl").getHtmlCode(devicemaps) : EMPTY;
    String divauthentication = (deviceInfoFields.contains(DeviceInfoField.AUTHENTICATION)) ? new HtmlElement(SolarMain.TEMPLATES_PATH + "divauthentication.tpl").getHtmlCode(devicemaps) : EMPTY;
    devicemaps.put("[divaddress]", divaddress);
    devicemaps.put("[divbaudrate]", divbaudrate);
    devicemaps.put("[divhf2211]", divhf2211);
    devicemaps.put("[divlan]", divlan);
    devicemaps.put("[divserial]", divserial);
    devicemaps.put("[divusb]", divusb);
    devicemaps.put("[divauthentication]", divauthentication);
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "devicemodal2.tpl").getHtmlCode(devicemaps);
  }

  private String showStep3()
  {

    dialogHelper.setActivityValues(devicemaps, currentDevice.getActivity());
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "devicemodal3.tpl").getHtmlCode(devicemaps);
  }

  private String showStep4()
  {
    dialogHelper.setDataExporter(devicemaps, currentDevice.getConfigExport());
    return new HtmlElement(SolarMain.TEMPLATES_PATH + "devicemodal4.tpl").getHtmlCode(devicemaps);
  }

  private String saveStep1()
  {
    if (formValues.containsKey("deviceselect"))
    {
      currentDevice.setDeviceInfoId(formValues.getOrDefault("deviceselect", EMPTY));
      DeviceInfo deviceInfo = Config.getInstance().getAppInfo().getDeviceInfo(currentDevice.getDeviceInfoId());
      currentDevice.setDeviceName(deviceInfo.getDeviceName());
      currentDevice.setDeviceClass(deviceInfo.getDeviceClass());
      currentDevice.setDeviceSpecification((deviceInfo.getDeviceSpecification()));
    }
    currentDevice.setDescription(formValues.getOrDefault("description", EMPTY));
    return showStep2();
  }

  private String saveStep2()
  {
    currentDevice.setParam(ConfigDeviceField.SERIALNUMBER, formValues.get("serial"));
    currentDevice.setParam(ConfigDeviceField.DEVICE_ADDRESS, formValues.get("device_address"));
    currentDevice.setParam(ConfigDeviceField.DEVICE_IP, formValues.get(DEVICE_IP));
    currentDevice.setParam(ConfigDeviceField.DEVICE_PORT, formValues.get(DEVICE_PORT));
    currentDevice.setParam(ConfigDeviceField.HF2211_ENABLED, Boolean.toString(formValues.containsKey("usehf2211")));
    currentDevice.setParam(ConfigDeviceField.HF2211_IP, formValues.get(HF2211IP));
    currentDevice.setParam(ConfigDeviceField.HF2211_PORT, formValues.get(HF2211PORT));
    currentDevice.setParam(ConfigDeviceField.OPTIONAL_USER, formValues.get("user"));
    currentDevice.setParam(ConfigDeviceField.OPTIONAL_PASSWORD, formValues.get("password"));
    currentDevice.setParam(ConfigDeviceField.BAUDRATE, formValues.get("baudrate"));
    if (!formValues.getOrDefault(COMPORTALTERNATIV, EMPTY).isEmpty())
    {
      currentDevice.setParam(ConfigDeviceField.COM_PORT, formValues.get(COMPORTALTERNATIV));
    }
    else
    {
      currentDevice.setParam(ConfigDeviceField.COM_PORT, formValues.get("com_port"));
    }

    return showStep3();
  }

  private String saveStep3()
  {
    dialogHelper.getActivityFromForm(formValues);
    currentDevice.setActivity(dialogHelper.getActivityFromForm(formValues));
    return showStep4();
  }

  private String saveStep4()
  {
    currentDevice.setConfigExport(dialogHelper.getDataExporterFromForm(formValues));
    Config.getInstance().addOrReplaceDevice(currentDevice);
    return EMPTY;
  }

  private AjaxResult checkStep(String page)
  {

    switch (page)
    {
      case "1":
        final String newName = formValues.getOrDefault("description", EMPTY);
        long present = 0L;
        for (ConfigDevice cd : Config.getInstance().getConfigDevices())
        {
          if (cd.getDescription().equalsIgnoreCase(newName) && !cd.getUuid().equals(currentDevice.getUuid()))
          {
            present++;
          }
        }
        if (present > 0)
        {
          return new AjaxResult(false, SolarMain.languageHelper.replacePlaceholder("{devicesetup.name.exists}"));
        }
        break;
      case "2":
        if (mustCheckConnection())
        {
          return testConnection();
        }
        else
        {
          saveStep2();
          return new AjaxResult(true);
        }
      case "3":
        LocalTime from = LocalTime.parse(formValues.get("activityfrom"));
        LocalTime to = LocalTime.parse(formValues.get("activityto"));
        if (!to.isAfter(from))
        {
          return new AjaxResult(false, SolarMain.languageHelper.replacePlaceholder("{devicesetup.timestamp.error}"));
        }
        break;
      default:
        return new AjaxResult(true);
    }

    return new AjaxResult(true);
  }

  private boolean mustCheckConnection()
  {
    boolean changed = isParamDifferent(ConfigDeviceField.DEVICE_IP, DEVICE_IP);
    changed |= isParamDifferent(ConfigDeviceField.DEVICE_PORT, DEVICE_PORT);
    changed |= !currentDevice.getParamOrDefault(ConfigDeviceField.HF2211_ENABLED, EMPTY).equals(Boolean.toString(formValues.containsKey("usehf2211")));
    changed |= isParamDifferent(ConfigDeviceField.HF2211_IP, HF2211IP);
    changed |= isParamDifferent(ConfigDeviceField.HF2211_PORT, HF2211PORT);
    changed |= isParamDifferent(ConfigDeviceField.OPTIONAL_USER, "user");
    changed |= isParamDifferent(ConfigDeviceField.OPTIONAL_PASSWORD, "password");
    changed |= isParamDifferent(ConfigDeviceField.BAUDRATE, "baudrate");
    if (!formValues.getOrDefault(COMPORTALTERNATIV, EMPTY).isEmpty())
    {
      changed |= isParamDifferent(ConfigDeviceField.COM_PORT, COMPORTALTERNATIV);
    }
    else
    {
      changed |= isParamDifferent(ConfigDeviceField.COM_PORT, "com_port");
    }
    Logger.debug("must check connection");
    return changed;
  }

  private boolean isParamDifferent(ConfigDeviceField currentDeviceParam, String formValueParam)
  {
    return !currentDevice.getParamOrDefault(currentDeviceParam, EMPTY).equals(formValues.getOrDefault(formValueParam, EMPTY));
  }

  private AjaxResult reloadComports()
  {
    return new AjaxResult(true, getAvailableUsbPorts());
  }

  private String getAvailableUsbPorts()
  {
    Logger.debug("get available usb ports...");
    Map<String, List<Pair>> comports = new HashMap<>();
    DeviceInfo deviceInfo = Config.getInstance().getAppInfo().getDeviceInfo(currentDevice.getDeviceInfoId());
    final HashSet<DeviceInfoField> deviceInfoFields = deviceInfo.getPrompts();
    String chosen1 = null;
    String chosen2 = null;
    if (deviceInfoFields.contains(DeviceInfoField.COMPORT))
    {
      List<Pair> ports = new SimpleSerialConnection(currentDevice).getAvailablePorts();
      if (!ports.isEmpty())
      {
        comports.put("USB", ports);
      }
      chosen1 = currentDevice.getParam(ConfigDeviceField.COM_PORT);
    }
    if (deviceInfoFields.contains(DeviceInfoField.HIDRAW))
    {
      List<Pair> ports = new SimpleHidrawConnection(currentDevice).getAvailableHidraws();
      if (!ports.isEmpty())
      {
        comports.put("HIDRAW", new SimpleHidrawConnection(currentDevice).getAvailableHidraws());
      }
      chosen2 = currentDevice.getParam(ConfigDeviceField.HIDRAW_PATH);
    }

    String chosen = (chosen1 != null ? chosen1 : chosen2);
    return new HtmlOptionList(comports).getOptions(chosen);
  }

  private AjaxResult reloadHidraws()
  {
    List<Pair> hidraws = new SimpleHidrawConnection(currentDevice).getAvailableHidraws();
    String hidrawOptions = new HtmlOptionList(hidraws).getOptions(currentDevice.getParamOrDefault(ConfigDeviceField.HIDRAW_PATH, EMPTY));
    return new AjaxResult(true, hidrawOptions);
  }

  private AjaxResult testConnection()
  {
    Logger.debug("test connection ...");
    saveStep2();
    AbstractDevice testDevice = Config.getInstance().getDeviceFromClassName(currentDevice);
    boolean result = testDevice.checkConnection();
    if (result)
    {
      return new AjaxResult(true);
    }
    return new AjaxResult(false, SolarMain.languageHelper.replacePlaceholder(CONNECTION_ERROR));

  }

}
