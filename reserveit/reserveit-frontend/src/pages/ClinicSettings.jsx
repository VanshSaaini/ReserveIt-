import { useEffect, useState } from "react";
import AppLayout, { ErrorBox } from "../components/AppLayout.jsx";
import { clinicApi } from "../api/client.js";
export default function ClinicSettings() {
  const [f, setF] = useState(null),
    [err, setErr] = useState(""),
    [msg, setMsg] = useState("");
  useEffect(() => {
    let active = true;
    const load = async () => {
      try {
        const data = await clinicApi.me();
        if (active) setF(data);
      } catch (e) {
        if (active) setErr(e.message);
      }
    };
    load();
    return () => { active = false; };
  }, []);
  if (!f)
    return (
      <AppLayout title="Clinic settings">
        <ErrorBox message={err} />
        <div className="loading">Loading…</div>
      </AppLayout>
    );
  const ch = (k) => (e) => setF({ ...f, [k]: e.target.value });
  async function save(e) {
    e.preventDefault();
    try {
      setF(
        await clinicApi.update({
          name: f.name,
          address: f.address,
          phone: f.phone,
          email: f.email,
        }),
      );
      setMsg("Clinic details updated.");
    } catch (e) {
      setErr(e.message);
    }
  }
  return (
    <AppLayout
      title="Clinic settings"
      subtitle="Keep your clinic information current."
    >
      <ErrorBox message={err} />
      {msg && <div className="form-alert">{msg}</div>}
      <section className="panel narrow">
        <form className="auth-form" onSubmit={save}>
          <label>
            Clinic name
            <input value={f.name} onChange={ch("name")} required />
          </label>
          <label>
            Address
            <input value={f.address} onChange={ch("address")} required />
          </label>
          <label>
            Phone
            <input value={f.phone || ""} onChange={ch("phone")} />
          </label>
          <label>
            Email
            <input value={f.email || ""} onChange={ch("email")} />
          </label>
          <button className="btn btn--primary">Save changes</button>
        </form>
      </section>
    </AppLayout>
  );
}
