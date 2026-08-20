import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import AppLayout, { StatCard, Loading, ErrorBox } from "../components/AppLayout.jsx";
import { clinicApi, appointmentApi } from "../api/client.js";

const money = (value) =>
  `₹${Number(value || 0).toLocaleString("en-IN", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
  })}`;

const todayString = () => new Date().toISOString().slice(0, 10);
const monthString = () => new Date().toISOString().slice(0, 7);

export default function ClinicDashboard() {
  const [c, setC] = useState(null);
  const [d, setD] = useState([]);
  const [s, setS] = useState([]);
  const [a, setA] = useState([]);
  const [analytics, setAnalytics] = useState(null);
  const [day, setDay] = useState(todayString());
  const [month, setMonth] = useState(monthString());
  const [err, setErr] = useState("");

  const loadDashboard = async () => {
    try {
      setErr("");
      const [clinic, doctors, services, appointments, report] = await Promise.all([
        clinicApi.me(),
        clinicApi.myDoctors(),
        clinicApi.myServices(),
        appointmentApi.clinicMine(),
        clinicApi.analytics(day, month),
      ]);
      setC(clinic);
      setD(doctors);
      setS(services);
      setA(appointments);
      setAnalytics(report);
    } catch (e) {
      setErr(e.message || "Failed to load clinic dashboard");
    }
  };

  useEffect(() => { loadDashboard(); }, [day, month]);

  return (
    <AppLayout title="Clinic dashboard" subtitle="Manage your clinic, team, appointment flow and financial performance.">
      <ErrorBox message={err} />
      {!c ? <Loading /> : <>
        <div className="stats-grid">
          <StatCard label="Doctors" value={d.length} />
          <StatCard label="Services" value={s.length} />
          <StatCard label="Appointments" value={a.length} />
          <StatCard label="Clinic status" value={c.active ? "Active" : "Paused"} />
        </div>

        {analytics && <>
          <section className="panel">
            <div className="panel-head">
              <div>
                <h2>Today's business</h2>
                <p>{analytics.reportDate} · selected day business and collection</p>
              </div>
              <div className="form-row">
                <label>Business day</label>
                <input type="date" value={day} onChange={(e) => setDay(e.target.value)} />
                <Link to="/clinic/appointments" className="btn btn--ghost">View appointments</Link>
              </div>
            </div>
            <div className="stats-grid">
              <StatCard label="Appointments" value={analytics.todayAppointments} />
              <StatCard label="Booked revenue" value={money(analytics.todayBookedRevenue)} />
              <StatCard label="Collected" value={money(analytics.todayCollectedRevenue)} />
              <StatCard label="Pending" value={money(analytics.todayPendingRevenue)} />
            </div>
          </section>

          <section className="panel">
            <div className="panel-head">
              <div>
                <h2>Monthly performance</h2>
                <p>{analytics.reportMonth} · selected month revenue and operational KPIs</p>
              </div>
              <div className="form-row">
                <label>Month</label>
                <input type="month" value={month} onChange={(e) => setMonth(e.target.value)} />
              </div>
            </div>
            <div className="stats-grid">
              <StatCard label="Monthly appointments" value={analytics.monthAppointments} />
              <StatCard label="Booked revenue" value={money(analytics.monthBookedRevenue)} />
              <StatCard label="Collected revenue" value={money(analytics.monthCollectedRevenue)} />
              <StatCard label="Pending collection" value={money(analytics.monthPendingRevenue)} />
            </div>
            <div className="quick-grid">
              <div className="action-card"><strong>{analytics.monthCompleted}</strong><span>Completed appointments</span></div>
              <div className="action-card"><strong>{analytics.monthCancelled}</strong><span>Cancelled appointments</span></div>
              <div className="action-card"><strong>{analytics.monthPaidAppointments}</strong><span>Paid appointments</span></div>
              <div className="action-card"><strong>{analytics.monthPendingPayments}</strong><span>Pending payments</span></div>
            </div>
          </section>

          <div className="analytics-grid">
            <section className="panel"><h2>Doctor performance</h2><p>Selected month's appointment volume and revenue by doctor.</p>
              {!analytics.doctors.length ? <p>No appointments in this month.</p> : analytics.doctors.map(x => <div className="mini-bar" key={x.doctorId}><div><strong>{x.doctorName}</strong><span>{x.appointments} appointments · {x.completed} completed</span></div><div><strong>{money(x.collectedRevenue)}</strong><span>{money(x.pendingRevenue)} pending</span></div></div>)}
            </section>
            <section className="panel"><h2>Top services</h2><p>Highest revenue-generating services in the selected month.</p>
              {!analytics.services.length ? <p>No service activity in this month.</p> : analytics.services.slice(0, 6).map(x => <div className="mini-bar" key={x.serviceId}><div><strong>{x.serviceName}</strong><span>{x.appointments} appointments</span></div><strong>{money(x.bookedRevenue)}</strong></div>)}
            </section>
          </div>
        </>}

        <div className="quick-grid">
          <Link to="/clinic/appointments" className="action-card"><strong>Appointment history & payments</strong><span>Review every doctor appointment, price, payment state and reminders.</span></Link>
          <Link to="/clinic/subscription" className="action-card"><strong>Manage subscription</strong><span>View your plan, usage, renewal date and subscription history.</span></Link>
          <Link to="/clinic/doctors" className="action-card"><strong>Add a doctor</strong><span>Create doctor accounts and set their practice details.</span></Link>
          <Link to="/clinic/services" className="action-card"><strong>Manage services</strong><span>Configure consultation types, durations and prices.</span></Link>
          <Link to="/clinic/settings" className="action-card"><strong>Clinic settings</strong><span>Maintain clinic contact and operational information.</span></Link>
        </div>

        <section className="panel"><div className="panel-head"><div><h2>{c.name}</h2><p>{c.address}</p></div><Link to="/clinic/settings" className="btn btn--ghost">Edit clinic</Link></div></section>
      </>}
    </AppLayout>
  );
}
