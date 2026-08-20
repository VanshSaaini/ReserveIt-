import { useEffect, useState } from "react";
import AppLayout, {
  ErrorBox,
  Empty,
  Loading,
} from "../components/AppLayout.jsx";
import { appointmentApi } from "../api/client.js";

export default function DoctorAppointments() {
  const [a, setA] = useState(null);
  const [err, setErr] = useState("");

  const load = () => {
    setErr("");

    appointmentApi
      .doctorMine()
      .then(setA)
      .catch((e) => setErr(e.message));
  };

  useEffect(() => {
    load();
  }, []);

  async function status(id, status) {
    try {
      await appointmentApi.status(id, {
        status,
        notes: "",
      });

      load();
    } catch (e) {
      setErr(e.message);
    }
  }

  return (
    <AppLayout
      title="My schedule"
      subtitle="Review patients and update appointment status."
    >
      <ErrorBox message={err} />

      {!a ? (
        <Loading />
      ) : !a.length ? (
        <Empty />
      ) : (
        <section className="panel">
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Patient</th>
                  <th>Clinic</th>
                  <th>Date</th>
                  <th>Time</th>
                  <th>Status</th>
                  <th></th>
                </tr>
              </thead>

              <tbody>
                {a.map((x) => (
                  <tr key={x.id}>
                    <td>{x.patientName}</td>
                    <td>{x.clinicName}</td>
                    <td>{x.appointmentDate}</td>
                    <td>{x.startTime}</td>
                    <td>{x.status}</td>

                    <td>
                      {x.status !== "CANCELLED" &&
                        x.status !== "COMPLETED" && (
                          <button
                            className="btn btn--primary btn--sm"
                            onClick={() =>
                              status(x.id, "COMPLETED")
                            }
                          >
                            Complete
                          </button>
                        )}
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