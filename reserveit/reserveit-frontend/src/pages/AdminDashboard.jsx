import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import AppLayout, { StatCard, Loading, ErrorBox, Empty } from "../components/AppLayout.jsx";
import { adminApi } from "../api/client.js";

const money = (value) => `₹${Number(value || 0).toLocaleString("en-IN", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
const monthString = () => new Date().toISOString().slice(0, 7);

export default function AdminDashboard() {
  const [analytics, setAnalytics] = useState(null);
  const [payments, setPayments] = useState([]);
  const [month, setMonth] = useState(monthString());
  const [err, setErr] = useState("");
  const [busyId, setBusyId] = useState(null);

  const load = async () => {
    try {
      setErr("");
      const [report, subscriptionPayments] = await Promise.all([
        adminApi.analytics(undefined, month),
        adminApi.subscriptionPayments(month),
      ]);
      setAnalytics(report);
      setPayments(subscriptionPayments);
    } catch (e) { setErr(e.message); }
  };

  useEffect(() => { load(); }, [month]);

  async function markSubscriptionPaid(id, paid) {
    try {
      setBusyId(id);
      await adminApi.subscriptionPaymentPaid(id, paid);
      await load();
    } catch (e) { setErr(e.message); }
    finally { setBusyId(null); }
  }

  return (
    <AppLayout title="Platform administration" subtitle="Monitor ReserveIt platform health, accounts, clinics and manually collected subscriptions.">
      <ErrorBox message={err} />
      {!analytics ? <Loading /> : <>
        <section className="panel">
          <div className="panel-head">
            <div><h2>Platform overview</h2><p>Administrative metrics only — clinic appointment and clinic service revenue are intentionally excluded.</p></div>
            <div className="form-row"><label>Month</label><input type="month" value={month} onChange={e => setMonth(e.target.value)} /></div>
          </div>
          <div className="stats-grid">
            <StatCard label="Total clinics" value={analytics.totalClinics} />
            <StatCard label="Active clinics" value={analytics.activeClinics} />
            <StatCard label="Doctors" value={analytics.totalDoctors} />
            <StatCard label="Patients" value={analytics.totalPatients} />
            <StatCard label="Active subscriptions" value={analytics.activeSubscriptions} />
            <StatCard label="Expiring subscriptions" value={analytics.expiringSubscriptions} />
            <StatCard label="Expired subscriptions" value={analytics.expiredSubscriptions} />
            <StatCard label="New clinics this month" value={analytics.newClinicsThisMonth} />
          </div>
        </section>

        <section className="panel">
          <div className="panel-head"><div><h2>Subscription collection</h2><p>Offline collection tracking. ReserveIt does not process clinic subscription payments.</p></div><Link to="/admin/subscription-plans" className="btn btn--ghost">Manage plans</Link></div>
          <div className="stats-grid">
            <StatCard label="Expected" value={money(analytics.subscriptionExpected)} />
            <StatCard label="Collected" value={money(analytics.subscriptionCollected)} />
            <StatCard label="Pending" value={money(analytics.subscriptionPending)} />
            <StatCard label="Paid / Pending" value={`${analytics.subscriptionPaidCount} / ${analytics.subscriptionPendingCount}`} />
          </div>
          {!payments.length ? <Empty /> : <div className="table-wrap"><table><thead><tr><th>Clinic</th><th>Plan</th><th>Month</th><th>Amount</th><th>Status</th><th>Collected at</th><th></th></tr></thead><tbody>
            {payments.map(p => <tr key={p.id}><td>{p.clinicName}</td><td>{p.planName}</td><td>{p.billingMonth}</td><td>{money(p.amount)}</td><td>{p.paid ? "PAID" : "PENDING"}</td><td>{p.paidAt ? new Date(p.paidAt).toLocaleString() : "—"}</td><td><button className="btn btn--ghost btn--sm" disabled={busyId === p.id} onClick={() => markSubscriptionPaid(p.id, !p.paid)}>{p.paid ? "Mark pending" : "Mark collected"}</button></td></tr>)}
          </tbody></table></div>}
        </section>

        <section className="panel"><h2>Subscription plan distribution</h2>{!analytics.planDistribution?.length ? <Empty /> : <div className="table-wrap"><table><thead><tr><th>Plan</th><th>Clinics</th><th>Monthly fee</th><th>Doctor limit</th></tr></thead><tbody>{analytics.planDistribution.map(p => <tr key={p.planName}><td>{p.planName}</td><td>{p.clinics}</td><td>{money(p.monthlyPrice)}</td><td>{p.maxDoctors}</td></tr>)}</tbody></table></div>}</section>

        <section className="panel"><h2>Recently registered clinics</h2>{!analytics.recentClinics?.length ? <Empty /> : <div className="table-wrap"><table><thead><tr><th>Clinic</th><th>Status</th><th>Registered</th></tr></thead><tbody>{analytics.recentClinics.map(c => <tr key={c.clinicId}><td>{c.clinicName}</td><td>{c.active ? "Active" : "Inactive"}</td><td>{c.registeredAt ? new Date(c.registeredAt).toLocaleString() : "—"}</td></tr>)}</tbody></table></div>}</section>

        <div className="quick-grid">
          <Link to="/admin/users" className="action-card"><strong>Manage users</strong><span>Review accounts by clinic, doctor and patient hierarchy.</span></Link>
          <Link to="/admin/subscription-plans" className="action-card"><strong>Manage subscription plans</strong><span>Configure plan price and doctor limits.</span></Link>
          <Link to="/admin/clinics" className="action-card"><strong>Manage clinics</strong><span>Review clinic accounts and platform availability.</span></Link>
        </div>
      </>}
    </AppLayout>
  );
}
