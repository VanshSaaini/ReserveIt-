import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import AppLayout, {
  StatCard,
  ErrorBox,
  Loading,
  Empty,
} from "../components/AppLayout.jsx";
import { doctorApi, appointmentApi } from "../api/client.js";
export default function DoctorDashboard() {
  const [d, setD] = useState(null),
    [a, setA] = useState([]),
    [v, setV] = useState([]),
    [err, setErr] = useState("");
  useEffect(() => {
    Promise.allSettled([
      doctorApi.me(),
      appointmentApi.doctorMine(),
      doctorApi.availability(),
    ]).then(([profile, appts, avail]) => {
      const failures = [profile, appts, avail].filter(
        (r) => r.status === "rejected",
      );
      if (failures.length) setErr(failures[0].reason.message);
      if (profile.status === "fulfilled") setD(profile.value);
      if (appts.status === "fulfilled") setA(appts.value);
      if (avail.status === "fulfilled") setV(avail.value);
    });
  }, []);
  return (
    <AppLayout
      title="Doctor dashboard"
      subtitle="Your clinical schedule and availability."
    >
      <ErrorBox message={err} />
      {!d && !err ? (
        <Loading />
      ) : !d ? null : (
        <>
          <div className="stats-grid">
            <StatCard label="Today's / total appointments" value={a.length} />
            <StatCard label="Availability windows" value={v.length} />
            <StatCard
              label="Specialization"
              value={d.specialization || "General"}
            />
          </div>
          <section className="panel">
            <div className="panel-head">
              <div>
                <h2>Next appointments</h2>
                <p>Keep your schedule ready for patients.</p>
              </div>
              <Link to="/doctor/availability" className="btn btn--primary">
                Manage availability
              </Link>
            </div>
            {a.slice(0, 6).map((x) => (
              <div className="data-row" key={x.id}>
                <div>
                  <strong>{x.patientName}</strong>
                  <span>
                    {x.appointmentDate} · {x.startTime} · {x.clinicName}
                  </span>
                </div>
                <span className="status">{x.status}</span>
              </div>
            ))}
            {!a.length && <Empty>No appointments assigned yet.</Empty>}
          </section>
        </>
      )}
    </AppLayout>
  );
}
