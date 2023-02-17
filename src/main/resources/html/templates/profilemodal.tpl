<form class="needs-validation" method="post" id="id_frmprofilesetup" novalidate>
  <div class="modal fade showmodal" id="id_profileedit_modal" data-bs-backdrop="static" data-bs-keyboard="false"
       tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">{profilesetup.title}</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="row">
            <p>{profilesetup.title.text}</p>
          </div>
          <div class="row mb-3">
            <div class="col-md-6">
              <div class="form-floating has-validation has-feedback">
                <input type="text" class="form-control" id="id_longitude_id" name="longitude" value="[longitude]"
                       placeholder="{profilesetup.longitude.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       pattern="^[-+]?((1[0-7]\d)|(\d{1,2}))(\.\d+)?$"
                       title="{profilesetup.longitude.tooltip}"/>
                <label for="id_longitude_id">{profilesetup.longitude.text}</label>
                <div class="invalid-feedback">{profilesetup.longitude.error}</div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-floating has-validation has-feedback">
                <input type="text" class="form-control" id="id_latitude_id" name="latitude" value="[latitude]"
                       placeholder="{profilesetup.latitude.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       pattern="^[-+]?(([0-8]\d)|(\d{1,2}))(\.\d+)?$"
                       title="{profilesetup.latitude.tooltip}"/>
                <label for="id_latitude_id">{profilesetup.latitude.text}</label>
                <div class="invalid-feedback">{profilesetup.latitude.error}</div>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-12">
              <div id="id_profilealert" class="alert alert-danger d-none">
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <input type="hidden" id="id_step" name="step" value="saveprofile"/>
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">{button.abort}</button>
          <button type="submit" class="btn btn-primary">
            <span id="loader" class="spinner-border spinner-border-sm d-none"></span>
            {button.save}
          </button>
        </div>
      </div>
    </div>
  </div>
</form>