<div id="[id]">
  <div class="input-group mb-2 has-validation">
    <span class="input-group-text">{rulesetup.action.device.sendto}</span>
    <select class="form-select" id="devicename_[id]" name="devicename_[id]">
      [devicelist]
    </select>
    <span class="input-group-text">{rulesetup.action.device.command}</span>
    <input type="text" class="form-control" name="sendname_[id]" value="[COMMAND]" required/>
    <button class="btn btn-outline-danger delaction" type="button" data-row-id="[id]"><em
      class="bi bi-trash-fill"></em></button>
    <div class="invalid-feedback">{field.required.value}</div>
  </div>
</div>