import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import AppLayout, { StatCard, Loading, ErrorBox, Empty } from "../components/AppLayout.jsx";
import { adminApi } from "../api/client.js";

const money = (value) =>
  `₹${Number(value || 0).toLocaleString("en-IN", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;

const todayString = () => new Date().toISOString().slice(0, 10);
const monthString = () => new Date().toISOString().slice(0, 7);

export default function AdminDashboard() {
  const [u, setU] = useState(null);
  const [c, setC] = useState(null);
  const [analytics, setAnalytics] = useState(null);
  const [payments, setPayments] = useState([]);
  const [day, setDay] = useState(todayString());
  const [month, setMonth] = useState(monthString());
  const [err, setErr] = useState("");
  const [busyId, setBusyId] = useState(null);

  const load = async () => {
    try {
      setErr("");
      const [users, clinics, report, subscriptionPayments] = await Promise.all([
        adminApi.users(),
        adminApi.clinics(),
        adminApi.analytics(day, month),
        adminApi.subscriptionPayments(month),
      ]);
      setU(users);
      setC(clinics);
      setAnalytics(report);
      setPayments(subscriptionPayments);
    } catch (e) {
      setErr(e.message);
    }
  };

  useEffect(() => { load(); }, [day, month]);

  async function markSubscriptionPaid(id, paid) {
    try {
      setBusyId(id);
      await adminApi.subscriptionPaymentPaid(id, paid);
      await load();
    } catch (e) {
      setErr(e.message);
    } finally {
      setBusyId(null);
    }
  }

  return (
    <AppLayout title="Platform administration" subtitle="Monitor ReserveIt operations, clinic performance and manually collected subscriptions.">
      <ErrorBox message={err} />

      {!u || !analytics ? <Loading /> : <>
        <div className="stats-grid">
          <StatCard label="Users" value={u.length} />
          <StatCard label="Clinics" value={c?.length || 0} />
          <StatCard label="Active users" value={u.filter((x) => x.active).length} />
          <StatCard label="Active clinics" value={(c || []).filter((x) => x.active).length} />
        </div>

        <section className="panel">
          <div className="panel-head">
            <div>
              <h2>Platform business — selected day</h2>
              <p>{analytics.reportDate} · all clinics combined</p>
            </div>
            <div className="form-row">
              <label>Day</label>
              <input type="date" value={day} onChange={(e) => setDay(e.target.value)} />
            </div>
          </div>
          <div className="stats-grid">
            <StatCard label="Appointments" value={analytics.dayAppointments} />
            <StatCard label="Booked revenue" value={money(analytics.dayBookedRevenue)} />
            <StatCard label="Collected" value={money(analytics.dayCollectedRevenue)} />
            <StatCard label="Pending" value={money(analytics.dayPendingRevenue)} />
          </div>
        </section>

        <section className="panel">
          <div className="panel-head">
            <div>
              <h2>Platform monthly performance</h2>
              <p>{analytics.reportMonth} · operational and clinic revenue KPIs</p>
            </div>
            <div className="form-row">
              <label>Month</label>
              <input type="month" value={month} onChange={(e) => setMonth(e.target.value)} />
            </div>
          </div>
          <div className="stats-grid">
            <StatCard label="Appointments" value={analytics.monthAppointments} />
            <StatCard label="Booked revenue" value={money(analytics.monthBookedRevenue)} />
            <StatCard label="Collected" value={money(analytics.monthCollectedRevenue)} />
            <StatCard label="Pending" value={money(analytics.monthPendingRevenue)} />
          </div>
          <div className="quick-grid">
            <div className="action-card"><strong>{analytics.monthCompleted}</strong><span>Completed appointments</span></div>
            <div className="action-card"><strong>{analytics.monthCancelled}</strong><span>Cancelled appointments</span></div>
            <div className="action-card"><strong>{analytics.clinics.length}</strong><span>Clinics with activity</span></div>
          </div>
        </section>

        <section className="panel">
          <h2>Clinic performance</h2>
          {!analytics.clinics.length ? <Empty /> : (
            <div className="table-wrap">
              <table>
                <thead><tr><th>Clinic</th><th>Appointments</th><th>Booked</th><th>Collected</th><th>Pending</th></tr></thead>
                <tbody>
                  {analytics.clinics.map(x => (
                    <tr key={x.clinicId}>
                      <td>{x.clinicName}</td>
                      <td>{x.appointments}</td>
                      <td>{money(x.bookedRevenue)}</td>
                      <td>{money(x.collectedRevenue)}</td>
                      <td>{money(x.pendingRevenue)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        <section className="panel">
          <div className="panel-head">
            <div>
              <h2>Manual clinic subscription collection</h2>
              <p>{analytics.reportMonth} · ReserveIt does not process clinic payments. Mark cash/bank/UPI collections manually here.</p>
            </div>
            <Link to="/admin/subscription-plans" className="btn btn--ghost">Manage plans</Link>
          </div>

          <div className="stats-grid">
            <StatCard label="Expected subscription fees" value={money(analytics.subscriptionExpected)} />
            <StatCard label="Collected" value={money(analytics.subscriptionCollected)} />
            <StatCard label="Pending" value={money(analytics.subscriptionPending)} />
            <StatCard label="Paid / Pending clinics" value={`${analytics.subscriptionPaidCount} / ${analytics.subscriptionPendingCount}`} />
          </div>

          {!payments.length ? <Empty /> : (
            <div className="table-wrap">
              <table>
                <thead><tr><th>Clinic</th><th>Plan</th><th>Billing month</th><th>Amount</th><th>Payment</th><th>Paid at</th><th>Action</th></tr></thead>
                <tbody>
                  {payments.map(p => (
                    <tr key={p.id}>
                      <td>{p.clinicName}</td>
                      <td>{p.planName}</td>
                      <td>{p.billingMonth}</td>
                      <td>{money(p.amount)}</td>
                      <td>{p.paid ? "PAID" : "PENDING"}</td>
                      <td>{p.paidAt ? new Date(p.paidAt).toLocaleString() : "—"}</td>
                      <td>
                        <button
                          className="btn btn--ghost btn--sm"
                          disabled={busyId === p.id}
                          onClick={() => markSubscriptionPaid(p.id, !p.paid)}
                        >
                          {p.paid ? "Mark pending" : "Mark paid"}
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>

        <div className="quick-grid">
          <Link to="/admin/users" className="action-card"><strong>Manage users</strong><span>Activate or deactivate platform accounts.</span></Link>
          <Link to="/admin/subscription-plans" className="action-card"><strong>Manage subscription plans</strong><span>Create plans, set doctor limits and control plan availability.</span></Link>
          <Link to="/admin/clinics" className="action-card"><strong>Manage clinics</strong><span>Review clinic accounts and platform availability.</span></Link>
        </div>
      </>}
    </AppLayout>
  );
}
