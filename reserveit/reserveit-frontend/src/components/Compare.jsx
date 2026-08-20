import { useReveal } from '../hooks/useReveal.js'

const OLD_WAY = [
  "Appointments taken over the phone, written into a register by hand",
  "Doctor availability lives in someone's head, or a whiteboard",
  "Double-bookings surface only when the patient walks in",
  "Reminders, if sent at all, are a manual phone call",
  "Patient history scattered across paper files and different desks"
]

const NEW_WAY = [
  "Patients book directly against real-time doctor availability",
  "Every slot a doctor opens is visible the moment it's set",
  "One appointment record — reception, doctor, and patient see the same status",
  "Confirmation and reminder emails go out automatically",
  "Patient and visit history held centrally, by role-based permission"
]

export default function Compare() {
  const headRef = useReveal()
  const cardsRef = useReveal()

  return (
    <section className="section">
      <div className="wrap">
        <div className="section-head reveal" ref={headRef}>
          <span className="eyebrow">Why ReserveIt</span>
          <h2>Manual scheduling breaks quietly, then all at once</h2>
          <p>
            A register and a spreadsheet can run a single clinic for a while. They stop working the moment a
            second doctor, a second location, or a busy Monday shows up.
          </p>
        </div>

        <div className="compare reveal" ref={cardsRef}>
          <div className="compare-card compare-card--old">
            <h3>The old way</h3>
            <ul>
              {OLD_WAY.map((item) => (
                <li key={item}><span className="dot"></span>{item}</li>
              ))}
            </ul>
          </div>
          <div className="compare-card compare-card--new">
            <h3>With ReserveIt</h3>
            <ul>
              {NEW_WAY.map((item) => (
                <li key={item}><span className="dot"></span>{item}</li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </section>
  )
}
