<form class="needs-validation" method="post" id="id_frmdevicesetup" novalidate>
  <div class="modal showmodal" id="id_deviceedit_modal" data-bs-backdrop="static" data-bs-keyboard="false"
       tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">{devicesetup.title}</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="row mb-3">
            <div class="col-md-12">
              <div class="form-control">
                {devicesetup.databasereceiver.checkbox.title}
                [databasecheckboxes]
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-12">
              <div class="form-control">
                {devicesetup.mqttreceiver.checkbox.title}
                <div class="row g-2 align-items-center">
                  <div class="col-auto">
                    <label for="id_mqtttopic" class="col-form-label">{mqttsetup.name.text}</label>
                  </div>
                  <div class="col-auto">
                    <input type="text" id="id_mqtttopic" class="form-control" name="mqtttopic" value="[mqtttopic]"
                           title="{mqttsetup.name.tooltip}">
                  </div>
                </div>
                [mqttcheckboxes]
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-12">
              <div id="id_devicealert" class="alert alert-danger d-none">
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" id="id_device_btn_back" value="3" class="btn btn-secondary">
            {button.step.back}
          </button>
          <input type="hidden" id="id_step" name="step" value="savedevice"/>
          <input type="hidden" id="id_page" name="page" value="4"/>
          <button type="submit" id="id_device_btn_forward" class="btn btn-primary text-end">
            <span id="loader" class="spinner-border spinner-border-sm d-none"></span>
            {button.save}
          </button>
        </div>
      </div>
    </div>
  </div>
</form>