import { useEffect, useState } from "react";
import AppLayout, { Loading, ErrorBox } from "../components/AppLayout.jsx";
import { doctorApi } from "../api/client.js";
export default function DoctorProfile() {
  const [d, setD] = useState(null),
    [err, setErr] = useState("");
  useEffect(
    () =>
      doctorApi
        .me()
        .then(setD)
        .catch((e) => setErr(e.message)),
    [],
  );
  return (
    <AppLayout
      title="My profile"
      subtitle="Your professional profile as patients see it."
    >
      <ErrorBox message={err} />
      {!d ? (
        <Loading />
      ) : (
        <section className="panel profile-card">
          <div className="avatar avatar--large">{d.firstName?.[0]}</div>
          <div>
            <h2>
              Dr. {d.firstName} {d.lastName}
            </h2>
            <p>{d.specialization || "General practitioner"}</p>
            <p>{d.qualifications || "Qualifications not provided"}</p>
            <p>
              {d.experienceYears ?? 0} years experience · {d.defaultSlotMinutes}{" "}
              minute slots
            </p>
            <p>{d.clinicName}</p>
          </div>
        </section>
      )}
    </AppLayout>
  );
}
