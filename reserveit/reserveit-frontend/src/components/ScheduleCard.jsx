// The hero's signature element: a mock daily schedule showing
// booked / available / selected appointment slots.
export default function ScheduleCard() {
  return (
    <div className="schedule-card">
      <div className="schedule-card__head">
        <h3>Tuesday, 14 Oct</h3>
        <span>Cascade Family Clinic</span>
      </div>

      <div className="schedule-card__doc">
        <span className="schedule-card__avatar" aria-hidden="true"></span>
        <div>
          <p>Dr. Meera Anand</p>
          <p>General Physician</p>
        </div>
      </div>

      <div className="slot-list">
        <div className="slot slot--booked">
          <span className="slot__time">09:00</span>
          <span className="pill pill--booked">Booked</span>
        </div>
        <div className="slot">
          <span className="slot__time">09:20</span>
          <span className="pill pill--open">Available</span>
        </div>
        <div className="slot slot--selected">
          <span className="slot__time">09:40</span>
          <span className="pill pill--selected">
            <svg viewBox="0 0 16 16"><path d="M3 8.5l3 3 7-7" /></svg>
            Selected
          </span>
        </div>
        <div className="slot slot--booked">
          <span className="slot__time">10:00</span>
          <span className="pill pill--booked">Booked</span>
        </div>
        <div className="slot">
          <span className="slot__time">10:20</span>
          <span className="pill pill--open">Available</span>
        </div>
      </div>
    </div>
  )
}
