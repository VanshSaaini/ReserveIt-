import { useEffect, useState } from "react";
import AppLayout, {
  ErrorBox,
  Empty,
  Loading,
} from "../components/AppLayout.jsx";
import { adminApi } from "../api/client.js";

export default function AdminUsers() {
  const [u, setU] = useState(null);
  const [err, setErr] = useState("");

  const load = () => {
    setErr("");

    adminApi
      .users()
      .then(setU)
      .catch((e) => setErr(e.message));
  };

  useEffect(() => {
    load();
  }, []);

  async function toggle(x) {
    try {
      await adminApi.userActive(x.id, !x.active);
      load();
    } catch (e) {
      setErr(e.message);
    }
  }

  return (
    <AppLayout
      title="Users"
      subtitle="Platform-wide user account administration."
    >
      <ErrorBox message={err} />

      {!u ? (
        <Loading />
      ) : !u.length ? (
        <Empty />
      ) : (
        <section className="panel">
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Status</th>
                  <th></th>
                </tr>
              </thead>

              <tbody>
                {u.map((x) => (
                  <tr key={x.id}>
                    <td>
                      {x.firstName} {x.lastName}
                    </td>

                    <td>{x.email}</td>

                    <td>{x.role}</td>

                    <td>
                      {x.active ? "Active" : "Inactive"}
                    </td>

                    <td>
                      <button
                        className="btn btn--ghost btn--sm"
                        onClick={() => toggle(x)}
                      >
                        {x.active
                          ? "Deactivate"
                          : "Activate"}
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