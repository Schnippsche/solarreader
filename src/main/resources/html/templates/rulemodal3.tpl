<form class="needs-validation" method="post" id="id_frmrulesetup" novalidate>
  <div class="modal fade showmodal" id="id_ruleedit_modal3" data-bs-backdrop="static" data-bs-keyboard="false"
       tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-xl">
      <div class="modal-content">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">{rulesetup.actions.title}</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                  aria-label="Close"></button>
        </div>
        <div class="modal-body minheight">
          <h5>{rulesetup.actions.title.text}:</h5>
          [actions]
          <div id="divnewaction" class="dropdown mt-3">
            <button class="btn btn-info dropdown-toggle" type="button" id="idnewaction"
                    data-bs-toggle="dropdown" aria-expanded="false">
              {rulesetup.actions.addAction.text}
            </button>
            <ul class="dropdown-menu" aria-labelledby="idnewaction">
              <li><a class="dropdown-item addnewaction" data-id="1">{rulesetup.actions.addAction1}</a></li>
              <li><a class="dropdown-item addnewaction  [relaisvisible]" data-id="2">{rulesetup.actions.addAction2}</a>
              </li>
              <li><a class="dropdown-item addnewaction" data-id="3">{rulesetup.actions.addAction3}</a></li>
              <li><a class="dropdown-item addnewaction" data-id="4">{rulesetup.actions.addAction4}</a></li>
              <li><a class="dropdown-item addnewaction" data-id="5">{rulesetup.actions.addAction5}</a></li>
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
          <input type="hidden" id="id_page" name="page" value="3"/>
          <button type="button" id="id_rule_btn_back" value="2" class="btn btn-secondary">
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