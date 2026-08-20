# ReserveIt — Frontend (React + Vite)

This is the React + Vite frontend for ReserveIt, converted from the original
Thymeleaf templates (`index.html`, `login.html`, `register.html`) that shipped
with the Spring Boot monolith. It's meant to run as a **separate frontend**
talking to the Spring Boot backend over REST, the same pattern used in your
other projects.

## Structure

```
src/
  api/client.js       fetch wrapper + authApi (login/register)
  components/         Header, Footer, ScheduleCard, and each home-page section
  hooks/useReveal.js  reveal-on-scroll (IntersectionObserver), reduced-motion aware
  pages/
    Home.jsx           the marketing/product page (was index.html)
    Login.jsx           was login.html
    Register.jsx        was register.html — patient/clinic toggle included
  styles/global.css   unchanged design tokens + component styles from style.css
```

## Getting started

```bash
npm install
npm run dev
```

The dev server runs on `http://localhost:5173` and proxies any request to
`/api/*` to `http://localhost:8080` (see `vite.config.js`), so you can run
the Spring Boot backend locally on its default port and call `fetch('/api/...')`
from the frontend without CORS configuration.

For a deployed backend, set `VITE_API_BASE_URL` in a `.env` file (see
`.env.example`).

## Backend endpoints this expects

`src/api/client.js` calls:

- `POST /api/auth/login` — body `{ username, password }`, expects a JSON
  response containing a `token` field (JWT) on success.
- `POST /api/auth/register` — body is the full register form (`accountType`,
  `firstName`, `lastName`, `email`, `mobile`, plus `dob` for patients or
  `clinicName`/`clinicAddress` for clinics, `password`).

Neither endpoint exists in the backend yet — you'll need a REST
`AuthController` (and a `POST /register` / `POST /login` mapping) since the
old Thymeleaf version relied on Spring Security's form-login flow, which
doesn't apply to a decoupled SPA. Once you're serving the API from React,
you can also remove `SecurityConfig`'s `.formLogin()` / `loginPage()` setup
and switch to a stateless JWT filter, since there's no server-rendered login
page to redirect to anymore.

## What changed from the Thymeleaf version

- `th:href="@{/...}"` → React Router `<Link to="...">`
- `th:if="${param.error}"` → component state (`error` from the failed fetch)
- Vanilla JS (`nav-toggle`, `role-tab`, `account-toggle`, reveal-on-scroll) →
  React `useState` / a small `useReveal` hook
- CSS is untouched — same custom properties, same class names, same
  responsive breakpoints
