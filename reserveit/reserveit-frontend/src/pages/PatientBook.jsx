import { useEffect, useState } from "react";
import AppLayout, {
  ErrorBox,
  Empty,
} from "../components/AppLayout.jsx";
import { clinicApi, doctorApi, appointmentApi } from "../api/client.js";
export default function PatientBook() {
  const [clinics, setClinics] = useState([]),
    [selected, setSelected] = useState(null),
    [doctors, setDoctors] = useState([]),
    [services, setServices] = useState([]),
    [doctor, setDoctor] = useState(""),
    [date, setDate] = useState(""),
    [slots, setSlots] = useState([]),
    [service, setService] = useState(""),
    [msg, setMsg] = useState(""),
    [err, setErr] = useState("");
  useEffect(() => {
    clinicApi
      .list()
      .then(setClinics)
      .catch((e) => setErr(e.message));
  }, []);
  async function choose(c) {
    setSelected(c);
    setMsg("");
    try {
      const [d, s] = await Promise.all([
        clinicApi.doctors(c.id),
        clinicApi.services(c.id),
      ]);
      setDoctors(d);
      setServices(s);
    } catch (e) {
      setErr(e.message);
    }
  }
  useEffect(() => {
    if (doctor && date)
      doctorApi
        .slots(doctor, date)
        .then(setSlots)
        .catch((e) => setErr(e.message));
  }, [doctor, date]);
  async function book(slot) {
    setErr("");
    try {
      await appointmentApi.book({
        doctorId: Number(doctor),
        serviceId: service ? Number(service) : null,
        appointmentDate: date,
        startTime: slot.startTime,
        notes: "",
      });
      setMsg("Appointment booked successfully. The appointment fee has been recorded with your booking.");
    } catch (e) {
      setErr(e.message);
    }
  }
  return (
    <AppLayout
      title="Find a clinic"
      subtitle="Choose a clinic, doctor and available time slot."
    >
      <ErrorBox message={err} />
      {msg && <div className="form-alert">{msg}</div>}
      <div className="booking-grid">
        <section className="panel">
          <div className="panel-head">
            <div>
              <h2>Clinics</h2>
              <p>Active clinics on ReserveIt.</p>
            </div>
          </div>
          {clinics.map((c) => (
            <button
              className={`select-card ${selected?.id === c.id ? "selected" : ""}`}
              key={c.id}
              onClick={() => choose(c)}
            >
              <strong>{c.name}</strong>
              <span>{c.address}</span>
            </button>
          ))}
          {!clinics.length && <Empty />}
        </section>
        {selected && (
          <section className="panel">
            <h2>Choose doctor & time</h2>
            <div className="form-field">
              <label>Doctor</label>
              <select
                value={doctor}
                onChange={(e) => setDoctor(e.target.value)}
              >
                <option value="">Select doctor</option>
                {doctors.map((d) => (
                  <option key={d.id} value={d.id}>
                    {d.firstName} {d.lastName} · {d.specialization || "General"}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-row">
              <div className="form-field">
                <label>Date</label>
                <input
                  type="date"
                  value={date}
                  onChange={(e) => setDate(e.target.value)}
                />
              </div>
              <div className="form-field">
                <label>Service</label>
                <select
                  value={service}
                  onChange={(e) => setService(e.target.value)}
                >
                  <option value="">Any service</option>
                  {services.map((s) => (
                    <option key={s.id} value={s.id}>
                      {s.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>
            {service && (() => {
              const selectedService = services.find((s) => String(s.id) === String(service));
              return selectedService ? (
                <div className="form-alert">Appointment fee: ₹{Number(selectedService.price || 0).toLocaleString("en-IN", { minimumFractionDigits: 2 })} · {selectedService.durationMinutes || "—"} minutes</div>
              ) : null;
            })()}
            <h3>Available slots</h3>
            {!doctor || !date ? (
              <Empty>Select a doctor and date.</Empty>
            ) : !slots.length ? (
              <Empty>No slots found.</Empty>
            ) : (
              <div className="slot-grid">
                {slots
                  .filter((s) => s.available)
                  .map((s) => (
                    <button
                      className="slot"
                      key={s.startTime}
                      onClick={() => book(s)}
                    >
                      {s.startTime} – {s.endTime}{service && (() => { const selectedService = services.find((x) => String(x.id) === String(service)); return selectedService ? ` · ₹${Number(selectedService.price || 0).toLocaleString("en-IN")}` : ""; })()}
                    </button>
                  ))}
              </div>
            )}
          </section>
        )}
      </div>
    </AppLayout>
  );
}
