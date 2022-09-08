<div class="modal fade showmodal" data-bs-backdrop="static" data-bs-keyboard="false" tabindex="-1">
  <div class="modal-dialog modal-dialog-centered">
    <div class="modal-content">
      <div class="modal-header bg-danger text-white">
        <h5 class="modal-title">{confirm.delete.header.title}</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"
                aria-label="Close"></button>
      </div>
      <div class="modal-body">
        <p>{confirm.deletedatabase.title}</p>
        <table>
          <tr>
            <td><b>{databasesetup.dbtitle.text}: </b></td>
            <td>[description]</td>
          </tr>
          <tr>
            <td><b>{databasesetup.dbhost.text}: </b></td>
            <td>[dbhost]</td>
          </tr>
          <tr>
            <td><b>{databasesetup.dbport.text}: </b></td>
            <td>[dbport]</td>
          </tr>
          <tr>
            <td><b>{databasesetup.dbname.text}: </b></td>
            <td>[dbname]</td>
          </tr>
        </table>
      </div>
      <div class="modal-footer">
        <form method="post">
          <input type="hidden" id="id_step" name="step" value="deletedatabase"/>
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
            {button.abort}
          </button>
          <button type="submit" class="btn btn-danger">{button.delete}</button>
        </form>
      </div>
    </div>
  </div>
</div>