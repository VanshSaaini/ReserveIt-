import { useEffect, useState } from "react";
import AppLayout, { ErrorBox, Empty, Loading } from "../components/AppLayout.jsx";
import { adminApi } from "../api/client.js";

export default function AdminUsers() {
  const [clinics, setClinics] = useState(null);
  const [expandedClinics, setExpandedClinics] = useState({});
  const [expandedDoctors, setExpandedDoctors] = useState({});
  const [err, setErr] = useState("");

  const load = async () => {
    try { setErr(""); setClinics(await adminApi.userHierarchy()); }
    catch (e) { setErr(e.message); }
  };
  useEffect(() => { load(); }, []);

  const toggleClinic = id => setExpandedClinics(x => ({ ...x, [id]: !x[id] }));
  const toggleDoctor = id => setExpandedDoctors(x => ({ ...x, [id]: !x[id] }));

  return (
    <AppLayout title="Users" subtitle="Platform-wide account administration organized by Clinic → Doctor → Patients.">
      <ErrorBox message={err} />
      {!clinics ? <Loading /> : !clinics.length ? <Empty /> : <section className="panel">
        {clinics.map(clinic => <div className="data-row" key={clinic.clinicId} style={{ display: "block" }}>
          <button className="btn btn--ghost" onClick={() => toggleClinic(clinic.clinicId)}>{expandedClinics[clinic.clinicId] ? "−" : "+"}</button>
          <strong style={{ marginLeft: 10 }}>{clinic.clinicName}</strong>
          <span style={{ marginLeft: 10 }}>{clinic.clinicActive ? "Active" : "Inactive"} · {clinic.doctorCount} doctors · {clinic.patientCount} patients · {clinic.subscriptionPlan || "No plan"}</span>
          {expandedClinics[clinic.clinicId] && <div style={{ marginTop: 14, paddingLeft: 28 }}>
            {!clinic.doctors.length ? <Empty /> : clinic.doctors.map(doctor => <div className="data-row" key={doctor.doctorId} style={{ display: "block" }}>
              <button className="btn btn--ghost" onClick={() => toggleDoctor(doctor.doctorId)}>{expandedDoctors[doctor.doctorId] ? "−" : "+"}</button>
              <strong style={{ marginLeft: 10 }}>Dr. {doctor.name}</strong>
              <span style={{ marginLeft: 10 }}>{doctor.specialization || "General"} · {doctor.active ? "Active" : "Inactive"} · {doctor.patientCount} patients</span>
              {expandedDoctors[doctor.doctorId] && <div style={{ marginTop: 12, paddingLeft: 28 }}>
                {!doctor.patients.length ? <Empty>No patients linked through appointments.</Empty> : <div className="table-wrap"><table><thead><tr><th>Patient</th><th>Email</th><th>Mobile</th><th>Status</th><th>DOB</th></tr></thead><tbody>{doctor.patients.map(patient => <tr key={patient.patientId}><td>{patient.name}</td><td>{patient.email}</td><td>{patient.mobile}</td><td>{patient.active ? "Active" : "Inactive"}</td><td>{patient.dateOfBirth || "—"}</td></tr>)}</tbody></table></div>}
              </div>}
            </div>)}
          </div>}
        </div>)}
      </section>}
    </AppLayout>
  );
}
