import { useEffect, useState } from "react";
import AppLayout, { ErrorBox, Empty } from "../components/AppLayout.jsx";
import { doctorApi } from "../api/client.js";
const blank = {
  dayOfWeek: "MONDAY",
  startTime: "09:00",
  endTime: "17:00",
  slotDurationMinutes: 30,
};
export default function DoctorAvailability() {
  const [v, setV] = useState([]),
    [f, setF] = useState(blank),
    [err, setErr] = useState("");
  const load = () =>
    doctorApi
      .availability()
      .then(setV)
      .catch((e) => setErr(e.message));

  useEffect(() => {
    load();
  }, []);
  const ch = (k) => (e) => setF({ ...f, [k]: e.target.value });
  async function add(e) {
    e.preventDefault();
    try {
      await doctorApi.addAvailability({
        ...f,
        slotDurationMinutes: Number(f.slotDurationMinutes),
      });
      load();
    } catch (e) {
      setErr(e.message);
    }
  }
  async function remove(id) {
    try {
      await doctorApi.deleteAvailability(id);
      load();
    } catch (e) {
      setErr(e.message);
    }
  }
  return (
    <AppLayout
      title="Availability"
      subtitle="Set the windows patients can book."
    >
      <ErrorBox message={err} />
      <div className="two-col">
        <section className="panel">
          <h2>Add availability</h2>
          <form className="auth-form" onSubmit={add}>
            <select value={f.dayOfWeek} onChange={ch("dayOfWeek")}>
              {[
                "MONDAY",
                "TUESDAY",
                "WEDNESDAY",
                "THURSDAY",
                "FRIDAY",
                "SATURDAY",
                "SUNDAY",
              ].map((x) => (
                <option key={x}>{x}</option>
              ))}
            </select>
            <div className="form-row">
              <input
                type="time"
                value={f.startTime}
                onChange={ch("startTime")}
              />
              <input type="time" value={f.endTime} onChange={ch("endTime")} />
            </div>
            <input
              type="number"
              value={f.slotDurationMinutes}
              onChange={ch("slotDurationMinutes")}
            />
            <button className="btn btn--primary">Add window</button>
          </form>
        </section>
        <section className="panel">
          <h2>Current windows</h2>
          {v.map((x) => (
            <div className="data-row" key={x.id}>
              <div>
                <strong>{x.dayOfWeek}</strong>
                <span>
                  {x.startTime} – {x.endTime} · {x.slotDurationMinutes} min
                  slots
                </span>
              </div>
              <button
                className="btn btn--danger btn--sm"
                onClick={() => remove(x.id)}
              >
                Delete
              </button>
            </div>
          ))}
          {!v.length && <Empty />}
        </section>
      </div>
    </AppLayout>
  );
}
