<nav aria-label="main nav" class="sb-topnav navbar navbar-expand navbar-dark bg-dark">
  <!-- Navbar Brand-->
  <a class="navbar-brand ps-3" href="/">Solarreader [version]</a>
  <!-- Sidebar Toggle-->
  <button class="btn btn-link btn-sm order-1 order-lg-0 me-4 me-lg-0" id="sidebarToggle"><em
    class="bi bi-list"></em></button>
  <!-- Navbar Search-->
  <div class="ms-auto"><a class="link-info [newrelease] ms-lg-2" href="https://github.com/Schnippsche/solarreader/releases">{app.updateavailable}</a></div>
  <!-- Navbar-->
  <form method="post"><input name="step" type="hidden" value="editprofile"/>
    <button class="btn btn-dark" title="{app.profile.title}"><em class="bi bi-person"></em></button>
  </form>
  <ul class="navbar-nav ms-auto ms-md-0 me-3 me-lg-4">
    <li class="nav-item dropdown">
      <a aria-expanded="false" class="nav-link dropdown-toggle" data-bs-toggle="dropdown" href="#" id="navbarDropdown"
         role="button"><em class="bi bi-flag"></em> {app.language.title}</a>
      <ul aria-labelledby="navbarDropdown" class="dropdown-menu dropdown-menu-end">
        <li>
          <form method="post"><input name="language" type="hidden" value="de"/>
            <button class="dropdown-item" type="submit"><img class="me-1" src="img/flag_german.png"/>{app.language.german}
            </button>
          </form>
        </li>
        <li>
          <form method="post"><input name="language" type="hidden" value="en"/>
            <button class="dropdown-item" type="submit"><img class="me-1" src="img/flag_english.png"/>{app.language.english}
            </button>
          </form>
        </li>
        <li>
          <form method="post"><input name="language" type="hidden" value="fr"/>
            <button class="dropdown-item" type="submit"><img class="me-1" src="img/flag_french.png"/>{app.language.french}
            </button>
          </form>
        </li>
        <li>
          <form method="post"><input name="language" type="hidden" value="es"/>
            <button class="dropdown-item" type="submit"><img class="me-1" src="img/flag_spanish.png"/>{app.language.spanish}
            </button>
          </form>
        </li>
        <li>
          <form method="post"><input name="language" type="hidden" value="it"/>
            <button class="dropdown-item" type="submit"><img class="me-1" src="img/flag_italian.png"/>{app.language.italian}
            </button>
          </form>
        </li>
        <li>
          <form method="post"><input name="language" type="hidden" value="pt"/>
            <button class="dropdown-item" type="submit"><img class="me-1" src="img/flag_portugal.png"/>{app.language.portuguese}
            </button>
          </form>
        </li>
      </ul>
    </li>
  </ul>
</nav>
