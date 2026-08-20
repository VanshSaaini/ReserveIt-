import { useEffect, useMemo, useState } from "react";
import AppLayout, {
  ErrorBox,
  Empty,
  Loading,
  StatCard,
} from "../components/AppLayout.jsx";
import { appointmentApi } from "../api/client.js";

const money = (value) =>
  `₹${Number(value || 0).toLocaleString("en-IN", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;

export default function ClinicAppointments() {
  const [a, setA] = useState(null);
  const [err, setErr] = useState("");
  const [reminderId, setReminderId] = useState(null);
  const [paymentId, setPaymentId] = useState(null);
  const [success, setSuccess] = useState("");
  const [doctorFilter, setDoctorFilter] = useState("ALL");
  const [statusFilter, setStatusFilter] = useState("ALL");
  const [paymentFilter, setPaymentFilter] = useState("ALL");

  const load = () => {
    setErr("");
    appointmentApi
      .clinicMine()
      .then(setA)
      .catch((e) => setErr(e.message));
  };

  useEffect(() => {
    load();
  }, []);

  const doctors = useMemo(
    () => [
      ...new Map((a || []).map((x) => [x.doctorId, x.doctorName])).entries(),
    ],
    [a],
  );
  const filtered = useMemo(
    () =>
      (a || []).filter(
        (x) =>
          (doctorFilter === "ALL" || String(x.doctorId) === doctorFilter) &&
          (statusFilter === "ALL" || x.status === statusFilter) &&
          (paymentFilter === "ALL" || x.paymentStatus === paymentFilter),
      ),
    [a, doctorFilter, statusFilter, paymentFilter],
  );

  const totals = useMemo(() => {
    const active = filtered.filter((x) => x.status !== "CANCELLED");
    return {
      revenue: active.reduce((n, x) => n + Number(x.price || 0), 0),
      collected: active
        .filter((x) => x.paymentStatus === "PAID")
        .reduce((n, x) => n + Number(x.price || 0), 0),
      pending: active
        .filter((x) => x.paymentStatus === "PENDING")
        .reduce((n, x) => n + Number(x.price || 0), 0),
    };
  }, [filtered]);

  async function sendReminder(id) {
    try {
      setReminderId(id);
      setErr("");
      setSuccess("");
      await appointmentApi.reminder(id);
      setSuccess("Reminder email sent successfully to the patient.");
    } catch (e) {
      setErr(e.message);
    } finally {
      setReminderId(null);
    }
  }

  async function togglePayment(id, paid) {
    try {
      setPaymentId(id);
      setErr("");
      setSuccess("");
      await appointmentApi.payment(id, paid);
      setSuccess(
        paid
          ? "Appointment marked as paid."
          : "Appointment payment moved back to pending.",
      );
      load();
    } catch (e) {
      setErr(e.message);
    } finally {
      setPaymentId(null);
    }
  }

  async function status(id, status) {
    try {
      await appointmentApi.status(id, { status, notes: "" });
      load();
    } catch (e) {
      setErr(e.message);
    }
  }

  function exportCsv() {
    const headers = [
      "Patient",
      "Doctor",
      "Service",
      "Date",
      "Start",
      "End",
      "Price",
      "Payment",
      "Status",
    ];
    const rows = filtered.map((x) => [
      x.patientName,
      x.doctorName,
      x.serviceName || "Consultation",
      x.appointmentDate,
      x.startTime,
      x.endTime,
      x.price || 0,
      x.paymentStatus,
      x.status,
    ]);
    const csv = [headers, ...rows]
      .map((row) =>
        row
          .map((value) => `"${String(value ?? "").replaceAll('"', '""')}"`)
          .join(","),
      )
      .join("\n");
    const blob = new Blob([csv], { type: "text/csv;charset=utf-8" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `reserveit-appointments-${new Date().toISOString().slice(0, 10)}.csv`;
    link.click();
    URL.revokeObjectURL(url);
  }

  return (
    <AppLayout
      title="Clinic appointments"
      subtitle="Appointment history, pricing, payment collection and reminders."
    >
      <ErrorBox message={err} />
      {success && <div className="form-alert">{success}</div>}

      {a && (
        <div className="stats-grid">
          <StatCard label="Filtered appointments" value={filtered.length} />
          <StatCard label="Booked revenue" value={money(totals.revenue)} />
          <StatCard label="Collected" value={money(totals.collected)} />
          <StatCard label="Pending payments" value={money(totals.pending)} />
        </div>
      )}

      <section className="panel">
        <div className="panel-head">
          <div>
            <h2>Appointment history</h2>
            <p>
              Every doctor appointment for your clinic, including historical
              price and payment status.
            </p>
          </div>
          <button
            className="btn btn--ghost"
            onClick={exportCsv}
            disabled={!filtered.length}
          >
            Export CSV
          </button>
        </div>
        <div className="form-row">
          <select
            value={doctorFilter}
            onChange={(e) => setDoctorFilter(e.target.value)}
          >
            <option value="ALL">All doctors</option>
            {doctors.map(([id, name]) => (
              <option key={id} value={id}>
                {name}
              </option>
            ))}
          </select>
          <select
            value={statusFilter}
            onChange={(e) => setStatusFilter(e.target.value)}
          >
            <option value="ALL">All statuses</option>
            <option value="CONFIRMED">Confirmed</option>
            <option value="RESCHEDULED">Rescheduled</option>
            <option value="COMPLETED">Completed</option>
            <option value="CANCELLED">Cancelled</option>
          </select>
          <select
            value={paymentFilter}
            onChange={(e) => setPaymentFilter(e.target.value)}
          >
            <option value="ALL">All payments</option>
            <option value="PAID">Paid</option>
            <option value="PENDING">Pending</option>
          </select>
        </div>
      </section>

      {!a ? (
        <Loading />
      ) : !filtered.length ? (
        <Empty />
      ) : (
        <section className="panel">
          <div className="table-wrap">
            <table>
              <thead>
                <tr>
                  <th>Patient</th>
                  <th>Doctor</th>
                  <th>Service</th>
                  <th>Date</th>
                  <th>Time</th>
                  <th>Price</th>
                  <th>Payment</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((x) => (
                  <tr key={x.id}>
                    <td>{x.patientName}</td>
                    <td>{x.doctorName}</td>
                    <td>{x.serviceName || "Consultation"}</td>
                    <td>{x.appointmentDate}</td>
                    <td>
                      {x.startTime} - {x.endTime}
                    </td>
                    <td>{money(x.price)}</td>
                    <td>
                      <span
                        className={`status ${x.paymentStatus === "PAID" ? "status--paid" : "status--pending"}`}
                      >
                        {x.paymentStatus}
                      </span>
                    </td>
                    <td>
                      <span className="status">{x.status}</span>
                    </td>
                    <td className="actions">
                      {x.status !== "CANCELLED" && x.status !== "COMPLETED" && (
                        <button
                          className="btn btn--ghost btn--sm"
                          onClick={() => sendReminder(x.id)}
                          disabled={reminderId === x.id}
                        >
                          {reminderId === x.id ? "Sending..." : "Send Reminder"}
                        </button>
                      )}
                      <button
                        className="btn btn--ghost btn--sm"
                        onClick={() =>
                          togglePayment(x.id, x.paymentStatus !== "PAID")
                        }
                        disabled={paymentId === x.id}
                      >
                        {paymentId === x.id
                          ? "Saving..."
                          : x.paymentStatus === "PAID"
                            ? "Mark Pending"
                            : "Mark Paid"}
                      </button>
                      {x.status !== "CANCELLED" && x.status !== "COMPLETED" && (
                        <button
                          className="btn btn--ghost btn--sm"
                          onClick={() => status(x.id, "CANCELLED")}
                        >
                          Cancel
                        </button>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      )}
    </AppLayout>
  );
}
