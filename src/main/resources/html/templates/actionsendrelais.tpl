<div id="[id]">
  <div class="input-group mb-2 has-validation">
    <label class="input-group-text" for="device_[id]">{rulesetup.action.relais.sendto}</label>
    <select class="form-select deviceselector" id="device_[id]" name="device_[id]">
      [devices]
    </select>
    <label class="input-group-text" for="command_[id]">{rulesetup.action.relais.type}</label>
    <select class="form-select commandselector" id="command_[id]" name="command_[id]">
      [commands]
    </select>
    <label class="input-group-text " for="choice_[id]">{rulesetup.action.relais.command}</label>
    <select class="form-select choiceselector" id="choice_[id]" name="choice_[id]">
      [choices]
    </select>
    <label id="durationlabel_[id]" class="input-group-text d-none"
           for="seconds_[id]">{rulesetup.action.relais.duration}</label>
    <input type="number" class="form-control d-none" id="seconds_[id]" name="seconds_[id]" min="1" value="[seconds]"/>
    <button class="btn btn-outline-danger delaction" type="button" data-row-id="[id]"><em
      class="bi bi-trash-fill"></em></button>
    <div class="invalid-feedback">{field.required.value}</div>
  </div>
</div>