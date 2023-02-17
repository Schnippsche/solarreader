<div id="[id]">
  <div class="input-group mb-2 has-validation">
    <label class="input-group-text" for="mqttbroker_[id]">{rulesetup.action.mqtt.sendto}</label>
    <select class="form-select" id="mqttbroker_[id]" name="mqttbroker_[id]">
      [mqttlist]
    </select>
    <label class="input-group-text" for="topicname_[id]">{rulesetup.action.mqtt.topic}</label>
    <input type="text" class="form-control" id="topicname_[id]" name="topicname_[id]" value="[TOPIC]" required/>
    <label class="input-group-text" for="payload_[id]">{rulesetup.action.mqtt.topic.value}</label>
    <input type="text" class="form-control" id="payload_[id]" name="payload_[id]" value="[PAYLOAD]" required/>
    <button class="btn btn-outline-danger delaction" type="button" data-row-id="[id]"><em
      class="bi bi-trash-fill"></em></button>
    <div class="invalid-feedback">{field.required.value}</div>
  </div>
</div>