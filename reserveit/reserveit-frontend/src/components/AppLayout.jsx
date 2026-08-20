import { NavLink, Link } from 'react-router-dom'
import BrandMark from './BrandMark.jsx'
import { getAuth, logout } from '../api/client.js'

const menus={
  PATIENT:[
    ['/patient','Overview'],['/patient/book','Find a clinic'],['/patient/appointments','Appointments'],['/patient/profile','My profile']
  ],
  CLINIC_ADMIN:[
    ['/clinic','Overview'],['/clinic/doctors','Doctors'],['/clinic/services','Services'],['/clinic/appointments','Appointments'],['/clinic/settings','Clinic settings']
  ],
  DOCTOR:[
    ['/doctor','Overview'],['/doctor/appointments','My schedule'],['/doctor/availability','Availability'],['/doctor/profile','My profile']
  ],
  SUPER_ADMIN:[
    ['/admin','Overview'],['/admin/users','Users'],['/admin/clinics','Clinics']
  ]
}
export default function AppLayout({children,title='Dashboard',subtitle=''}) {
  const auth=getAuth() || {}
  const role=auth.role || 'PATIENT'
  const menu=menus[role] || menus.PATIENT
  return <div className="app-shell">
    <aside className="app-sidebar">
      <Link className="brand app-brand" to="/"><BrandMark/>ReserveIt</Link>
      <div className="profile-mini">
        <div className="avatar">{(auth.firstName||auth.email||'U')[0].toUpperCase()}</div>
        <div><strong>{[auth.firstName,auth.lastName].filter(Boolean).join(' ')||auth.email}</strong><span>{role.replace('_',' ')}</span></div>
      </div>
      <nav className="side-nav">{menu.map(([to,label])=><NavLink key={to} end={to.split('/').length===2} to={to}>{label}</NavLink>)}</nav>
      <button className="side-logout" onClick={logout}>Log out</button>
    </aside>
    <main className="app-main">
      <header className="app-topbar"><div><h1>{title}</h1>{subtitle&&<p>{subtitle}</p>}</div><Link className="btn btn--primary btn--sm" to="/">Public site</Link></header>
      <div className="app-content">{children}</div>
    </main>
  </div>
}
export function StatCard({label,value,meta}){return <div className="stat-card"><span>{label}</span><strong>{value}</strong>{meta&&<small>{meta}</small>}</div>}
export function Empty({children='Nothing here yet.'}){return <div className="empty-state">{children}</div>}
export function Loading(){return <div className="loading">Loading…</div>}
export function ErrorBox({message}){return message?<div className="form-alert form-alert--error">{message}</div>:null}
