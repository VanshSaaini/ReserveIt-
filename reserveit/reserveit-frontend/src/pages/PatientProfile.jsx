import { useEffect, useState } from "react";
import AppLayout, { Loading, ErrorBox } from "../components/AppLayout.jsx";
import { patientApi } from "../api/client.js";

export default function PatientProfile() {
  const [p, setP] = useState(null);
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState(null);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    patientApi
      .me()
      .then(setP)
      .catch((e) => setErr(e.message));
  }, []);

  function startEdit() {
    setErr("");
    setMsg("");
    setForm({
      firstName: p.firstName || "",
      lastName: p.lastName || "",
      mobile: p.mobile || "",
      dateOfBirth: p.dateOfBirth || "",
    });
    setEditing(true);
  }

  function cancelEdit() {
    setEditing(false);
    setForm(null);
  }

  const ch = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  async function save(e) {
    e.preventDefault();
    setErr("");
    setSaving(true);
    try {
      const updated = await patientApi.update({
        ...form,
        dateOfBirth: form.dateOfBirth || null,
      });
      setP(updated);
      setEditing(false);
      setMsg("Profile updated.");
    } catch (e) {
      setErr(e.message);
    } finally {
      setSaving(false);
    }
  }

  return (
    <AppLayout title="My profile" subtitle="Your patient information.">
      <ErrorBox message={err} />
      {msg && <div className="form-alert">{msg}</div>}
      {!p ? (
        <Loading />
      ) : editing ? (
        <section className="panel narrow">
          <form className="auth-form" onSubmit={save}>
            <div className="form-row">
              <input
                placeholder="First name"
                value={form.firstName}
                onChange={ch("firstName")}
                required
              />
              <input
                placeholder="Last name"
                value={form.lastName}
                onChange={ch("lastName")}
                required
              />
            </div>
            <input
              placeholder="Mobile"
              value={form.mobile}
              onChange={ch("mobile")}
              required
            />
            <label>
              Date of birth
              <input
                type="date"
                value={form.dateOfBirth}
                onChange={ch("dateOfBirth")}
              />
            </label>
            <div className="form-row">
              <button
                type="submit"
                className="btn btn--primary"
                disabled={saving}
              >
                {saving ? "Saving…" : "Save changes"}
              </button>
              <button
                type="button"
                className="btn btn--ghost"
                onClick={cancelEdit}
                disabled={saving}
              >
                Cancel
              </button>
            </div>
          </form>
        </section>
      ) : (
        <section className="panel profile-card">
          <div className="avatar avatar--large">{p.firstName?.[0]}</div>
          <div>
            <h2>
              {p.firstName} {p.lastName}
            </h2>
            <p>{p.email}</p>
            <p>{p.mobile}</p>
            <p>Date of birth: {p.dateOfBirth || "Not provided"}</p>
            <button className="btn btn--ghost btn--sm" onClick={startEdit}>
              Edit profile
            </button>
          </div>
        </section>
      )}
    </AppLayout>
  );
}
