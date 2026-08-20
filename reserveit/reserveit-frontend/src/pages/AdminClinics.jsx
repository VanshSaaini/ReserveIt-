import { useEffect, useState } from "react";
import AppLayout, {
  ErrorBox,
  Empty,
  Loading,
} from "../components/AppLayout.jsx";
import { adminApi } from "../api/client.js";

export default function AdminClinics() {
  const [c, setC] = useState(null);
  const [err, setErr] = useState("");

  const load = () => {
    setErr("");

    adminApi
      .clinics()
      .then(setC)
      .catch((e) => setErr(e.message));
  };

  useEffect(() => {
    load();
  }, []);

  async function toggle(x) {
    try {
      await adminApi.clinicActive(x.id, !x.active);
      load();
    } catch (e) {
      setErr(e.message);
    }
  }

  return (
    <AppLayout
      title="Clinics"
      subtitle="Platform-wide clinic administration."
    >
      <ErrorBox message={err} />

      {!c ? (
        <Loading />
      ) : !c.length ? (
        <Empty />
      ) : (
        <section className="panel">
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Clinic</th>
                  <th>Address</th>
                  <th>Admin</th>
                  <th>Status</th>
                  <th></th>
                </tr>
              </thead>

              <tbody>
                {c.map((x) => (
                  <tr key={x.id}>
                    <td>{x.name}</td>
                    <td>{x.address}</td>
                    <td>{x.adminName}</td>
                    <td>{x.active ? "Active" : "Inactive"}</td>

                    <td>
                      <button
                        className="btn btn--ghost btn--sm"
                        onClick={() => toggle(x)}
                      >
                        {x.active ? "Deactivate" : "Activate"}
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      )}
    </AppLayout>
  );
}