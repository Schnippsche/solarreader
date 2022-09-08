<div class="col-md-12 mb-2 mt-2">
  <div class="accordion " id="warnaccordion">
    <div class="accordion-item">
      <div class="accordion-header" id="warnheading">
        <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#warncollapse"
                aria-expanded="true" aria-controls="warncollapse">
          <em class="bi bi-exclamation-triangle me-1"></em>
          {card.warnings.header}
        </button>
      </div>
      <div id="warncollapse" class="accordion-collapse show" aria-labelledby=warnheading"
           data-bs-parent="#warnaccordion">
        <div class="accordion-body">
          <ul class="list-group">
            [warnings]
          </ul>
        </div>
      </div>
    </div>
  </div>
</div>