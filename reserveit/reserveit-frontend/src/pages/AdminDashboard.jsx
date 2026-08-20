import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import AppLayout, {
  StatCard,
  Loading,
  ErrorBox,
} from "../components/AppLayout.jsx";
import { adminApi } from "../api/client.js";

export default function AdminDashboard() {
  const [u, setU] = useState(null);
  const [c, setC] = useState(null);
  const [err, setErr] = useState("");

  useEffect(() => {
    setErr("");

    Promise.all([
      adminApi.users(),
      adminApi.clinics(),
    ])
      .then(([users, clinics]) => {
        setU(users);
        setC(clinics);
      })
      .catch((e) => {
        setErr(e.message);
      });
  }, []);

  return (
    <AppLayout
      title="Platform administration"
      subtitle="Monitor every account and clinic on ReserveIt."
    >
      <ErrorBox message={err} />

      {!u ? (
        <Loading />
      ) : (
        <>
          <div className="stats-grid">
            <StatCard
              label="Users"
              value={u.length}
            />

            <StatCard
              label="Clinics"
              value={c?.length || 0}
            />

            <StatCard
              label="Active users"
              value={u.filter((x) => x.active).length}
            />

            <StatCard
              label="Active clinics"
              value={
                (c || []).filter((x) => x.active).length
              }
            />
          </div>

          <div className="quick-grid">
            <Link
              to="/admin/users"
              className="action-card"
            >
              <strong>Manage users</strong>

              <span>
                Activate or deactivate patient, doctor and
                clinic accounts.
              </span>
            </Link>

            <Link
              to="/admin/clinics"
              className="action-card"
            >
              <strong>Manage clinics</strong>

              <span>
                Review every clinic and control platform
                availability.
              </span>
            </Link>
          </div>
        </>
      )}
    </AppLayout>
  );
}