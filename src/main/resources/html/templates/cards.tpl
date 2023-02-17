<div class="col-md-6 mb-2 mt-2">
  <div class="accordion" id="sourceaccordion">
    <div class="accordion-item">
      <div class="accordion-header" id="sourceheading">
        <button aria-controls="sourcecollapse" aria-expanded="true" class="accordion-button"
                data-bs-target="#sourcecollapse"
                data-bs-toggle="collapse" type="button">
          {card.datasource.header}
        </button>
      </div>
      <div aria-labelledby="sourceheading" class="accordion-collapse collapse show" data-bs-parent="#sourceaccordion"
           id="sourcecollapse">
        <div class="accordion-body">
          <h5 class="card-title">{card.datasource.title}</h5>
          <p class="card-text">{card.datasource.text}</p>
        </div>
        <ul class="list-group list-group-flush">
          <li class="list-group-item">
            <div class="d-flex flex-row">
              <div class="p-1 align-self-center"><img alt="" src="img/influxdata.png"/></div>
              <div class="p-1 flex-fill">
                <div class="text-secondary">{card.datasource.newdatabase.title}</div>
                <div class="small">{card.datasource.newdatabase.text}</div>
              </div>
              <div class="p-1 align-self-center">
                <form method="post">
                  <input name="step" type="hidden" value="newdatabase"/>
                  <button class="btn btn-link" type="submit">{card.datasource.new}</button>
                </form>
              </div>
            </div>
          </li>
          <li class="list-group-item">
            <div class="d-flex flex-row">
              <div class="p-1 align-self-center"><img alt="" src="img/inverter.png"/></div>
              <div class="p-1 flex-fill">
                <div class="text-secondary">{card.datasource.newdevice.title}</div>
                <div class="small">{card.datasource.newdevice.text}</div>
              </div>
              <div class="p-1 align-self-center">
                <form method="post">
                  <input name="step" type="hidden" value="newdevice"/>
                  <button class="btn btn-link" type="submit">{card.datasource.new}</button>
                </form>
              </div>
            </div>
          </li>
          <li class="list-group-item">
            <div class="d-flex flex-row">
              <div class="p-1 align-self-center"><img alt="" src="img/mqtt.png"/></div>
              <div class="p-1 flex-fill">
                <div class="text-secondary">{card.datasource.newmqtt.title}</div>
                <div class="small">{card.datasource.newmqtt.text}</div>
              </div>
              <div class="p-1 align-self-center">
                <form method="post">
                  <input name="step" type="hidden" value="newmqtt"/>
                  <button class="btn btn-link" type="submit">{card.datasource.new}</button>
                </form>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</div>

<div class="col-md-6 mb-2 mt-2">
  <div class="accordion" id="serviceaccordion">
    <div class="accordion-item">
      <div class="accordion-header" id="serviceheading">
        <button aria-controls="servicecollapse" aria-expanded="true" class="accordion-button"
                data-bs-target="#servicecollapse"
                data-bs-toggle="collapse" type="button">
          {card.services.header}
        </button>
      </div>
      <div aria-labelledby="serviceheading" class="accordion-collapse collapse show" data-bs-parent="#serviecaccordion"
           id="servicecollapse">
        <div class="accordion-body">
          <h5 class="card-title">{card.services.title}</h5>
          <p class="card-text">{card.services.text}</p>
        </div>
        <ul class="list-group list-group-flush">
          <li class="list-group-item">
            <div class="d-flex flex-row">
              <div class="p-1 align-self-center"><img alt="" src="img/openweather.png"/></div>
              <div class="p-1 flex-fill">
                <div class="text-secondary">{card.services.openweather.title}</div>
                <div class="small">{card.services.openweather.text}</div>
              </div>
              <div class="p-1 align-self-center">
                <form method="post">
                  <input name="step" type="hidden" value="editopenweather"/>
                  <button class="btn btn-link" type="submit">{card.services.config}</button>
                </form>
              </div>
            </div>
          </li>
          <li class="list-group-item">
            <div class="d-flex flex-row">
              <div class="p-1 align-self-center"><img alt="" src="img/solarprognose.png"/></div>
              <div class="p-1 flex-fill">
                <div class="text-secondary">{card.services.solarprognose.title}</div>
                <div class="small">{card.services.solarprognose.text}</div>
              </div>
              <div class="p-1 align-self-center">
                <form method="post">
                  <input name="step" type="hidden" value="editsolarprognose"/>
                  <button class="btn btn-link" type="submit">{card.services.config}</button>
                </form>
              </div>
            </div>
          </li>
          <li class="list-group-item">
            <div class="d-flex flex-row">
              <div class="p-1 align-self-center"><img alt="" src="img/awattar.png"/>
              </div>
              <div class="p-1 flex-fill">
                <div class="text-secondary">{card.services.awattar.title}</div>
                <div class="small">{card.services.awattar.text}</div>
              </div>
              <div class="p-1 align-self-center">
                <form method="post">
                  <input name="step" type="hidden" value="editawattar"/>
                  <button class="btn btn-link" type="submit">{card.services.config}</button>
                </form>
              </div>
            </div>
          </li>
        </ul>
      </div>
    </div>
  </div>
</div>

