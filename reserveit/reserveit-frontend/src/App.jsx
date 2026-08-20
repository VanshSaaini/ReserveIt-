import { Routes, Route, Navigate } from "react-router-dom";
import Home from "./pages/Home.jsx";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import ProtectedRoute from "./components/ProtectedRoute.jsx";
import PatientDashboard from "./pages/PatientDashboard.jsx";
import PatientBook from "./pages/PatientBook.jsx";
import PatientAppointments from "./pages/PatientAppointments.jsx";
import PatientProfile from "./pages/PatientProfile.jsx";
import ClinicDashboard from "./pages/ClinicDashboard.jsx";
import ClinicDoctors from "./pages/ClinicDoctors.jsx";
import ClinicServices from "./pages/ClinicServices.jsx";
import ClinicAppointments from "./pages/ClinicAppointments.jsx";
import ClinicSettings from "./pages/ClinicSettings.jsx";
import DoctorDashboard from "./pages/DoctorDashboard.jsx";
import DoctorAppointments from "./pages/DoctorAppointments.jsx";
import DoctorAvailability from "./pages/DoctorAvailability.jsx";
import DoctorProfile from "./pages/DoctorProfile.jsx";
import AdminDashboard from "./pages/AdminDashboard.jsx";
import AdminUsers from "./pages/AdminUsers.jsx";
import AdminClinics from "./pages/AdminClinics.jsx";
import AdminSubscriptionPlans from "./pages/AdminSubscriptionPlans.jsx";
import ClinicSubscription from "./pages/ClinicSubscription.jsx";

const R = ({ roles, children }) => (
  <ProtectedRoute roles={roles}>{children}</ProtectedRoute>
);
export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route
        path="/patient"
        element={
          <R roles={["PATIENT"]}>
            <PatientDashboard />
          </R>
        }
      />
      <Route
        path="/patient/book"
        element={
          <R roles={["PATIENT"]}>
            <PatientBook />
          </R>
        }
      />
      <Route
        path="/patient/appointments"
        element={
          <R roles={["PATIENT"]}>
            <PatientAppointments />
          </R>
        }
      />
      <Route
        path="/patient/profile"
        element={
          <R roles={["PATIENT"]}>
            <PatientProfile />
          </R>
        }
      />
      <Route
        path="/clinic"
        element={
          <R roles={["CLINIC_ADMIN"]}>
            <ClinicDashboard />
          </R>
        }
      />
      <Route
        path="/clinic/doctors"
        element={
          <R roles={["CLINIC_ADMIN"]}>
            <ClinicDoctors />
          </R>
        }
      />
      <Route
        path="/clinic/services"
        element={
          <R roles={["CLINIC_ADMIN"]}>
            <ClinicServices />
          </R>
        }
      />
      <Route
        path="/clinic/appointments"
        element={
          <R roles={["CLINIC_ADMIN"]}>
            <ClinicAppointments />
          </R>
        }
      />
      <Route
        path="/clinic/subscription"
        element={
          <R roles={["CLINIC_ADMIN"]}>
            <ClinicSubscription />
          </R>
        }
      />
      <Route
        path="/clinic/settings"
        element={
          <R roles={["CLINIC_ADMIN"]}>
            <ClinicSettings />
          </R>
        }
      />
      <Route
        path="/doctor"
        element={
          <R roles={["DOCTOR"]}>
            <DoctorDashboard />
          </R>
        }
      />
      <Route
        path="/doctor/appointments"
        element={
          <R roles={["DOCTOR"]}>
            <DoctorAppointments />
          </R>
        }
      />
      <Route
        path="/doctor/availability"
        element={
          <R roles={["DOCTOR"]}>
            <DoctorAvailability />
          </R>
        }
      />
      <Route
        path="/doctor/profile"
        element={
          <R roles={["DOCTOR"]}>
            <DoctorProfile />
          </R>
        }
      />
      <Route
        path="/admin"
        element={
          <R roles={["SUPER_ADMIN"]}>
            <AdminDashboard />
          </R>
        }
      />
      <Route
        path="/admin/users"
        element={
          <R roles={["SUPER_ADMIN"]}>
            <AdminUsers />
          </R>
        }
      />
      <Route
        path="/admin/subscription-plans"
        element={
          <R roles={["SUPER_ADMIN"]}>
            <AdminSubscriptionPlans />
          </R>
        }
      />
      <Route
        path="/admin/clinics"
        element={
          <R roles={["SUPER_ADMIN"]}>
            <AdminClinics />
          </R>
        }
      />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
