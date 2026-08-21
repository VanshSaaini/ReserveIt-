import { useEffect, useState } from "react";
import AppLayout, { ErrorBox, Loading, Empty, StatCard } from "../components/AppLayout.jsx";
import { clinicApi } from "../api/client.js";

export default function ClinicSubscription() {
  const [subscription, setSubscription] = useState(null);
  const [plans, setPlans] = useState([]);
  const [history, setHistory] = useState([]);
  const [err, setErr] = useState("");
  const [msg, setMsg] = useState("");
  const [busy, setBusy] = useState(false);

  const load = async () => {
    try {
      setErr("");
      const [sub, availablePlans, events] = await Promise.all([
        clinicApi.subscription().catch(() => null),
        clinicApi.subscriptionPlans(),
        clinicApi.subscriptionHistory().catch(() => []),
      ]);
      setSubscription(sub);
      setPlans(availablePlans);
      setHistory(events);
    } catch (e) {
      setErr(e.message);
    }
  };

  useEffect(() => { load(); }, []);

  async function selectPlan(planId) {
    try {
      setBusy(true);
      setErr("");
      setMsg("");
      await clinicApi.selectSubscriptionPlan(planId);
      setMsg("Subscription plan updated successfully.");
      await load();
    } catch (e) {
      setErr(e.message);
    } finally {
      setBusy(false);
    }
  }

  async function renew() {
    try {
      setBusy(true);
      setErr("");
      setMsg("");
      await clinicApi.renewSubscription();
      setMsg("Subscription renewed successfully.");
      await load();
    } catch (e) {
      setErr(e.message);
    } finally {
      setBusy(false);
    }
  }

  async function cancel() {
    if (!window.confirm("Cancel the current subscription now?")) return;

    try {
      setBusy(true);
      setErr("");
      setMsg("");
      await clinicApi.cancelSubscription();
      setMsg("Subscription cancelled.");
      await load();
    } catch (e) {
      setErr(e.message);
    } finally {
      setBusy(false);
    }
  }

  return (
    <AppLayout
      title="Subscription"
      subtitle="Manage your clinic plan, lifecycle and doctor capacity."
    >
      <ErrorBox message={err} />
      {msg && <div className="form-alert">{msg}</div>}

      {!subscription ? (
        <section className="panel">
          <h2>Choose a subscription</h2>
          <p>Your clinic does not have an active subscription yet.</p>
        </section>
      ) : (
        <>
          <div className="stats-grid">
            <StatCard label="Current plan" value={subscription.planName} />
            <StatCard
              label="Doctors"
              value={`${subscription.currentDoctors}/${subscription.maxDoctors}`}
            />
            <StatCard
              label="Remaining capacity"
              value={subscription.remainingDoctors}
            />
            <StatCard label="Status" value={subscription.status} />
            <StatCard label="Fee collection" value={subscription.feeCollected ? "Collected" : "Pending"} />
          </div>

          <section className="panel">
            <div className="panel-head">
              <div>
                <h2>{subscription.planName}</h2>
                <p>
                  ₹{Number(subscription.priceMonthly).toFixed(2)} / month ·
                  {" "}valid {subscription.startDate} to {subscription.endDate}
                </p>
              </div>
              <div className="form-row">
                <button
                  className="btn btn--primary"
                  disabled={busy || subscription.status === "CANCELLED"}
                  onClick={renew}
                >
                  Renew
                </button>
                <button
                  className="btn btn--ghost"
                  disabled={busy || subscription.status === "CANCELLED"}
                  onClick={cancel}
                >
                  Cancel
                </button>
              </div>
            </div>
            <p>
              {subscription.daysRemaining} day(s) remaining. Fee is collected offline by ReserveIt administration; no online payment is required here.
            </p>
            <p>
              {subscription.feeCollectedAt ? `Collected: ${new Date(subscription.feeCollectedAt).toLocaleString()}` : "Subscription fee: pending offline collection."}
            </p>
            <p>
              {subscription.status === "EXPIRING" &&
                " Your subscription is approaching its end date."}
            </p>
          </section>
        </>
      )}

      <section className="panel">
        <h2>{subscription ? "Change plan" : "Available plans"}</h2>

        {!plans.length && <Empty />}

        <div className="quick-grid">
          {plans.map((plan) => {
            const current = subscription?.planId === plan.id;
            const downgrade =
              subscription && plan.maxDoctors < subscription.maxDoctors;
            const blockedDowngrade =
              downgrade &&
              subscription.currentDoctors > plan.maxDoctors;

            return (
              <div className="action-card" key={plan.id}>
                <strong>{plan.name}</strong>
                <span>
                  ₹{Number(plan.priceMonthly).toFixed(2)} / month
                </span>
                <span>
                  Up to {plan.maxDoctors} doctors
                </span>
                <button
                  className="btn btn--primary"
                  disabled={busy || current || blockedDowngrade}
                  onClick={() => selectPlan(plan.id)}
                >
                  {current
                    ? "Current plan"
                    : blockedDowngrade
                      ? `Need ${plan.maxDoctors} or fewer doctors`
                      : downgrade
                        ? "Downgrade"
                        : "Upgrade"}
                </button>
              </div>
            );
          })}
        </div>
      </section>

      <section className="panel">
        <h2>Subscription history</h2>

        {!history.length ? (
          <Empty />
        ) : (
          history.map((event) => (
            <div className="data-row" key={event.id}>
              <div>
                <strong>
                  {event.eventType.replaceAll("_", " ")}
                </strong>
                <span>
                  {event.planName} · {event.status} ·{" "}
                  {event.createdAt
                    ? new Date(event.createdAt).toLocaleString()
                    : ""}
                </span>
              </div>
              <span>
                {event.startDate} → {event.endDate}
              </span>
            </div>
          ))
        )}
      </section>
    </AppLayout>
  );
}
