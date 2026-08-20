import { useEffect, useState } from "react";
import AppLayout, {
  ErrorBox,
  Empty,
  Loading,
} from "../components/AppLayout.jsx";
import { appointmentApi } from "../api/client.js";

export default function ClinicAppointments() {
  const [a, setA] = useState(null);
  const [err, setErr] = useState("");
  const [reminderId, setReminderId] = useState(null);
  const [success, setSuccess] = useState("");

  const load = () => {
    setErr("");
    setSuccess("");

    appointmentApi
      .clinicMine()
      .then(setA)
      .catch((e) => setErr(e.message));
  };

  useEffect(() => {
    load();
  }, []);

  async function sendReminder(id) {
    try {
      setReminderId(id);
      setErr("");
      setSuccess("");
      await appointmentApi.reminder(id);
      setSuccess("Reminder email sent successfully to the patient.");
    } catch (e) {
      setErr(e.message);
    } finally {
      setReminderId(null);
    }
  }

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
      title="Clinic appointments"
      subtitle="Manage the appointments across your clinic."
    >
      <ErrorBox message={err} />
      {success && <div style={{ marginBottom: "1rem", padding: "0.75rem 1rem", borderRadius: "8px", background: "#eaf8ef", color: "#176b35" }}>{success}</div>}

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
                  <th>Doctor</th>
                  <th>Date</th>
                  <th>Time</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>

              <tbody>
                {a.map((x) => (
                  <tr key={x.id}>
                    <td>{x.patientName}</td>
                    <td>{x.doctorName}</td>
                    <td>{x.appointmentDate}</td>
                    <td>{x.startTime}</td>
                    <td>{x.status}</td>

                    <td className="actions">
                      {x.status !== "CANCELLED" &&
                        x.status !== "COMPLETED" && (
                          <>
                            <button
                              className="btn btn--ghost btn--sm"
                              onClick={() => sendReminder(x.id)}
                              disabled={reminderId === x.id}
                            >
                              {reminderId === x.id ? "Sending..." : "Send Reminder"}
                            </button>
                            <button
                              className="btn btn--ghost btn--sm"
                              onClick={() => status(x.id, "CANCELLED")}
                            >
                              Cancel
                            </button>
                          </>
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