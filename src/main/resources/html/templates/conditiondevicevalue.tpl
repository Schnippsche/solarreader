<div id="[id]">
  <!-- condition device value -->
  <div class="row">
    <div class="col-sm-3 col md-2 col-lg-2 mb-2 comparator">
      <select class="form-select" name="compare_[id]" id="compare_[id]">
        [comparelist]
      </select>
    </div>
  </div>
  <div class="input-group mb-2 has-validation">
    <label class="input-group-text" for="device_[id]">{rulesetup.conditiondevice.from}</label>
    <select class="form-select deviceselector" id="device_[id]" name="device_[id]">
      [devicelist]
    </select>
    <label class="input-group-text" for="devicename_[id]">{rulesetup.conditiondevice.value}</label>
    <select class="form-select" id="devicename_[id]" name="devicename_[id]">
      [devicenames]
    </select>
    <select class="form-select" name="comparator_[id]">
      [comparators]
    </select>
    <input type="text" class="form-control" name="devicevalue_[id]" value="[DEVICEVALUE]" required/>
    <button class="btn btn-outline-danger delcondition" type="button" data-row-id="[id]"><em
      class="bi bi-trash-fill"></em></button>
    <div class="invalid-feedback">{field.required.value}</div>
  </div>
</div>