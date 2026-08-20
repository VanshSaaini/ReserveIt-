import { useEffect, useState } from "react";
import AppLayout, { ErrorBox, Empty } from "../components/AppLayout.jsx";
import { adminApi } from "../api/client.js";

const blank = { name: "", priceMonthly: "", maxDoctors: 2 };

export default function AdminSubscriptionPlans() {
  const [plans, setPlans] = useState([]);
  const [form, setForm] = useState(blank);
  const [editing, setEditing] = useState(null);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");

  const load = async () => {
    try {
      setErr("");
      setPlans(await adminApi.subscriptionPlans());
    } catch (e) {
      setErr(e.message);
    }
  };

  useEffect(() => { load(); }, []);

  const change = (key) => (e) =>
    setForm((x) => ({ ...x, [key]: e.target.value }));

  async function save(e) {
    e.preventDefault();
    try {
      setErr("");
      setMsg("");
      const payload = {
        name: form.name.trim(),
        priceMonthly: Number(form.priceMonthly),
        maxDoctors: Number(form.maxDoctors),
      };

      if (editing) {
        await adminApi.updateSubscriptionPlan(editing.id, payload);
        setMsg("Subscription plan updated.");
      } else {
        await adminApi.createSubscriptionPlan(payload);
        setMsg("Subscription plan created.");
      }

      setEditing(null);
      setForm(blank);
      await load();
    } catch (e) {
      setErr(e.message);
    }
  }

  function edit(plan) {
    setEditing(plan);
    setForm({
      name: plan.name,
      priceMonthly: plan.priceMonthly,
      maxDoctors: plan.maxDoctors,
    });
    setMsg("");
  }

  async function toggle(plan) {
    try {
      setErr("");
      await adminApi.subscriptionPlanActive(plan.id, !plan.active);
      await load();
    } catch (e) {
      setErr(e.message);
    }
  }

  return (
    <AppLayout
      title="Subscription plans"
      subtitle="Define pricing, doctor limits and plan availability."
    >
      <ErrorBox message={err} />
      {msg && <div className="form-alert">{msg}</div>}

      <div className="two-col">
        <section className="panel">
          <h2>{editing ? "Edit plan" : "Create plan"}</h2>

          <form className="auth-form" onSubmit={save}>
            <input
              placeholder="Plan name"
              value={form.name}
              onChange={change("name")}
              required
            />
            <input
              type="number"
              min="0"
              step="0.01"
              placeholder="Monthly price"
              value={form.priceMonthly}
              onChange={change("priceMonthly")}
              required
            />
            <input
              type="number"
              min="1"
              placeholder="Maximum doctors"
              value={form.maxDoctors}
              onChange={change("maxDoctors")}
              required
            />

            <div className="form-row">
              <button className="btn btn--primary" type="submit">
                {editing ? "Save changes" : "Create plan"}
              </button>
              {editing && (
                <button
                  type="button"
                  className="btn btn--ghost"
                  onClick={() => {
                    setEditing(null);
                    setForm(blank);
                  }}
                >
                  Cancel
                </button>
              )}
            </div>
          </form>
        </section>

        <section className="panel">
          <h2>Plans</h2>

          {!plans.length && <Empty />}

          {plans.map((plan) => (
            <div className="data-row" key={plan.id}>
              <div>
                <strong>{plan.name}</strong>
                <span>
                  ₹{Number(plan.priceMonthly).toFixed(2)} / month ·{" "}
                  {plan.maxDoctors} doctors ·{" "}
                  {plan.active ? "Active" : "Inactive"}
                </span>
              </div>

              <div className="form-row">
                <button
                  className="btn btn--ghost btn--sm"
                  onClick={() => edit(plan)}
                >
                  Edit
                </button>
                <button
                  className="btn btn--ghost btn--sm"
                  onClick={() => toggle(plan)}
                >
                  {plan.active ? "Deactivate" : "Activate"}
                </button>
              </div>
            </div>
          ))}
        </section>
      </div>
    </AppLayout>
  );
}
