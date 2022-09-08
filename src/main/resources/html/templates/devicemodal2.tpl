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
          [divhf2211]
          [divlan]
          <div class="row mb-3">
            [divaddress]
            [divbaudrate]
            [divserial]
          </div>
          [divusb]
          <div class="row">
            <div class="col-md-12">
              <div id="id_devicealert" class="alert alert-danger d-none"><p></p>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" id="id_device_btn_back" value="1" class="btn btn-secondary">
            {button.step.back}
          </button>
          <input type="hidden" id="id_step" name="step" value="savedevice"/>
          <input type="hidden" id="id_page" name="page" value="2"/>
          <button type="submit" id="id_device_btn_forward" class="btn btn-primary text-end">
            <span id="loader" class="spinner-border spinner-border-sm d-none"></span>
            {button.step.forward}
          </button>
        </div>
      </div>
    </div>
  </div>
</form>