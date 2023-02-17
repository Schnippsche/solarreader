<div id="[id]">
  <div class="row">
    <div class="col-sm-3 col md-2 col-lg-2 mb-2 comparator">
      <select class="form-select" name="compare_[id]" id="compare_[id]">
        [comparelist]
      </select>
    </div>
  </div>
  <!--  condition period -->
  <div class="input-group has-validation mb-2">
    <label for="timefrom_[id]" class="input-group-text">{rulesetup.conditionperiod.from}</label>
    <input type="time" step="1" class="form-control" id="timefrom_[id]" name="timefrom_[id]" value="[TIMEFROM]" required/>
    <label for="timeto_[id]" class="input-group-text">{rulesetup.conditionperiod.to}</label>
    <input type="time" step="1" class="form-control" id="timeto_[id]" name="timeto_[id]" value="[TIMETO]" required/>
    <span class="input-group-text">{rulesetup.conditionperiod.clock}</span>
    <button class="btn btn-outline-danger delcondition" type="button" data-row-id="[id]"><em
      class="bi bi-trash-fill"></em></button>
    <div class="invalid-feedback">{field.required.value}</div>
  </div>
</div>