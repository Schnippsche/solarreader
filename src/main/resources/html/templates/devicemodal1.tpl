<form class="needs-validation" method="post" id="id_frmdevicesetup" novalidate>
  <div class="modal fade showmodal" id="id_deviceedit_modal" data-bs-backdrop="static" data-bs-keyboard="false"
       tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">{devicesetup.title}</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <div class="row">
            <div class="col-md-12">
              <p>{devicesetup.page1.title}</p>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-12">
              <div class="form-floating">
                <select class="form-select" id="id_device_selection" name="deviceselect">
                  [deviceoptions]
                </select>
                <label for="id_device_selection">{devicesetup.deviceselect.title}</label>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-12">
              <div class="form-floating">
                <input type="text" class="form-control" id="id_devicesetup_title"
                       name="description"
                       value="[description]" placeholder="{devicesetup.title.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{devicesetup.title.tooltip}"/>
                <label for="id_devicesetup_title">{devicesetup.title.text}</label>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-12">
              <div id="id_devicealert" class="alert alert-danger d-none">
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <input type="hidden" id="id_step" name="step" value="savedevice"/>
          <input type="hidden" id="id_page" name="page" value="1"/>
          <button id="id_btndevicedelete" title="{devicesetup.deletebutton.tooltip}" type="button"
                  data-bs-dismiss="modal" class="btn btn-outline-danger me-auto [enabledelete]">
            <em class="bi bi-trash"></em>
          </button>
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">{button.abort}</button>
          <button type="submit" id="id_device_btn_forward" class="btn btn-primary text-end">
            <span id="loader" class="spinner-border spinner-border-sm d-none"> </span>
            {button.step.forward}
          </button>
        </div>
      </div>
    </div>
  </div>
</form>