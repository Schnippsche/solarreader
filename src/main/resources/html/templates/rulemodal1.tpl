<form class="needs-validation" method="post" id="id_frmrulesetup" novalidate>
  <div class="modal fade showmodal" id="id_ruleedit_modal1" data-bs-backdrop="static" data-bs-keyboard="false"
       tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable modal-xl">
      <div class="modal-content">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">{rulesetup.title}</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                  aria-label="Close"></button>
        </div>
        <div class="modal-body minheight">
          <h5>{rulesetup.title.text}</h5>
          [rules]
        </div>
        <div class="modal-footer">
          <input type="hidden" id="id_step" name="step" value="saverule"/>
          <input type="hidden" id="id_page" name="page" value="1"/>
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