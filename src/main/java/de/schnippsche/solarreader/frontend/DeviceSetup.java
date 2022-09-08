package de.schnippsche.solarreader.frontend;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortInvalidPortException;
import de.schnippsche.solarreader.SolarMain;
import de.schnippsche.solarreader.backend.configuration.*;
import de.schnippsche.solarreader.backend.connections.NetworkConnection;
import de.schnippsche.solarreader.backend.connections.SimpleSerialConnection;
import de.schnippsche.solarreader.backend.utils.NumericHelper;
import de.schnippsche.solarreader.backend.utils.Pair;
import de.schnippsche.solarreader.frontend.elements.HtmlOptionList;
import okhttp3.Call;
import org.tinylog.Logger;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class DeviceSetup
{
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
      case "testconnection":
        return testConnection();
      default:
        return null;
    }
    return null;
  }

  public synchronized String getModalCode()
  {
    String step = formValues.getOrDefault("step", "");
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
        currentDevice = Config.getInstance().getDeviceFromUuid(formValues.getOrDefault("id", "0"));
        return showDeviceStep(1);
      case "confirmdeletedevice":
        return confirmDelete();
      case "deletedevice":
        Config.getInstance().removeDevice(currentDevice);
        return "";
      default:
        return "";
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
        return "";
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
        return "";
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
    String deviceOptions = Config.getInstance()
                                 .getAppInfo()
                                 .getDeviceOptionList()
                                 .getOptions(currentDevice.getDeviceInfoId());
    devicemaps.put("[deviceoptions]", deviceOptions);
    devicemaps.put("[description]", currentDevice.getDescription());
    devicemaps.put("[enabledelete]", currentDevice.getDescription().isEmpty() ? "d-none" : "");
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
    String checked = currentDevice.getParamOrDefault(ConfigDeviceField.HF2211_ENABLED, "")
                                  .equalsIgnoreCase("true") ? "checked" : "";
    devicemaps.put("[hf2211enabled]", checked);
    devicemaps.put("[HF2211IP]", currentDevice.getParamOrDefault(ConfigDeviceField.HF2211_IP, ""));
    devicemaps.put("[HF2211PORT]", currentDevice.getParamOrDefault(ConfigDeviceField.HF2211_PORT, ""));
    devicemaps.put("[DEVICEIP]", currentDevice.getParamOrDefault(ConfigDeviceField.DEVICE_IP, ""));
    devicemaps.put("[DEVICEPORT]", currentDevice.getParamOrDefault(ConfigDeviceField.DEVICE_PORT, ""));
    devicemaps.put("[DEVICEADDRESS]", currentDevice.getParamOrDefault(ConfigDeviceField.DEVICE_ADDRESS, ""));
    devicemaps.put("[BAUDRATE]", currentDevice.getParamOrDefault(ConfigDeviceField.BAUDRATE, ""));
    devicemaps.put("[SERIALNUMBER]", currentDevice.getParamOrDefault(ConfigDeviceField.SERIALNUMBER, ""));
    devicemaps.put("[COMPORTALTERNATIV]", currentDevice.getParamOrDefault(ConfigDeviceField.COM_PORT, ""));
    List<Pair> comports = new SimpleSerialConnection().getAvailablePorts();
    String options = new HtmlOptionList(comports).getOptions(currentDevice.getParamOrDefault(ConfigDeviceField.COM_PORT, ""));
    devicemaps.put("[comportoptions]", options);
    String divaddress = (deviceInfoFields.contains(DeviceInfoField.ADDRESS)) ? new HtmlElement(SolarMain.TEMPLATES_PATH + "divaddress.tpl").getHtmlCode(devicemaps) : "";
    String divbaudrate = (deviceInfoFields.contains(DeviceInfoField.BAUDRATE)) ? new HtmlElement(SolarMain.TEMPLATES_PATH + "divbaudrate.tpl").getHtmlCode(devicemaps) : "";
    String divhf2211 = (deviceInfoFields.contains(DeviceInfoField.HF2211)) ? new HtmlElement(SolarMain.TEMPLATES_PATH + "divhf2211.tpl").getHtmlCode(devicemaps) : "";
    String divlan = (deviceInfoFields.contains(DeviceInfoField.LAN)) ? new HtmlElement(SolarMain.TEMPLATES_PATH + "divlan.tpl").getHtmlCode(devicemaps) : "";
    String divserial = (deviceInfoFields.contains(DeviceInfoField.SERIALNUMBER)) ? new HtmlElement(SolarMain.TEMPLATES_PATH + "divserial.tpl").getHtmlCode(devicemaps) : "";
    String divusb = (deviceInfoFields.contains(DeviceInfoField.SERIELL)) ? new HtmlElement(SolarMain.TEMPLATES_PATH + "divusb.tpl").getHtmlCode(devicemaps) : "";
    devicemaps.put("[divaddress]", divaddress);
    devicemaps.put("[divbaudrate]", divbaudrate);
    devicemaps.put("[divhf2211]", divhf2211);
    devicemaps.put("[divlan]", divlan);
    devicemaps.put("[divserial]", divserial);
    devicemaps.put("[divusb]", divusb);

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
    currentDevice.setDeviceInfoId(formValues.getOrDefault("deviceselect", ""));
    DeviceInfo deviceInfo = Config.getInstance().getAppInfo().getDeviceInfo(currentDevice.getDeviceInfoId());
    currentDevice.setDeviceName(deviceInfo.getDeviceName());
    currentDevice.setDeviceClass(deviceInfo.getDeviceClass());
    currentDevice.setDescription(formValues.getOrDefault("description", ""));
    return showStep2();
  }

  private String saveStep2()
  {
    currentDevice.setParam(ConfigDeviceField.SERIALNUMBER, formValues.get("serial"));
    currentDevice.setParam(ConfigDeviceField.DEVICE_ADDRESS, formValues.get("device_address"));
    currentDevice.setParam(ConfigDeviceField.DEVICE_IP, formValues.get("device_ip"));
    currentDevice.setParam(ConfigDeviceField.DEVICE_PORT, formValues.get("device_port"));
    currentDevice.setParam(ConfigDeviceField.HF2211_ENABLED, Boolean.toString(formValues.containsKey("usehf2211")));
    currentDevice.setParam(ConfigDeviceField.HF2211_IP, formValues.get("hf2211ip"));
    currentDevice.setParam(ConfigDeviceField.HF2211_PORT, formValues.get("hf2211port"));

    if (!formValues.getOrDefault("comportalternativ", "").isEmpty())
    {
      currentDevice.setParam(ConfigDeviceField.COM_PORT, formValues.get("comportalternativ"));
    } else
    {
      currentDevice.setParam(ConfigDeviceField.COM_PORT, formValues.getOrDefault("com_port", ""));
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
    if (!Config.getInstance().getConfigDevices().contains(currentDevice))
    {
      Config.getInstance().getConfigDevices().add(currentDevice);
    }
    dialogHelper.saveConfiguration();
    // mark for reload all devices
    Config.getInstance().setDeviceConfigurationChanged();
    return "";
  }

  private AjaxResult checkStep(String page)
  {

    switch (page)
    {
      case "1":
        final String newName = formValues.getOrDefault("description", "");
        long present = Config.getInstance()
                             .getConfigDevices()
                             .stream()
                             .filter(cd -> cd.getDescription().equalsIgnoreCase(newName))
                             .filter(cd -> !cd.getUuid().equals(currentDevice.getUuid()))
                             .count();
        if (present > 0)
        {
          return new AjaxResult(false, SolarMain.languageHelper.replacePlaceholder("{devicesetup.name.exists}"));
        }
        break;
      case "2":
        return testConnection();
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

  private AjaxResult checkComport(String testPort) throws SerialPortInvalidPortException
  {
    Logger.debug("check comport {}", testPort);
    SerialPort tempPort = SerialPort.getCommPort(testPort);
    boolean ok = tempPort.openPort();
    tempPort.closePort();
    if (!ok)
    {
      Logger.error("can't connect to port {}, error code {}", testPort, tempPort.getLastErrorCode());
      return new AjaxResult(false, SolarMain.languageHelper.replacePlaceholder("{devicesetup.connection.error}"));
    }

    return new AjaxResult(true);
  }

  private AjaxResult reloadComports()
  {
    List<Pair> comports = new SimpleSerialConnection().getAvailablePorts();
    String options = new HtmlOptionList(comports).getOptions(currentDevice.getParamOrDefault(ConfigDeviceField.COM_PORT, ""));
    return new AjaxResult(true, options);
  }

  private AjaxResult testConnection()
  {
    String testPort;
    try
    {
      // hf2211 enabled ? Test IP connection
      if (formValues.containsKey("usehf2211"))
      {
        return checkLan(formValues.get("hf2211ip"), formValues.get("hf2211port"));
      } else if (!formValues.getOrDefault("device_ip", "").isEmpty())
      {
        return checkLan(formValues.get("device_ip"), formValues.get("device_port"));
      }
      if (!formValues.getOrDefault("comportalternativ", "").isEmpty())
      {
        testPort = formValues.get("comportalternativ");
      } else
      {
        testPort = formValues.getOrDefault("com_port", "");
      }
      return checkComport(testPort);
    } catch (Exception ex)
    {
      Logger.error("can't connect to device, reason: {}", ex);
      return new AjaxResult(false, SolarMain.languageHelper.replacePlaceholder("{devicesetup.connection.error}"));
    }
  }

  private AjaxResult checkLan(String ip, String port) throws IOException
  {
    Logger.debug("check lan with ip {} and port {}", ip, port);
    String testUrl = String.format("http://%s:%s/", ip, port);
    okhttp3.Request request = new okhttp3.Request.Builder().url(testUrl).build();
    Call call = NetworkConnection.HTTPCLIENT.newCall(request);
    try (okhttp3.Response response = call.execute())
    {
      return new AjaxResult(true);
    }
  }

}
