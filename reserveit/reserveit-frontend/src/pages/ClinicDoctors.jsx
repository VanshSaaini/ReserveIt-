import { useEffect, useState } from "react";
import AppLayout, { ErrorBox, Empty } from "../components/AppLayout.jsx";
import { clinicApi, doctorApi } from "../api/client.js";


const initial = {
  firstName: "",
  lastName: "",
  email: "",
  mobile: "",
  password: "",
  specialization: "",
  qualifications: "",
  experienceYears: "",
  defaultSlotMinutes: 30,
};

export default function ClinicDoctors() {
  const [d, setD] = useState([]);
  const [f, setF] = useState(initial);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");

  const load = async () => {
    try {
      setErr("");

      const doctors = await clinicApi.myDoctors();

      setD(doctors);
    } catch (e) {
      setErr(e.message);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const ch = (k) => (e) => {
    setF((prev) => ({
      ...prev,
      [k]: e.target.value,
    }));
  };

  async function add(e) {
    e.preventDefault();

    try {
      setErr("");
      setMsg("");

      await doctorApi.create({
        ...f,
        experienceYears: f.experienceYears
          ? Number(f.experienceYears)
          : null,
        defaultSlotMinutes: Number(f.defaultSlotMinutes),
      });

      setF({ ...initial });

      setMsg("Doctor created successfully.");

      await load();
    } catch (e) {
      setErr(e.message);
    }
  }

  async function toggle(x) {
    try {
      setErr("");

      await doctorApi.active(x.id, !x.active);

      await load();
    } catch (e) {
      setErr(e.message);
    }
  }

  return (
    <AppLayout
      title="Doctors"
      subtitle="Build and manage your clinic's medical team."
    >
      <ErrorBox message={err} />

      {msg && <div className="form-alert">{msg}</div>}

      <div className="two-col">

        {/* ADD DOCTOR */}
        <section className="panel">
          <h2>Add doctor</h2>

          <form className="auth-form" onSubmit={add}>

            <div className="form-row">
              <input
                placeholder="First name"
                value={f.firstName}
                onChange={ch("firstName")}
                required
              />

              <input
                placeholder="Last name"
                value={f.lastName}
                onChange={ch("lastName")}
                required
              />
            </div>

            <input
              type="email"
              placeholder="Email"
              value={f.email}
              onChange={ch("email")}
              required
            />

            <input
              placeholder="Mobile"
              value={f.mobile}
              onChange={ch("mobile")}
              required
            />

            <input
              type="password"
              placeholder="Temporary password"
              value={f.password}
              onChange={ch("password")}
              required
            />

            <input
              placeholder="Specialization"
              value={f.specialization}
              onChange={ch("specialization")}
            />

            <input
              placeholder="Qualifications"
              value={f.qualifications}
              onChange={ch("qualifications")}
            />

            <div className="form-row">
              <input
                type="number"
                placeholder="Experience years"
                value={f.experienceYears}
                onChange={ch("experienceYears")}
              />

              <input
                type="number"
                placeholder="Slot minutes"
                value={f.defaultSlotMinutes}
                onChange={ch("defaultSlotMinutes")}
              />
            </div>

            <button
              type="submit"
              className="btn btn--primary"
            >
              Create doctor
            </button>

          </form>
        </section>

        {/* DOCTOR LIST */}
        <section className="panel">
          <h2>Your doctors</h2>

          {d.map((x) => (
            <div className="data-row" key={x.id}>

              <div>
                <strong>
                  Dr. {x.firstName} {x.lastName}
                </strong>

                <span>
                  {x.specialization || "General"} ·{" "}
                  {x.experienceYears ?? 0} years
                </span>
              </div>

              <button
                className="btn btn--ghost btn--sm"
                onClick={() => toggle(x)}
              >
                {x.active ? "Deactivate" : "Activate"}
              </button>

            </div>
          ))}

          {!d.length && <Empty />}
        </section>

      </div>
    </AppLayout>
  );
}