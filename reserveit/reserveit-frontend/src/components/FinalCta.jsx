import { Link } from 'react-router-dom'
import { useReveal } from '../hooks/useReveal.js'

export default function FinalCta() {
  const ref = useReveal()

  return (
    <section className="final-cta">
      <div className="wrap reveal" ref={ref}>
        <span className="eyebrow" style={{ justifyContent: 'center' }}>Ready when you are</span>
        <h2>Bring your clinic's schedule onto one platform</h2>
        <p>Set up your clinic, add your doctors, and start taking bookings the same day.</p>
        <div className="hero-actions">
          <Link className="btn btn--primary" to="/register">Register your clinic</Link>
          <Link className="btn btn--ghost" to="/login">I already have an account</Link>
        </div>
      </div>
    </section>
  )
}
