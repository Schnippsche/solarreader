<nav class="sb-topnav navbar navbar-expand navbar-dark bg-dark" aria-label="main nav">
  <!-- Navbar Brand-->
  <a class="navbar-brand ps-3" href="/">Solarreader [version]</a>
  <!-- Sidebar Toggle-->
  <button class="btn btn-link btn-sm order-1 order-lg-0 me-4 me-lg-0" id="sidebarToggle"><em
      class="bi bi-list"></em></button>
  <!-- Navbar Search-->
  <div class="d-none d-md-inline-block form-inline ms-auto me-0 me-md-3 my-2 my-md-0"></div>
  <!-- Navbar-->
  <ul class="navbar-nav ms-auto ms-md-0 me-3 me-lg-4">
    <li class="nav-item dropdown">
      <a class="nav-link dropdown-toggle" id="navbarDropdown" href="#" role="button" data-bs-toggle="dropdown"
         aria-expanded="false"><em class="bi bi-flag"></em> {app.language.title}</a>
      <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="navbarDropdown">
        <li>
          <form method="post"><input type="hidden" name="language" value="de"/>
            <button type="submit" class="dropdown-item"><img src="img/flag_german.png" class="me-1"/>{app.language.german}
            </button>
          </form>
        </li>
        <li>
          <form method="post"><input type="hidden" name="language" value="en"/>
            <button type="submit" class="dropdown-item"><img src="img/flag_english.png" class="me-1"/>{app.language.english}
            </button>
          </form>
        </li>
        <li>
          <form method="post"><input type="hidden" name="language" value="fr"/>
            <button type="submit" class="dropdown-item"><img src="img/flag_french.png" class="me-1"/>{app.language.french}
            </button>
          </form>
        </li>
        <li>
          <form method="post"><input type="hidden" name="language" value="es"/>
            <button type="submit" class="dropdown-item"><img src="img/flag_spanish.png" class="me-1"/>{app.language.spanish}
            </button>
          </form>
        </li>
        <li>
          <form method="post"><input type="hidden" name="language" value="it"/>
            <button type="submit" class="dropdown-item"><img src="img/flag_italian.png" class="me-1"/>{app.language.italian}
            </button>
          </form>
        </li>
        <li>
          <form method="post"><input type="hidden" name="language" value="pt"/>
            <button type="submit" class="dropdown-item"><img src="img/flag_portugal.png" class="me-1"/>{app.language.portuguese}
            </button>
          </form>
        </li>
      </ul>
    </li>
  </ul>
</nav>
