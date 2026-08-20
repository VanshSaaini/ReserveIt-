import { useReveal } from '../hooks/useReveal.js'

const ITEMS = [
  {
    title: 'JWT authentication',
    body: 'Every session is verified by token, checked and validated on each request.',
    path: 'M12 2l8 4v6c0 5-3.4 8.4-8 10-4.6-1.6-8-5-8-10V6l8-4z'
  },
  {
    title: 'Password hashing',
    body: 'Credentials are never stored in plain text, on any account, on any role.',
    path: null,
    custom: (
      <>
        <rect x="4" y="10" width="16" height="10" rx="2" />
        <path d="M8 10V7a4 4 0 018 0v3" />
      </>
    )
  },
  {
    title: 'Role-based access',
    body: 'A doctor, clinic admin, and super admin each reach only what their role permits.',
    custom: (
      <>
        <circle cx="12" cy="8" r="4" />
        <path d="M4 21c0-4.4 3.6-7 8-7s8 2.6 8 7" />
      </>
    )
  },
  {
    title: 'Protected endpoints',
    body: 'API access is gated by authorization, not left open behind an assumed front-end.',
    custom: (
      <>
        <path d="M12 3l7 3v6c0 4.5-3 7.5-7 9-4-1.5-7-4.5-7-9V6l7-3z" />
        <path d="M9 12l2 2 4-4" />
      </>
    )
  },
  {
    title: 'Session validation',
    body: 'Tokens are checked for validity on every call, not only at sign-in.',
    custom: (
      <>
        <circle cx="12" cy="12" r="9" />
        <path d="M12 7v5l3 3" />
      </>
    )
  },
  {
    title: 'Account control',
    body: 'Accounts can be activated or deactivated without deleting their history.',
    custom: (
      <>
        <circle cx="12" cy="12" r="9" />
        <path d="M8 12l3 3 5-6" />
      </>
    )
  },
  {
    title: 'Scoped patient data',
    body: 'Access to patient information follows the same permission rules across every screen.',
    custom: (
      <>
        <rect x="3" y="4" width="18" height="16" rx="2" />
        <path d="M3 9h18" />
      </>
    )
  },
  {
    title: 'Clinic isolation',
    body: "One clinic's staff and records stay separate from another's, by design.",
    custom: (
      <>
        <path d="M4 19h16" />
        <path d="M6 19V9l6-5 6 5v10" />
      </>
    )
  }
]

export default function Security() {
  const headRef = useReveal()
  const gridRef = useReveal()

  return (
    <section className="section section--brand" id="security">
      <div className="wrap">
        <div className="section-head reveal" ref={headRef}>
          <span className="eyebrow" style={{ color: 'var(--accent)' }}>Handled with care</span>
          <h2>Health information deserves more than a login form</h2>
          <p>
            ReserveIt is built around who's allowed to see what, enforced at every request — not just at the
            screen you happen to be looking at.
          </p>
        </div>

        <div className="security-grid reveal" ref={gridRef}>
          {ITEMS.map((item) => (
            <div className="security-item" key={item.title}>
              <h4>
                <svg viewBox="0 0 24 24">
                  {item.custom ?? <path d={item.path} />}
                </svg>
                {item.title}
              </h4>
              <p>{item.body}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
