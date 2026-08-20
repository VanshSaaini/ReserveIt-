import { Link } from 'react-router-dom'
import { useReveal } from '../hooks/useReveal.js'
import ScheduleCard from './ScheduleCard.jsx'

export default function Hero() {
  const revealRef = useReveal()

  return (
    <section className="hero">
      <div className="hero-grid" aria-hidden="true"></div>
      <div className="wrap">
        <div className="hero-copy">
          <span className="eyebrow">Clinic management, one platform</span>
          <h1>Every open slot, in <em>one</em> place — for every clinic you run.</h1>
          <p className="lede">
            ReserveIt replaces the call register, the shared spreadsheet, and the sticky notes with a
            single system patients, doctors, and clinic staff all read from — so a booking made at
            reception matches the one a patient sees online.
          </p>

          <div className="hero-actions">
            <Link className="btn btn--primary" to="/register">Register your clinic</Link>
            <a className="btn btn--ghost" href="#workflow">See how booking works</a>
          </div>
          <p className="hero-note">Free to set up. No card required for a single clinic.</p>

          <div className="hero-roles">
            <div><strong>4</strong>role types</div>
            <div><strong>JWT</strong>secured sessions</div>
            <div><strong>Live</strong>slot availability</div>
          </div>
        </div>

        <div className="hero-visual reveal" ref={revealRef}>
          <ScheduleCard />
        </div>
      </div>
    </section>
  )
}
