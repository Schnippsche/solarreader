<div class="col-md-12 mb-2 mt-2">
  <div class="accordion " id="warnaccordion">
    <div class="accordion-item">
      <div class="accordion-header" id="warnheading">
        <button aria-controls="warncollapse" aria-expanded="true" class="accordion-button" data-bs-target="#warncollapse"
                data-bs-toggle="collapse" type="button">
          <em class="bi bi-exclamation-triangle me-1"></em>
          {card.warnings.header}
        </button>
      </div>
      <div aria-labelledby=warnheading" class="accordion-collapse show" data-bs-parent="#warnaccordion"
           id="warncollapse">
        <div class="accordion-body">
          <ul class="list-group">
            [warnings]
          </ul>
        </div>
      </div>
    </div>
  </div>
</div>