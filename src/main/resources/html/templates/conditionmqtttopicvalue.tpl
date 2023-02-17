<div id="[id]">
  <!--  condition mqtt topic and mqtt value  -->
  <div class="row">
    <div class="col-sm-3 col md-2 col-lg-2 mb-2 comparator">
      <select class="form-select" name="compare_[id]" id="compare_[id]">
        [comparelist]
      </select>
    </div>
  </div>
  <div class="input-group mb-2 has-validation">
    <label for="topic_[id]" class="input-group-text">{rulesetup.conditionmqtt.topic}</label>
    <input type="text" class="form-control" id="topic_[id]" name="topic_[id]" value="[TOPIC]" required/>
    <label for="mqttbroker_[id]"  class="input-group-text">{rulesetup.conditionmqtt.broker}</label>
    <select class="form-select" id="mqttbroker_[id]"  name="mqttbroker_[id]">
      [mqttlist]
    </select>
    <label for="comparator_[id]" class="input-group-text">{rulesetup.conditionmqtt.value}</label>
    <select class="form-select" id="comparator_[id]" name="comparator_[id]">
      [comparators]
    </select>
    <input type="text" class="form-control" name="payload_[id]" value="[PAYLOAD]" required/>
    <button class="btn btn-outline-danger delcondition" type="button" data-row-id="[id]"><em
      class="bi bi-trash-fill"></em></button>
    <div class="invalid-feedback">{field.required.value}</div>
  </div>
</div>