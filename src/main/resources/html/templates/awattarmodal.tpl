<form class="needs-validation" method="post" id="id_frmawattarsetup" novalidate>
  <div class="modal fade showmodal" id="id_awattaredit_modal" data-bs-backdrop="static" data-bs-keyboard="false"
       tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">{awattarsetup.title}</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="row">
            <p>{awattarsetup.title.text}</p>
          </div>
          <div class="row mb-3 ">
            <div class="col-md-12">
              <div class="form-floating has-validation has-feedback">
                <input type="number" class="form-control" id="id_price" name="price" value="[price]"
                       placeholder="{awattarsetup.price.text}" required step="any"
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{awattarsetup.price.tooltip}"/>
                <label for="id_price">{awattarsetup.price.text}</label>
                <div class="invalid-feedback">{awattarsetup.price.error}</div>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-12">
              <fieldset class="form-group border p-3">
                <div class="row mb-3">
                  <div class="col-md-12">
                    <div class="form-floating">
                      {devicesetup.enable.title}
                      <div class="form-check form-switch mt-2">
                        <input class="form-check-input" type="checkbox"
                               role="switch" [ENABLED]
                               name="enable" id="id_deviceenable"/>
                        <label class="form-check-label" for="id_deviceenable"
                               data-bs-toggle="tooltip"
                               data-bs-placement="bottom"
                               title="{devicesetup.enable.tooltip}">{devicesetup.enable.text}</label>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="row mb-3">
                  <div class="col-md-6">
                    <div class="form-floating has-validation has-feedback">
                      <input type="time" class="form-control" id="id_devicesetup_activityfrom"
                             name="activityfrom" value="[ACTIVITYFROM]" placeholder="{activity.from.title}" step="1"
                             data-bs-toggle="tooltip" data-bs-placement="bottom" required
                             title="{activity.from.tooltip}"/>
                      <label for="id_devicesetup_activityfrom">{activity.from.title}</label>
                      <div class="invalid-feedback">{field.required.value}</div>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="form-floating has-validation has-feedback">
                      <input type="time" class="form-control" id="id_devicesetup_activityto"
                             name="activityto" value="[ACTIVITYTO]" placeholder="{activity.to.title}" step="1"
                             data-bs-toggle="tooltip" data-bs-placement="bottom"
                             title="{activity.to.tooltip}" required/>
                      <label for="id_devicesetup_activityto">{activity.to.title}</label>
                      <div class="invalid-feedback">{field.required.value}</div>
                    </div>
                  </div>
                </div>
                <div class="row mb-3">
                  <div class="col-md-6">
                    <div class="form-floating has-validation has-feedback">
                      <input type="number" class="form-control" id="id_devicesetup_activityinterval"
                             name="activityinterval" value="[ACTIVITYINTERVAL]"
                             placeholder="{activity.interval.title}" required min="1" max="2000"
                             data-bs-toggle="tooltip" data-bs-placement="bottom"
                             title="{activity.interval.tooltip}"/>
                      <label for="id_devicesetup_activityinterval">{activity.interval.title}</label>
                      <div class="invalid-feedback">{field.required.onlynumbers}</div>
                    </div>
                  </div>
                  <div class="col-md-6">
                    <div class="form-floating">
                      <select class="form-select" id="id_devicesetup_activityunit"
                              name="timeUnit">
                        [TIMEUNITS]
                      </select>
                      <label for="id_devicesetup_activityunit">{activity.unit.title}</label>
                    </div>
                  </div>
                </div>
              </fieldset>
            </div>
          </div>
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
                [mqttcheckboxes]
              </div>
            </div>
          </div>
          <div class="row">
            <div class="col-md-12">
              <div id="id_awattaralert" class="alert alert-danger d-none">
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <input type="hidden" id="id_step" name="step" value="saveawattar"/>
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