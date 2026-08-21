import { useEffect, useState } from "react";
import AppLayout, { Loading, ErrorBox } from "../components/AppLayout.jsx";
import { doctorApi } from "../api/client.js";

export default function DoctorProfile() {
  const [d, setD] = useState(null);
  const [form, setForm] = useState(null);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    let active = true;
    const load = async () => {
      try {
        const data = await doctorApi.me();
        if (active) {
          setD(data);
          setForm({ firstName: data.firstName || "", lastName: data.lastName || "", mobile: data.mobile || "", specialization: data.specialization || "", qualifications: data.qualifications || "", experienceYears: data.experienceYears ?? "", defaultSlotMinutes: data.defaultSlotMinutes ?? 30 });
        }
      } catch (e) { if (active) setErr(e.message); }
    };
    load();
    return () => { active = false; };
  }, []);

  const change = key => e => setForm(x => ({ ...x, [key]: e.target.value }));

  async function save(e) {
    e.preventDefault();
    try {
      setSaving(true); setErr(""); setMsg("");
      const updated = await doctorApi.updateMe({ ...form, experienceYears: form.experienceYears === "" ? null : Number(form.experienceYears), defaultSlotMinutes: Number(form.defaultSlotMinutes) });
      setD(updated);
      setForm({ firstName: updated.firstName || "", lastName: updated.lastName || "", mobile: updated.mobile || "", specialization: updated.specialization || "", qualifications: updated.qualifications || "", experienceYears: updated.experienceYears ?? "", defaultSlotMinutes: updated.defaultSlotMinutes ?? 30 });
      setMsg("Profile updated successfully.");
    } catch (e) { setErr(e.message); }
    finally { setSaving(false); }
  }

  return <AppLayout title="My profile" subtitle="Manage the professional information patients see.">
    <ErrorBox message={err} />
    {msg && <div className="form-alert">{msg}</div>}
    {!d || !form ? <Loading /> : <section className="panel narrow"><form className="auth-form" onSubmit={save}>
      <label>First name<input value={form.firstName} onChange={change("firstName")} required /></label>
      <label>Last name<input value={form.lastName} onChange={change("lastName")} required /></label>
      <label>Email<input value={d.email || ""} disabled /></label>
      <label>Mobile<input value={form.mobile} onChange={change("mobile")} required /></label>
      <label>Specialization<input value={form.specialization} onChange={change("specialization")} /></label>
      <label>Qualifications<textarea value={form.qualifications} onChange={change("qualifications")} rows="3" /></label>
      <label>Experience (years)<input type="number" min="0" value={form.experienceYears} onChange={change("experienceYears")} /></label>
      <label>Default slot duration (minutes)<input type="number" min="5" value={form.defaultSlotMinutes} onChange={change("defaultSlotMinutes")} /></label>
      <div className="form-alert">Clinic: {d.clinicName || "—"}</div>
      <button className="btn btn--primary" disabled={saving}>{saving ? "Saving…" : "Save profile"}</button>
    </form></section>}
  </AppLayout>;
}
