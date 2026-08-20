import { useState } from 'react'
import { useReveal } from '../hooks/useReveal.js'

const ROLES = [
  {
    key: 'patient',
    num: '01',
    label: 'Patient',
    title: 'For patients',
    intro: "Find a clinic, check who's available, and book without a phone call.",
    features: [
      ['Find a clinic', 'Search clinics by service or specialisation and see the doctors attached to each one.'],
      ['Real slot availability', "Only the time slots a doctor has actually opened are shown — no booking into a gap that isn't there."],
      ['Book in a few taps', 'Pick a doctor, a date, and a slot; the system confirms the appointment and records it immediately.'],
      ['Stay in the loop', 'Confirmation, cancellation, and reschedule notifications land by email — plus a reminder before the visit.'],
      ['Full appointment history', 'Upcoming, completed, and cancelled visits stay in one list, across every clinic used.'],
      ['One profile', 'Personal and contact details are managed in a single place and reused for every booking.']
    ]
  },
  {
    key: 'doctor',
    num: '02',
    label: 'Doctor',
    title: 'For doctors',
    intro: 'Set your own availability, then let the schedule fill itself in.',
    features: [
      ['Availability, your terms', "Define working hours and slot length so patients can only book into time you've actually opened."],
      ['Daily schedule', "A single view of who's booked in today, and when, before the first patient arrives."],
      ['Appointment status', 'Mark a visit confirmed, completed, cancelled, or rescheduled — the record updates for everyone at once.'],
      ['Patient context', "See the information relevant to an appointment, scoped to what the clinic's permissions allow."],
      ['Professional profile', 'Specialisation, qualifications, and experience are attached to your listing at every clinic you work with.'],
      ['Upcoming & past visits', 'A running record of appointments, so nothing depends on memory or a paper diary.']
    ]
  },
  {
    key: 'clinic',
    num: '03',
    label: 'Clinic admin',
    title: 'For clinic admins',
    intro: 'Run the day-to-day of the clinic from one screen — doctors, services, and slots included.',
    features: [
      ['Clinic profile', 'Keep contact details, address, services, and operating hours current for every patient search.'],
      ['Manage doctors', 'Add doctors, assign them to the clinic, and activate or deactivate accounts as your roster changes.'],
      ['Services on offer', 'Define the services the clinic provides and attach them to the doctors and appointments they apply to.'],
      ['Appointment slots', "Configure working days and time slots so booking always reflects who's actually in that day."],
      ['Appointment oversight', 'Monitor upcoming, confirmed, completed, cancelled, and rescheduled appointments in one queue.'],
      ["Patient records", "View the patients tied to your clinic's appointments, within the permissions your role allows."]
    ]
  },
  {
    key: 'super',
    num: '04',
    label: 'Super admin',
    title: 'For the super admin',
    intro: 'Oversight of the platform itself — every clinic, every account, one control panel.',
    features: [
      ['Clinic registry', 'Review and manage every clinic registered on the platform from a single list.'],
      ['User & role management', 'Manage platform users, assign roles and permissions, and activate or deactivate accounts.'],
      ['System configuration', "Adjust platform-wide settings without touching any individual clinic's data."],
      ['Activity monitoring', 'Keep an eye on clinic and system activity as new clinics and users come on board.'],
      ['Platform statistics', 'See usage across the platform at a glance, rather than clinic by clinic.'],
      ['Administrative controls', 'Handle the operational tasks that keep the platform itself running smoothly.']
    ]
  }
]

export default function Roles() {
  const [active, setActive] = useState('patient')
  const headRef = useReveal()
  const bodyRef = useReveal()
  const activeRole = ROLES.find((role) => role.key === active)

  return (
    <section className="section section--surface" id="roles">
      <div className="wrap">
        <div className="section-head reveal" ref={headRef}>
          <span className="eyebrow">One system, four roles</span>
          <h2>Built for everyone in the clinic — not just the front desk</h2>
          <p>
            Patients, doctors, clinic admins, and the platform's super admin each get exactly the screens
            their job needs, nothing they'd have to work around.
          </p>
        </div>

        <div className="roles reveal" ref={bodyRef}>
          <div className="role-tabs" role="tablist" aria-label="User roles">
            {ROLES.map((role) => (
              <button
                key={role.key}
                className={`role-tab${role.key === active ? ' is-active' : ''}`}
                role="tab"
                aria-selected={role.key === active}
                onClick={() => setActive(role.key)}
              >
                <span className="role-tab__num">{role.num}</span>
                <span className="role-tab__label">{role.label}</span>
              </button>
            ))}
          </div>

          <div className="role-panels">
            <div className="role-panel is-active" key={active}>
              <div className="role-panel__head">
                <h3>{activeRole.title}</h3>
                <p>{activeRole.intro}</p>
              </div>
              <div className="feature-grid">
                {activeRole.features.map(([title, body]) => (
                  <div className="feature" key={title}>
                    <h4>{title}</h4>
                    <p>{body}</p>
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
