<form class="needs-validation" method="post" id="id_frmmqttsetup" novalidate>
  <div class="modal fade showmodal" id="id_mqttedit_modal" data-bs-backdrop="static" data-bs-keyboard="false"
       tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">{mqttsetup.title}</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="row mb-3">
            <div class="col-md-6">
              <div class="form-floating has-validation has-feedback">
                <input type="text" class="form-control" id="id_title" name="mqtttitle" value="[mqtttitle]"
                       placeholder="{mqttsetup.title.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{mqttsetup.title.tooltip}"/>
                <label for="id_title">{mqttsetup.title.text}</label>
                <div class="invalid-feedback">{field.required.value}</div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-floating has-validation has-feedback">
                <input type="text" class="form-control" id="id_mqttname" name="mqttname" value="[mqttname]"
                       placeholder="{mqttsetup.maintopic.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{mqttsetup.maintopic.tooltip}"/>
                <label for="id_mqttname">{mqttsetup.maintopic.text}</label>
                <div class="invalid-feedback">{field.required.value}</div>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-6">
              <div class="form-floating has-validation has-feedback">
                <input type="text" class="form-control" id="id_host" name="mqtthost" value="[mqtthost]"
                       placeholder="{mqttsetup.host.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{mqttsetup.host.tooltip}"/>
                <label for="id_host">{mqttsetup.host.text}</label>
                <div class="invalid-feedback">{field.required.value}</div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-floating has-validation has-feedback">
                <input type="text" class="form-control" id="id_port" name="mqttport" value="[mqttport]"
                       placeholder="{mqttsetup.port.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{mqttsetup.port.tooltip}"
                       pattern="\d+"/>
                <label for="id_port">{mqttsetup.port.text}</label>
                <div class="invalid-feedback">{field.required.onlynumbers}</div>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-6">
              <div class="form-floating">
                <input type="text" class="form-control" id="id_user" name="mqttuser" value="[mqttuser]"
                       placeholder="{mqttsetup.user.text}"
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{mqttsetup.user.tooltip}"/>
                <label for="id_user">{mqttsetup.user.text}</label>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-floating">
                <input type="text" class="form-control" id="id_password" name="mqttpassword" value="[mqttpassword]"
                       placeholder="{mqttsetup.password.text}"
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{mqttsetup.password.tooltip}"/>
                <label for="id_password">{mqttsetup.password.text}</label>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-6">
              <div class="form-floating">
                <div class="form-control" id="id_switchssltitle">
                  <div class="form-check form-switch">
                    <input class="form-check-input" type="checkbox" role="switch"
                           name="mqttusessl" [mqttusesslchecked] id="id_usessl"/>
                    <label class="form-check-label" for="id_usessl" data-bs-toggle="tooltip"
                           data-bs-placement="bottom"
                           title="{switch.use.ssl.tooltip}">{switch.use.ssl}</label>
                  </div>
                </div>
                <label for="id_switchssltitle">{switch.use.ssl.label}</label>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-floating">
                <div class="form-control" id="id_switchenabledtitle">
                  <div class="form-check form-switch">
                    <input class="form-check-input" type="checkbox" role="switch"
                           name="mqttenable" [mqttenablechecked] id="id_enable"/>
                    <label class="form-check-label" for="id_enable" data-bs-toggle="tooltip"
                           data-bs-placement="bottom"
                           title="{mqttsetup.enable.tooltip}">{mqttsetup.enable.text}</label>
                  </div>
                </div>
                <label for="id_switchenabledtitle">{mqttsetup.enable.title}</label>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-12">
              <div id="id_mqttalert" class="alert alert-danger d-none">
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <input type="hidden" id="id_step" name="step" value="savemqtt"/>
          <button id="id_btnmqttdelete" title="{mqttsetup.deletebutton.tooltip}" type="button"
                  data-bs-dismiss="modal" class="btn btn-outline-danger me-auto [enabledelete]">
            <em class="bi bi-trash"></em>
          </button>
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