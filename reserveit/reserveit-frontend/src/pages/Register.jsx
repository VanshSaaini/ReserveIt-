import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import BrandMark from '../components/BrandMark.jsx'
import { authApi } from '../api/client.js'

const INITIAL_FORM = {
  accountType: 'patient',
  firstName: '',
  lastName: '',
  email: '',
  mobile: '',
  dob: '',
  clinicName: '',
  clinicAddress: '',
  password: '',
  confirmPassword: '',
  agree: false
}

export default function Register() {
  const navigate = useNavigate()
  const [form, setForm] = useState(INITIAL_FORM)
  const [error, setError] = useState('')
  const [submitting, setSubmitting] = useState(false)

  function updateField(field) {
    return (e) => {
      const value = e.target.type === 'checkbox' ? e.target.checked : e.target.value
      setForm((prev) => ({ ...prev, [field]: value }))
    }
  }

  function setAccountType(type) {
    setForm((prev) => ({ ...prev, accountType: type }))
  }

  async function handleSubmit(e) {
    e.preventDefault()
    setError('')

    if (form.password !== form.confirmPassword) {
      setError('Passwords do not match.')
      return
    }

    setSubmitting(true)
    try {
      await authApi.register(form)
      navigate('/login')
    } catch (err) {
      setError(err.message || "We couldn't create that account. Check the details below and try again.")
    } finally {
      setSubmitting(false)
    }
  }

  const isClinic = form.accountType === 'clinic'

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
          <span className="eyebrow" style={{ color: 'var(--accent)' }}>Get started</span>
          <h2>Set up in minutes, book on day one.</h2>
          <p>Whether you're booking your own visits or running a clinic's whole schedule, your account gets you straight to the parts you'll actually use.</p>
        </div>

        <div className="auth-aside__foot">
          <div><strong>Free</strong>for a single clinic</div>
          <div><strong>No</strong>card required</div>
          <div><strong>5 min</strong>average setup</div>
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

          <h1>Create your account</h1>
          <p className="auth-sub">Tell us who you are, and we'll set the right kind of account.</p>

          <div className="account-toggle" role="tablist" aria-label="Account type">
            <button
              type="button"
              className={!isClinic ? 'is-active' : ''}
              role="tab"
              aria-selected={!isClinic}
              onClick={() => setAccountType('patient')}
            >
              I'm a patient
            </button>
            <button
              type="button"
              className={isClinic ? 'is-active' : ''}
              role="tab"
              aria-selected={isClinic}
              onClick={() => setAccountType('clinic')}
            >
              I'm registering a clinic
            </button>
          </div>

          {error && <div className="form-alert form-alert--error">{error}</div>}

          <form className="auth-form" onSubmit={handleSubmit}>
            <div className="form-row">
              <div className="form-field">
                <label htmlFor="firstName">First name</label>
                <input
                  type="text"
                  id="firstName"
                  autoComplete="given-name"
                  placeholder="Aditi"
                  value={form.firstName}
                  onChange={updateField('firstName')}
                  required
                />
              </div>
              <div className="form-field">
                <label htmlFor="lastName">Last name</label>
                <input
                  type="text"
                  id="lastName"
                  autoComplete="family-name"
                  placeholder="Rao"
                  value={form.lastName}
                  onChange={updateField('lastName')}
                  required
                />
              </div>
            </div>

            <div className="form-field">
              <label htmlFor="email">Email address</label>
              <input
                type="email"
                id="email"
                autoComplete="email"
                placeholder="you@example.com"
                value={form.email}
                onChange={updateField('email')}
                required
              />
            </div>

            <div className="form-field">
              <label htmlFor="mobile">Mobile number</label>
              <input
                type="tel"
                id="mobile"
                autoComplete="tel"
                placeholder="+91 98765 43210"
                value={form.mobile}
                onChange={updateField('mobile')}
                required
              />
            </div>

            {!isClinic && (
              <div className="role-fields is-active">
                <div className="form-field">
                  <label htmlFor="dob">Date of birth</label>
                  <input
                    type="date"
                    id="dob"
                    autoComplete="bday"
                    value={form.dob}
                    onChange={updateField('dob')}
                  />
                </div>
              </div>
            )}

            {isClinic && (
              <div className="role-fields is-active">
                <div className="form-field">
                  <label htmlFor="clinicName">Clinic name</label>
                  <input
                    type="text"
                    id="clinicName"
                    placeholder="Cascade Family Clinic"
                    value={form.clinicName}
                    onChange={updateField('clinicName')}
                    required
                  />
                </div>
                <div className="form-field">
                  <label htmlFor="clinicAddress">Clinic address</label>
                  <input
                    type="text"
                    id="clinicAddress"
                    placeholder="Street, city, state"
                    value={form.clinicAddress}
                    onChange={updateField('clinicAddress')}
                    required
                  />
                  <span className="field-hint">You can add specific services and doctors after setup.</span>
                </div>
              </div>
            )}

            <div className="form-row">
              <div className="form-field">
                <label htmlFor="password">Password</label>
                <input
                  type="password"
                  id="password"
                  autoComplete="new-password"
                  placeholder="••••••••"
                  value={form.password}
                  onChange={updateField('password')}
                  required
                />
              </div>
              <div className="form-field">
                <label htmlFor="confirmPassword">Confirm password</label>
                <input
                  type="password"
                  id="confirmPassword"
                  autoComplete="new-password"
                  placeholder="••••••••"
                  value={form.confirmPassword}
                  onChange={updateField('confirmPassword')}
                  required
                />
              </div>
            </div>

            <label className="form-check" style={{ margin: '-0.3rem 0 1.4rem' }}>
              <input
                type="checkbox"
                checked={form.agree}
                onChange={updateField('agree')}
                required
              />
              I agree to the terms of service and privacy policy
            </label>

            <button type="submit" className="btn btn--primary" disabled={submitting}>
              {submitting
                ? 'Creating account…'
                : isClinic ? 'Create clinic account' : 'Create patient account'}
            </button>
          </form>

          <p className="auth-foot">
            Already have an account?{' '}
            <Link className="form-link" to="/login">Log in</Link>
          </p>
        </div>
      </main>
    </div>
  )
}
