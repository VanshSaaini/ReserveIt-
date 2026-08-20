import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import AppLayout, {
  StatCard,
  Loading,
  ErrorBox,
} from "../components/AppLayout.jsx";
import { clinicApi, appointmentApi } from "../api/client.js";

export default function ClinicDashboard() {
  const [c, setC] = useState(null);
  const [d, setD] = useState([]);
  const [s, setS] = useState([]);
  const [a, setA] = useState([]);
  const [err, setErr] = useState("");

  useEffect(() => {
    const loadDashboard = async () => {
      try {
        const [clinic, doctors, services, appointments] =
          await Promise.all([
            clinicApi.me(),
            clinicApi.myDoctors(),
            clinicApi.myServices(),
            appointmentApi.clinicMine(),
          ]);

        setC(clinic);
        setD(doctors);
        setS(services);
        setA(appointments);
      } catch (e) {
        setErr(e.message || "Failed to load clinic dashboard");
      }
    };

    loadDashboard();
  }, []);

  return (
    <AppLayout
      title="Clinic dashboard"
      subtitle="Manage your clinic, team and appointment flow."
    >
      <ErrorBox message={err} />

      {!c ? (
        <Loading />
      ) : (
        <>
          <div className="stats-grid">
            <StatCard
              label="Doctors"
              value={d.length}
            />

            <StatCard
              label="Services"
              value={s.length}
            />

            <StatCard
              label="Appointments"
              value={a.length}
            />

            <StatCard
              label="Clinic status"
              value={c.active ? "Active" : "Paused"}
            />
          </div>

          <div className="quick-grid">
            <Link
              to="/clinic/doctors"
              className="action-card"
            >
              <strong>Add a doctor</strong>

              <span>
                Create doctor accounts and set their practice details.
              </span>
            </Link>

            <Link
              to="/clinic/services"
              className="action-card"
            >
              <strong>Manage services</strong>

              <span>
                Configure consultation types, durations and prices.
              </span>
            </Link>

            <Link
              to="/clinic/appointments"
              className="action-card"
            >
              <strong>Review schedule</strong>

              <span>
                See and update today's clinic appointments.
              </span>
            </Link>
          </div>

          <section className="panel">
            <div className="panel-head">
              <div>
                <h2>{c.name}</h2>
                <p>{c.address}</p>
              </div>

              <Link
                to="/clinic/settings"
                className="btn btn--ghost"
              >
                Edit clinic
              </Link>
            </div>
          </section>
        </>
      )}
    </AppLayout>
  );
}