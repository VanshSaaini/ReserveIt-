import { useState } from 'react'
import { Link } from 'react-router-dom'
import BrandMark from './BrandMark.jsx'

export default function Header() {
  const [menuOpen, setMenuOpen] = useState(false)

  return (
    <header className="site-header">
      <div className="wrap navbar">
        <Link className="brand" to="/">
          <BrandMark />
          ReserveIt
        </Link>

        <nav className="nav-links" aria-label="Primary">
          <a href="#roles">Product</a>
          <a href="#workflow">How it works</a>
          <a href="#security">Security</a>
          <a href="#stack">Built with</a>
        </nav>

        <div className="nav-actions">
          <Link className="link-in" to="/login">Log in</Link>
          <Link className="btn btn--primary btn--sm" to="/register">Get started</Link>
          <button
            className={`nav-toggle${menuOpen ? ' is-active' : ''}`}
            aria-label="Toggle menu"
            aria-expanded={menuOpen}
            aria-controls="mobileMenu"
            onClick={() => setMenuOpen((open) => !open)}
          >
            <span className="bar"></span>
            <span className="bar"></span>
            <span className="bar"></span>
          </button>
        </div>
      </div>

      <nav
        className="nav-links"
        id="mobileMenu"
        aria-label="Mobile"
        style={{
          display: menuOpen ? 'flex' : 'none',
          flexDirection: 'column',
          padding: '1rem 1.5rem 1.5rem',
          gap: '1rem',
          borderTop: '1px solid var(--line)'
        }}
      >
        <a href="#roles" onClick={() => setMenuOpen(false)}>Product</a>
        <a href="#workflow" onClick={() => setMenuOpen(false)}>How it works</a>
        <a href="#security" onClick={() => setMenuOpen(false)}>Security</a>
        <a href="#stack" onClick={() => setMenuOpen(false)}>Built with</a>
      </nav>
    </header>
  )
}
