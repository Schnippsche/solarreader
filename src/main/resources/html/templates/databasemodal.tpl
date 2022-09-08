<form class="needs-validation" method="post" id="id_frmdatabasesetup" novalidate>
  <div class="modal fade showmodal" id="id_databaseedit_modal" data-bs-backdrop="static" data-bs-keyboard="false"
       tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">{databasesetup.title}</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="row mb-3">
            <div class="col-md-6">
              <div class="form-floating has-validation has-feedback">
                <input type="text" class="form-control" id="id_title" name="dbtitle" value="[dbtitle]"
                       placeholder="{databasesetup.dbtitle.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{databasesetup.dbtitle.tooltip}"/>
                <label for="id_title">{databasesetup.dbtitle.text}</label>
                <div class="invalid-feedback">{field.required.value}</div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-floating has-validation has-feedback">
                <input type="text" class="form-control" id="id_databasename" name="dbname" value="[dbname]"
                       placeholder="{databasesetup.dbname.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{databasesetup.dbname.tooltip}"/>
                <label for="id_databasename">{databasesetup.dbname.text}</label>
                <div class="invalid-feedback">{field.required.value}</div>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-6">
              <div class="form-floating has-validation has-feedback">
                <input type="text" class="form-control" id="id_host" name="dbhost" value="[dbhost]"
                       placeholder="{databasesetup.dbhost.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{databasesetup.dbhost.tooltip}"/>
                <label for="id_host">{databasesetup.dbhost.text}</label>
                <div class="invalid-feedback">{field.required.value}</div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-floating has-validation has-feedback">
                <input type="text" class="form-control" id="id_port" name="dbport" value="[dbport]"
                       placeholder="{databasesetup.dbport.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{databasesetup.dbport.tooltip}"
                       pattern="\d+"/>
                <label for="id_port">{databasesetup.dbport.text}</label>
                <div class="invalid-feedback">{field.required.onlynumbers}</div>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-6">
              <div class="form-floating">
                <input type="text" class="form-control" id="id_user" name="dbuser" value="[dbuser]"
                       placeholder="{databasesetup.dbuser.text}"
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{databasesetup.dbuser.tooltip}"/>
                <label for="id_user">{databasesetup.dbuser.text}</label>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-floating">
                <input type="text" class="form-control" id="id_password" name="dbpassword" value="[dbpassword]"
                       placeholder="{databasesetup.dbpassword.text}"
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{databasesetup.dbpassword.tooltip}"/>
                <label for="id_password">{databasesetup.dbpassword.text}</label>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-6">
              <div class="form-floating">
                <div class="form-control" id="id_switchssltitle">
                  <div class="form-check form-switch">
                    <input class="form-check-input" type="checkbox" role="switch"
                           name="dbusessl" [dbusesslchecked] id="id_usessl"/>
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
                           name="dbenable" [dbenablechecked] id="id_enable"/>
                    <label class="form-check-label" for="id_enable" data-bs-toggle="tooltip"
                           data-bs-placement="bottom"
                           title="{databasesetup.dbenable.tooltip}">{databasesetup.dbenable.text}</label>
                  </div>
                </div>
                <label for="id_switchenabledtitle">{databasesetup.dbenable.title}</label>
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-12">
              <div id="id_databasealert" class="alert alert-danger d-none">
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <input type="hidden" id="id_step" name="step" value="savedatabase"/>
          <button id="id_btndatabasedelete" title="{databasesetup.deletebutton.tooltip}" type="button"
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