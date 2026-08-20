import { Link } from 'react-router-dom'
import BrandMark from './BrandMark.jsx'

export default function Footer() {
  return (
    <footer className="site-footer">
      <div className="wrap">
        <div className="footer-top">
          <div className="footer-brand">
            <Link className="brand" to="/">
              <BrandMark />
              ReserveIt
            </Link>
            <p>A single scheduling platform for patients, doctors, and the clinics that bring them together.</p>
          </div>

          <div className="footer-col">
            <h5>Product</h5>
            <ul>
              <li><a href="#roles">For patients</a></li>
              <li><a href="#roles">For doctors</a></li>
              <li><a href="#roles">For clinics</a></li>
              <li><a href="#security">Security</a></li>
            </ul>
          </div>

          <div className="footer-col">
            <h5>Account</h5>
            <ul>
              <li><Link to="/register">Register a clinic</Link></li>
              <li><Link to="/login">Log in</Link></li>
            </ul>
          </div>

          <div className="footer-col">
            <h5>Platform</h5>
            <ul>
              <li><a href="#workflow">How booking works</a></li>
              <li><a href="#stack">Tech stack</a></li>
            </ul>
          </div>
        </div>

        <div className="footer-bottom">
          <span>© 2026 ReserveIt. All rights reserved.</span>
          <span>Built for clinics that would rather practice medicine than manage a spreadsheet.</span>
        </div>
      </div>
    </footer>
  )
}
