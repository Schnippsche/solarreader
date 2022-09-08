<div class="row mb-3" id="id_divhf2211">
  <div class="col-md-12">
    <fieldset class="form-group border p-3">
      {devicesetup.hf2211.label}
      <div class="row mb-2 mt-2">
        <div class="form-floating">
          <div class="form-check form-switch">
            <input class="form-check-input" type="checkbox" role="switch" [hf2211enabled]
                   name="usehf2211" id="id_devicesetup_hf2211_enabled"/>
            <label class="form-check-label" for="id_devicesetup_hf2211_enabled"
                   data-bs-toggle="tooltip"
                   data-bs-placement="bottom"
                   title="{devicesetup.hf2211.tooltip}">{devicesetup.hf2211.title}</label>
          </div>
        </div>
      </div>
      <div class="row d-none" id="id_hf2211_row">
        <div class="col-md-6 mb-2">
          <div class="form-floating has-validation has-feedback">
            <input type="text" class="form-control" id="id_devicesetup_hf2211_ip"
                   name="hf2211ip"
                   value="[HF2211IP]"
                   placeholder="{devicesetup.hf2211.ip.text}"
                   data-bs-toggle="tooltip" data-bs-placement="bottom"
                   title="{devicesetup.hf2211.ip.tooltip}"/>
            <label for="id_devicesetup_hf2211_ip">{devicesetup.hf2211.ip.text}</label>
            <div class="invalid-feedback">{field.required.value}</div>
          </div>
        </div>
        <div class="col-md-6">
          <div class="form-floating has-validation has-feedback">
            <input type="number" class="form-control" id="id_devicesetup_hf2211_port"
                   name="hf2211port"
                   value="[HF2211PORT]"
                   placeholder="{devicesetup.hf2211.port.text}"
                   data-bs-toggle="tooltip" data-bs-placement="bottom"
                   title="{devicesetup.hf2211.port.tooltip}"/>
            <label for="id_devicesetup_hf2211_port">{devicesetup.hf2211.port.text}</label>
            <div class="invalid-feedback">{field.onlynumbers}</div>
          </div>
        </div>
      </div>
    </fieldset>
  </div>
</div>