<div class="row mb-3" id="id_divlan">
  <div class="col-md-6">
    <div class="form-floating has-validation has-feedback">
      <input type="text" class="form-control" id="id_devicesetup_device_ip" name="device_ip"
             value="[DEVICEIP]" required
             placeholder="{devicesetup.host.text}"
             data-bs-toggle="tooltip" data-bs-placement="bottom"
             title="{devicesetup.host.tooltip}"/>
      <label for="id_devicesetup_device_ip">{devicesetup.host.text}</label>
      <div class="invalid-feedback">{field.required.value}</div>
    </div>
  </div>
  <div class="col-md-6">
    <div class="form-floating has-validation has-feedback">
      <input type="number" class="form-control" id="id_devicesetup_device_port"
             name="device_port" value="[DEVICEPORT]" required
             placeholder="{devicesetup.port.text}"
             data-bs-toggle="tooltip" data-bs-placement="bottom"
             title="{devicesetup.port.tooltip}"/>
      <label for="id_devicesetup_device_port">{devicesetup.port.text}</label>
      <div class="invalid-feedback">{field.onlynumbers}</div>
    </div>
  </div>
</div>
