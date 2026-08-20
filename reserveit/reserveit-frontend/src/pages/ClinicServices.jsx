import { useEffect, useState } from "react";
import AppLayout, { ErrorBox, Empty } from "../components/AppLayout.jsx";
import { clinicApi, serviceApi } from "../api/client.js";

const blank = {
  name: "",
  description: "",
  durationMinutes: 30,
  price: "",
};

export default function ClinicServices() {
  const [s, setS] = useState([]);
  const [f, setF] = useState(blank);
  const [err, setErr] = useState("");

  const load = () => {
    clinicApi
      .myServices()
      .then((data) => {
        setS(data);
      })
      .catch((e) => {
        setErr(e.message);
      });
  };

  useEffect(() => {
    load();
  }, []);
  const ch = (k) => (e) => setF({ ...f, [k]: e.target.value });
  async function add(e) {
    e.preventDefault();
    try {
      await clinicApi.createService({
        ...f,
        durationMinutes: Number(f.durationMinutes),
        price: f.price || null,
      });
      setF(blank);
      load();
    } catch (e) {
      setErr(e.message);
    }
  }
  async function remove(id) {
    try {
      await serviceApi.remove(id);
      load();
    } catch (e) {
      setErr(e.message);
    }
  }
  return (
    <AppLayout
      title="Services"
      subtitle="Define what patients can book at your clinic."
    >
      <ErrorBox message={err} />
      <div className="two-col">
        <section className="panel">
          <h2>Add service</h2>
          <form className="auth-form" onSubmit={add}>
            <input
              placeholder="Service name"
              value={f.name}
              onChange={ch("name")}
              required
            />
            <textarea
              placeholder="Description"
              value={f.description}
              onChange={ch("description")}
            />
            <div className="form-row">
              <input
                type="number"
                placeholder="Duration (minutes)"
                value={f.durationMinutes}
                onChange={ch("durationMinutes")}
              />
              <input
                type="number"
                step="0.01"
                placeholder="Price"
                value={f.price}
                onChange={ch("price")}
              />
            </div>
            <button className="btn btn--primary">Add service</button>
          </form>
        </section>
        <section className="panel">
          <h2>Active services</h2>
          {s.map((x) => (
            <div className="data-row" key={x.id}>
              <div>
                <strong>{x.name}</strong>
                <span>
                  {x.durationMinutes} min ·{" "}
                  {x.price != null ? `₹${x.price}` : "Price on request"}
                </span>
              </div>
              <button
                className="btn btn--danger btn--sm"
                onClick={() => remove(x.id)}
              >
                Remove
              </button>
            </div>
          ))}
          {!s.length && <Empty />}
        </section>
      </div>
    </AppLayout>
  );
}
