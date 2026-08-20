import { useReveal } from '../hooks/useReveal.js'

const STEPS = [
  ['Find & choose', 'Patient searches for a clinic, reviews its doctors and services, and picks who to see.'],
  ['Check availability', "Only the doctor's actual open slots for that date are shown."],
  ['Book & confirm', 'Patient selects a slot and confirms; the system stores the appointment right away.'],
  ["Everyone's notified", 'Confirmation reaches the patient; the doctor and clinic see it on their schedule.'],
  ['Reminder sent', 'An automatic reminder goes out ahead of the appointment date.'],
  ['Visit & close out', 'Doctor marks the appointment completed, cancelled, or rescheduled — the record updates for good.']
]

export default function Workflow() {
  const headRef = useReveal()
  const listRef = useReveal()

  return (
    <section className="section" id="workflow">
      <div className="wrap">
        <div className="section-head reveal" ref={headRef}>
          <span className="eyebrow">From search to visit</span>
          <h2>What booking an appointment actually looks like</h2>
          <p>
            Six stages, start to finish — the same record moves through all of them, so nobody's re-entering
            the same booking twice.
          </p>
        </div>

        <div className="timeline reveal" ref={listRef}>
          {STEPS.map(([title, body]) => (
            <div className="step" key={title}>
              <div className="step__num"></div>
              <h4>{title}</h4>
              <p>{body}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
