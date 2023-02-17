<form class="needs-validation" method="post" id="id_frmrulesetup" novalidate>
  <div class="modal fade showmodal" id="id_ruleedit_modal2" data-bs-backdrop="static" data-bs-keyboard="false"
       tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-xl">
      <div class="modal-content">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">{rulesetup.conditions.title}</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                  aria-label="Close"></button>
        </div>
        <div class="modal-body minheight">
          <div class="input-group mb-2 has-validation">
            <span class="input-group-text">{rulesetup.conditions.title.text}</span>
            <input type="text" class="form-control" name="rulename" value="[rulename]" required/>
            <div class="invalid-feedback">{field.required.value}</div>
          </div>
          <h5>{rulesetup.conditions.title2}:</h5>
          [conditions]
          <div id="divnewcondition" class="dropdown mt-3">
            <button class="btn btn-info dropdown-toggle" type="button" id="idnewcondition"
                    data-bs-toggle="dropdown" aria-expanded="false">
              {rulesetup.conditions.addcondition.text}
            </button>
            <ul class="dropdown-menu" aria-labelledby="idnewcondition">
              <li><a class="dropdown-item addnewcondition" data-id="1">{rulesetup.conditions.addcondition1}</a></li>
              <li><a class="dropdown-item addnewcondition" data-id="3">{rulesetup.conditions.addcondition3}</a></li>
              <li><a class="dropdown-item addnewcondition" data-id="4">{rulesetup.conditions.addcondition4}</a></li>
              <li><a class="dropdown-item addnewcondition" data-id="5">{rulesetup.conditions.addcondition5}</a></li>
              <li><a class="dropdown-item addnewcondition" data-id="6">{rulesetup.conditions.addcondition6}</a></li>
            </ul>
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
          <input type="hidden" id="id_page" name="page" value="2"/>
          <button id="id_btnruledelete" title="{rulesetup.deletebutton.tooltip}" type="button"
                  data-bs-dismiss="modal" class="btn btn-outline-danger me-auto [enabledelete]">
            <em class="bi bi-trash"></em>
          </button>
          <button type="button" id="id_rule_btn_back" value="1" class="btn btn-secondary">
            {button.step.back}
          </button>
          <button type="submit" class="btn btn-primary text-end">
            <span id="loader" class="spinner-border spinner-border-sm d-none"></span>
            {button.step.forward}
          </button>
        </div>
      </div>
    </div>
  </div>
</form>