<div class="col-md-12 mb-2 mt-2">
  <div class="accordion" id="ruleaccordion">
    <div class="accordion-item">
      <div class="accordion-header" id="ruleheading">
        <button aria-controls="rulecollapse" aria-expanded="true" class="accordion-button"
                data-bs-target="#rulecollapse"
                data-bs-toggle="collapse" type="button">
          {card.rules.header}
        </button>
      </div>
      <div aria-labelledby="ruleheading" class="accordion-collapse collapse show" data-bs-parent="#rulecaccordion"
           id="rulecollapse">
        <div class="accordion-body">
          <h5 class="card-title">{card.rules.title}</h5>
        </div>
        <ul class="list-group list-group-flush">
          <li class="list-group-item">
            <div class="d-flex flex-row">
              <div class="p-1 align-self-center"><img alt="" src="img/rules.png"/></div>
              <div class="p-1 flex-fill align-self-center">
                <div class="text-secondary">{card.rules.text}</div>
              </div>
              <div class="p-1 align-self-center">
                <form method="post">
                  <input name="step" type="hidden" value="newrule"/>
                  <button class="btn btn-link" type="submit">{card.rules.config}</button>
                </form>
              </div>
            </div>
          </li>
          <li class="list-group-item">
            <table class="table" id="id_rules_table">
              <thead>
              <tr>
                <td class="w-100">{card.rules.table.rulescol.title}</td>
                <td>{card.status.table.statuscol.title}</td>
                <td>{card.status.table.activitycol.title}</td>
              </tr>
              </thead>
              <tbody>
              </tbody>
            </table>
          </li>
        </ul>
      </div>
    </div>
  </div>
</div>

<div class="col-md-12 mb-2 mt-2">
  <div class="accordion" id="statusaccordion">
    <div class="accordion-item">
      <div class="accordion-header" id="statusheading">
        <button aria-controls="statuscollapse" aria-expanded="true" class="accordion-button"
                data-bs-target="#statuscollapse"
                data-bs-toggle="collapse" type="button">
          {card.status.header}
        </button>
      </div>
      <div aria-labelledby="statusheading" class="accordion-collapse collapse show" data-bs-parent="#statusaccordion"
           id="statuscollapse">
        <div class="accordion-body">
          <table class="table" id="id_status_table">
            <thead>
            <tr>
              <td class="w-100">{card.status.table.elemcol.title}</td>
              <td>{card.status.table.statuscol.title}</td>
              <td>{card.status.table.activitycol.title}</td>
              <td>{card.status.table.infocol.title}</td>
            </tr>
            </thead>
            <tbody>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>
<div class="col-md-12 mb-2 mt-2">
  <div class="accordion" id="logaccordion">
    <div class="accordion-item">
      <div class="accordion-header" id="logheading">
        <button aria-controls="logcollapse" aria-expanded="false" class="accordion-button collapsed"
                data-bs-target="#logcollapse"
                data-bs-toggle="collapse" type="button">
          {card.log.header}
        </button>
      </div>
      <div aria-labelledby="logheading" class="accordion-collapse collapse" data-bs-parent="#logaccordion"
           id="logcollapse">
        <div class="accordion-body">
          <ul class="nav">
            <li class="nav-item">
              <select class="form-select" id="id_logentries">
                <option value="50">50 {card.log.rowselect.rows}</option>
                <option value="100">100 {card.log.rowselect.rows}</option>
                <option value="200">200 {card.log.rowselect.rows}</option>
                <option value="500">500 {card.log.rowselect.rows}</option>
                <option value="1000">1000 {card.log.rowselect.rows}</option>
              </select>
            </li>
            <li class="nav-item">
              <button class="btn" data-bs-placement="top" data-bs-toggle="tooltip"
                      id="id_btn_log_refresh" title="{card.log.button.refresh.title}">
                <em class="bi bi-arrow-clockwise"></em>
              </button>
            </li>
            <li class="nav-item">
              <button class="btn" data-bs-placement="top" data-bs-toggle="tooltip"
                      id="id_btn_log_download" title="{card.log.button.download.title}">
                <em class="bi bi-download"></em>
              </button>
            </li>
          </ul>
          <pre class="mt-2 font-monospace bg-dark text-white logblock" id="id_log_content"></pre>
        </div>
      </div>
    </div>
  </div>
</div>
<div class="col-md-6 mb-2 mt-2">
  <div class="accordion" id="diskspaceaccordion">
    <div class="accordion-item">
      <div class="accordion-header" id="diskspaceheading">
        <button aria-controls="diskspacecollapse" aria-expanded="true" class="accordion-button"
                data-bs-target="#diskspacecollapse"
                data-bs-toggle="collapse" type="button">
          {card.diskspaces.header}
        </button>
      </div>
      <div aria-labelledby="diskspaceheading" class="accordion-collapse collapse show"
           data-bs-parent="#diskspaceaccordion"
           id="diskspacecollapse">
        <div class="accordion-body">
          <div class="row">
            <canvas id="canvasdiskspace"></canvas>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
<div class="col-md-6 mb-2 mt-2">
  <div class="accordion" id="memoryspaceaccordion">
    <div class="accordion-item">
      <div class="accordion-header" id="memoryspaceheading">
        <button aria-controls="memoryspacecollapse" aria-expanded="true" class="accordion-button"
                data-bs-target="#memoryspacecollapse"
                data-bs-toggle="collapse" type="button">
          {card.memoryspaces.header}
        </button>
      </div>
      <div aria-labelledby="memoryspaceheading" class="accordion-collapse collapse show"
           data-bs-parent="#memoryspaceaccordion"
           id="memoryspacecollapse">
        <div class="accordion-body">
          <div class="row">
            <canvas id="canvasmemoryspace"></canvas>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>




