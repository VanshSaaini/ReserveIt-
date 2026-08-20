import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import BrandMark from "../components/BrandMark.jsx";
import { authApi } from "../api/client.js";

export default function Login() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    username: "",
    password: "",
    rememberMe: false,
  });
  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  function updateField(field) {
    return (e) => {
      const value =
        e.target.type === "checkbox" ? e.target.checked : e.target.value;
      setForm((prev) => ({ ...prev, [field]: value }));
    };
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setSubmitting(true);
    try {
      const data = await authApi.login({
        username: form.username,
        password: form.password,
        rememberMe: form.rememberMe,
      });
      // Adjust to match whatever shape your backend returns.
      if (data?.token) {
        localStorage.setItem("reserveit_token", data.token);
      }
      const role = data?.role || "PATIENT";
      const target =
        role === "CLINIC_ADMIN"
          ? "/clinic"
          : role === "DOCTOR"
            ? "/doctor"
            : role === "SUPER_ADMIN"
              ? "/admin"
              : "/patient";
      navigate(target);
    } catch (err) {
      setError(
        err.message ||
          "That email and password combination doesn't match our records.",
      );
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="auth-shell">
      {/* ===================== Brand panel ===================== */}
      <aside className="auth-aside">
        <div className="hero-grid" aria-hidden="true"></div>

        <div className="auth-aside__top">
          <Link className="brand" to="/">
            <BrandMark />
            ReserveIt
          </Link>
        </div>

        <div className="auth-aside__body">
          <span className="eyebrow" style={{ color: "var(--accent)" }}>
            Welcome back
          </span>
          <h2>Pick up where your clinic left off.</h2>
          <p>
            Today's schedule, every doctor's availability, and every patient
            record — all where you left them.
          </p>
        </div>

        <div className="auth-aside__foot">
          <div>
            <strong>4</strong>role types
          </div>
          <div>
            <strong>JWT</strong>secured sessions
          </div>
          <div>
            <strong>Live</strong>slot availability
          </div>
        </div>
      </aside>

      {/* ===================== Form panel ===================== */}
      <main className="auth-main">
        <div className="auth-card">
          <div className="auth-card__top">
            <Link className="brand" to="/">
              <BrandMark />
              ReserveIt
            </Link>
          </div>

          <h1>Log in</h1>
          <p className="auth-sub">
            Enter your details to reach your dashboard.
          </p>

          {error && <div className="form-alert form-alert--error">{error}</div>}

          <form className="auth-form" onSubmit={handleSubmit}>
            <div className="form-field">
              <label htmlFor="username">Email address</label>
              <input
                type="email"
                id="username"
                name="username"
                autoComplete="email"
                placeholder="you@clinic.com"
                value={form.username}
                onChange={updateField("username")}
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="password">Password</label>
              <input
                type="password"
                id="password"
                name="password"
                autoComplete="current-password"
                placeholder="••••••••"
                value={form.password}
                onChange={updateField("password")}
                required
              />
            </div>

            <div className="form-between">
              <label className="form-check">
                <input
                  type="checkbox"
                  name="rememberMe"
                  checked={form.rememberMe}
                  onChange={updateField("rememberMe")}
                />
                Keep me signed in
              </label>
              <a className="form-link" href="#">
                Forgot password?
              </a>
            </div>

            <button
              type="submit"
              className="btn btn--primary"
              disabled={submitting}
            >
              {submitting ? "Logging in…" : "Log in"}
            </button>
          </form>

          <p className="auth-foot">
            New to ReserveIt?{" "}
            <Link className="form-link" to="/register">
              Create an account
            </Link>
          </p>
        </div>
      </main>
    </div>
  );
}
