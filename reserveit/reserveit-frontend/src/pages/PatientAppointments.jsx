import { useEffect, useState } from "react";
import AppLayout, {
  Empty,
  ErrorBox,
  Loading,
} from "../components/AppLayout.jsx";
import { appointmentApi } from "../api/client.js";

export default function PatientAppointments() {
  const [a, setA] = useState(null),
    [err, setErr] = useState(""),
    [reschedulingId, setReschedulingId] = useState(null),
    [form, setForm] = useState({ appointmentDate: "", startTime: "" });

  useEffect(() => {
    let active = true;
    const load = async () => {
      try {
        const data = await appointmentApi.mine();
        if (active) setA(data);
      } catch (e) {
        if (active) setErr(e.message);
      }
    };
    load();
    return () => { active = false; };
  }, []);

  async function cancel(id) {
    try {
      await appointmentApi.cancel(id);
      setA((x) =>
        x.map((a) => (a.id === id ? { ...a, status: "CANCELLED" } : a)),
      );
    } catch (e) {
      setErr(e.message);
    }
  }

  function startReschedule(x) {
    setErr("");
    setReschedulingId(x.id);
    setForm({ appointmentDate: x.appointmentDate, startTime: x.startTime });
  }

  function cancelReschedule() {
    setReschedulingId(null);
  }

  async function saveReschedule(id) {
    try {
      const updated = await appointmentApi.reschedule(id, form);
      setA((x) => x.map((a) => (a.id === id ? updated : a)));
      setReschedulingId(null);
    } catch (e) {
      setErr(e.message);
    }
  }

  return (
    <AppLayout
      title="My appointments"
      subtitle="View, reschedule or cancel your visits."
    >
      <ErrorBox message={err} />
      {!a ? (
        <Loading />
      ) : !a.length ? (
        <Empty>No appointments found.</Empty>
      ) : (
        <div className="panel">
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Doctor</th>
                  <th>Clinic</th>
                  <th>Service</th>
                  <th>Date</th>
                  <th>Time</th>
                  <th>Duration</th>
                  <th>Price</th>
                  <th>Status</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                {a.map((x) => {
                  const canModify =
                    x.status !== "CANCELLED" && x.status !== "COMPLETED";
                  const isRescheduling = reschedulingId === x.id;
                  return (
                    <tr key={x.id}>
                      <td>{x.doctorName}</td>
                      <td>{x.clinicName}</td>
                      <td>{x.serviceName || "Consultation"}</td>
                      {isRescheduling ? (
                        <>
                          <td>
                            <input
                              type="date"
                              value={form.appointmentDate}
                              onChange={(e) =>
                                setForm((f) => ({
                                  ...f,
                                  appointmentDate: e.target.value,
                                }))
                              }
                            />
                          </td>
                          <td>
                            <input
                              type="time"
                              value={form.startTime}
                              onChange={(e) =>
                                setForm((f) => ({
                                  ...f,
                                  startTime: e.target.value,
                                }))
                              }
                            />
                          </td>
                        </>
                      ) : (
                        <>
                          <td>{x.appointmentDate}</td>
                          <td>{x.startTime} – {x.endTime}</td>
                        </>
                      )}
                      <td>{x.endTime && x.startTime ? `${Math.round((new Date(`1970-01-01T${x.endTime}`) - new Date(`1970-01-01T${x.startTime}`)) / 60000)} min` : "—"}</td>
                      <td>₹{Number(x.price || 0).toLocaleString("en-IN", { minimumFractionDigits: 2 })}</td>
                      <td>
                        <span className="status">{x.status}</span>
                      </td>
                      <td className="actions">
                        {isRescheduling ? (
                          <>
                            <button
                              className="btn btn--primary btn--sm"
                              onClick={() => saveReschedule(x.id)}
                            >
                              Save
                            </button>
                            <button
                              className="btn btn--ghost btn--sm"
                              onClick={cancelReschedule}
                            >
                              Close
                            </button>
                          </>
                        ) : (
                          canModify && (
                            <>
                              <button
                                className="btn btn--ghost btn--sm"
                                onClick={() => startReschedule(x)}
                              >
                                Reschedule
                              </button>
                              <button
                                className="btn btn--danger btn--sm"
                                onClick={() => cancel(x.id)}
                              >
                                Cancel
                              </button>
                            </>
                          )
                        )}
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </AppLayout>
  );
}
