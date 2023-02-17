<div id="[id]">
  <div class="input-group mb-2 has-validation">
    <span class="input-group-text">{rulesetup.action.database.sendto}</span>
    <select class="form-select" name="database_[id]">
      [databaselist]
    </select>
    <span class="input-group-text">{rulesetup.action.database.table}</span>
    <input type="text" class="form-control" name="table_[id]" value="[TABLE]" required/>
    <span class="input-group-text">{rulesetup.action.database.column}</span>
    <input type="text" class="form-control" name="column_[id]" value="[COLUMN]" required/>
    <span class="input-group-text">{rulesetup.action.database.colvalue}</span>
    <input type="text" class="form-control" name="value_[id]" value="[VALUE]" required/>
    <button class="btn btn-outline-danger delaction" type="button" data-row-id="[id]"><em
      class="bi bi-trash-fill"></em></button>
    <div class="invalid-feedback">{field.required.value}</div>
  </div>
</div>