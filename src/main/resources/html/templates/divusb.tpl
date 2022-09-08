<div id="id_divusb">
  <div class="row mb-3">
    <div class="col-md-12 mb-2">
      {devicesetup.serialport.intro}
    </div>
    <div class="col-md-12">
      <div class="form-floating input-group">
        <select class="form-select" id="id_devicesetup_com_port" name="com_port">
          [comportoptions]
        </select>
        <label for="id_devicesetup_com_port">{devicesetup.serialport.title}</label>
        <button type="button" data-bs-toggle="tooltip" data-bs-placement="top"
                title="{devicesetup.serial.buttonreload.tooltip}" id="id_btn_serialreload"
                class="btn btn-primary input-group-append">&nbsp;<em
          class="bi bi-arrow-clockwise">&nbsp;</em>&nbsp;
        </button>
      </div>
    </div>
  </div>
  <div class="row mb-3">
    <div class="col-md-12">
      <p class="fst-italic small">{devicesetup.serialport.alternativ.title}</p>
    </div>
    <div class="col-md-12">
      <div class="form-floating">
        <input type="text" class="form-control" id="id_devicesetup_comportalternativ"
               name="comportalternativ"
               value="[COMPORTALTERNATIV]"
               placeholder="{devicesetup.serial.alternativ.text}"
               data-bs-toggle="tooltip" data-bs-placement="bottom"
               title="{devicesetup.serial.alternativ.tooltip}"/>
        <label for="id_devicesetup_comportalternativ">{devicesetup.serial.alternativ.text}</label>
      </div>
    </div>
  </div>
</div>