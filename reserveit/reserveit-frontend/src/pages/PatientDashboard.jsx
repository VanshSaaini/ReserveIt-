import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import AppLayout, {
  StatCard,
  Loading,
  Empty,
  ErrorBox,
} from "../components/AppLayout.jsx";
import { appointmentApi, patientApi } from "../api/client.js";
export default function PatientDashboard() {
  const [a, setA] = useState([]),
    [p, setP] = useState(null),
    [err, setErr] = useState("");
  useEffect(() => {
    Promise.all([appointmentApi.mine(), patientApi.me()])
      .then(([x, y]) => {
        setA(x || []);
        setP(y);
      })
      .catch((e) => setErr(e.message));
  }, []);
  return (
    <AppLayout
      title="Patient dashboard"
      subtitle="Keep track of visits, doctors and upcoming care."
    >
      <ErrorBox message={err} />
      {!p && !err ? (
        <Loading />
      ) : (
        <>
          <div className="stats-grid">
            <StatCard
              label="Upcoming"
              value={a.filter((x) => x.status === "CONFIRMED").length}
            />
            <StatCard label="Total appointments" value={a.length} />
            <StatCard label="Profile" value={p ? "Complete" : "—"} />
          </div>
          <section className="panel">
            <div className="panel-head">
              <div>
                <h2>Upcoming appointments</h2>
                <p>Your next visits at a glance.</p>
              </div>
              <Link className="btn btn--primary" to="/patient/book">
                Book appointment
              </Link>
            </div>
            {a
              .filter((x) => x.status !== "CANCELLED")
              .slice(0, 5)
              .map((x) => (
                <AppointmentRow key={x.id} a={x} />
              ))}
            {!a.length && (
              <Empty>No appointments yet. Find a clinic to get started.</Empty>
            )}
          </section>
        </>
      )}
    </AppLayout>
  );
}
function AppointmentRow({ a }) {
  return (
    <div className="data-row">
      <div>
        <strong>{a.doctorName}</strong>
        <span>
          {a.clinicName} · {a.appointmentDate} · {a.startTime}
        </span>
      </div>
      <span className={`status status--${String(a.status).toLowerCase()}`}>
        {a.status}
      </span>
    </div>
  );
}
