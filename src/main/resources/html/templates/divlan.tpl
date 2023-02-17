<div class="row mb-3" id="id_divlan">
  <div class="col-md-6">
    <div class="form-floating has-validation has-feedback">
      <input class="form-control" data-bs-placement="bottom" data-bs-toggle="tooltip" id="id_devicesetup_device_ip"
             name="device_ip" placeholder="{devicesetup.host.text}"
             required
             title="{devicesetup.host.tooltip}" type="text"
             value="[DEVICEIP]"/>
      <label for="id_devicesetup_device_ip">{devicesetup.host.text}</label>
      <div class="invalid-feedback">{field.required.value}</div>
    </div>
  </div>
  <div class="col-md-6">
    <div class="form-floating has-validation has-feedback">
      <input class="form-control" data-bs-placement="bottom" data-bs-toggle="tooltip"
             id="id_devicesetup_device_port" name="device_port" placeholder="{devicesetup.port.text}"
             required
             title="{devicesetup.port.tooltip}" type="number"
             value="[DEVICEPORT]"/>
      <label for="id_devicesetup_device_port">{devicesetup.port.text}</label>
      <div class="invalid-feedback">{field.onlynumbers}</div>
    </div>
  </div>
</div>
