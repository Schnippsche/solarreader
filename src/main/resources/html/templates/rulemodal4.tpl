<form class="needs-validation" method="post" id="id_frmrulesetup" novalidate>
  <div class="modal fade showmodal" id="id_ruleedit_modal4" data-bs-backdrop="static" data-bs-keyboard="false"
       tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-xl">
      <div class="modal-content">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">{rulesetup.summary.title}</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body minheight">
          <h4>{rulesetup.summary.title.text}</h4>
          <p>{rulesetup.summary.description}: [ruletitle]</p>
          <p class="fw-bold text-primary">{rulesetup.summary.if}</p>
          <p>[summarycondition]</p>
          <p class="fw-bold text-danger">{rulesetup.summary.then}</p>
          <p>[summaryaction]</p>
          <div class="row mb-3">
            <div class="col-md-12">
              <div class="form-check form-switch mt-2">
                <input class="form-check-input" type="checkbox"
                       role="switch" [RULEENABLED]
                       name="enable" id="id_ruleenable"/>
                <label class="form-check-label" for="id_ruleenable">{rulesetup.summary.activerule}</label>
              </div>
            </div>
          </div>
        </div>
        <div class="container">
          <div class="row">
            <div class="col-md-12">
              <div id="id_ruleealert" class="alert alert-danger d-none"><p></p>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <input type="hidden" id="id_step" name="step" value="saverule"/>
          <input type="hidden" id="id_page" name="page" value="4"/>
          <button type="button" id="id_rule_btn_back" value="3" class="btn btn-secondary">
            {button.step.back}
          </button>
          <button type="submit" class="btn btn-primary text-end">
            <span id="loader" class="spinner-border spinner-border-sm d-none"></span>
            {button.save}
          </button>
        </div>
      </div>
    </div>
  </div>
</form>