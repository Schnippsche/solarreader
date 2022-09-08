<form class="needs-validation" method="post" id="id_frmawattarsetup" novalidate>
  <div class="modal fade showmodal" id="id_tablefieldedit_modal" data-bs-backdrop="static" data-bs-keyboard="false"
       tabindex="-1">
    <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
      <div class="modal-content">
        <div class="modal-header bg-primary text-white">
          <h5 class="modal-title">{tablefieldedit.title}</h5>
          <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
          <div class="row">
            <p>{tablefieldedit.title.text}</p>
          </div>
          <div class="row mb-3">
            <div class="col-md-6">
              <div class="form-floating has-validation has-feedback">
                <input type="text" class="form-control" id="id_tablename" name="tablename" value="[tablename]"
                       placeholder="{tablefield.tablename.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom" pattern="^[A-Za-z]+[A-Za-z0-9]*"
                       title="{tablefield.tablename.tooltip}"/>
                <label for="id_tablename">{tablefield.tablename.text}</label>
                <div class="invalid-feedback">{tablefield.tablename.error}</div>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-floating has-validation has-feedback">
                <input type="text" class="form-control" id="id_columnname" name="columnname" value="[columnname]"
                       placeholder="{tablefield.columnname.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom" pattern="^[A-Za-z_]+[A-Za-z0-9_]*"
                       title="{tablefield.columnname.tooltip}"/>
                <label for="id_columnname">{tablefield.columnname.text}</label>
                <div class="invalid-feedback">{tablefield.columnname.error}</div>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-6">
              <div class="form-floating">
                <select class="form-select" id="id_columntype" name="columntype">
                  [COLUMNTYPES]
                </select>
                <label for="id_columntype">{tablefield.columntype.title}</label>
              </div>
            </div>
            <div class="col-md-6">
              <div class="form-floating">
                <select class="form-select" id="id_sourcetype" name="sourcetype">
                  [SOURCETYPES]
                </select>
                <label for="id_sourcetype">{tablefield.sourcetype.title}</label>
              </div>
            </div>
          </div>
          <div class="row mb-3">
            <div class="col-md-12" id="id_sourcetypes">
              <div class="form-floating has-validation has-feedback^d-none" id="id_constant">
                <input type="text" class="form-control" id="id_sourcevalueconstanttext" name="sourcevalue"
                       value="[sourcevalue]"
                       placeholder="{tablefield.sourcevalue.constant.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{tablefield.sourcevalue.constant.tooltip}"/>
                <label for="id_sourcevalueconstanttext">{tablefield.sourcevalue.constant.text}</label>
                <div class="invalid-feedback">{tablefield.sourcevalue.constant.error}</div>
              </div>
              <div class="form-floating has-validation has-feedback d-none" id="id_calculated">
                <input type="text" class="form-control" id="id_sourcevaluecalculatedtext" name="sourcevalue"
                       value="[sourcevalue]"
                       placeholder="{tablefield.sourcevalue.calculated.text}" required
                       data-bs-toggle="tooltip" data-bs-placement="bottom"
                       title="{tablefield.sourcevalue.calculated.tooltip}"/>
                <label for="id_sourcevaluecalculatedtext">{tablefield.sourcevalue.calculated.text}</label>
                <div class="invalid-feedback">{tablefield.sourcevalue.calculated.error}</div>
              </div>
              <div class="form-floating d-none" id="id_resultfield">
                <select class="form-select" id="id_sourcevalueresultfield" name="sourcevalue">
                  [RESULTFIELDVALUES]
                </select>
                <label for="id_sourcevalueresultfield">{tablefield.sourcevalue.resultfield.title}</label>
              </div>
              <div class="form-floating d-none" id="id_standardfield">
                <select class="form-select" id="id_sourcevaluestandardfield" name="sourcevalue">
                  [STANDARDFIELDVALUES]
                </select>
                <label for="id_sourcevaluestandardfield">{tablefield.sourcevalue.standardfield.title}</label>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <input type="hidden" id="id_step" name="step" value="savetablefield"/>
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