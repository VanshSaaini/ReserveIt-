const BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

export function getToken() {
  return localStorage.getItem('reserveit_token')
}
export function getAuth() {
  try { return JSON.parse(localStorage.getItem('reserveit_auth') || 'null') } catch { return null }
}
export function logout() {
  localStorage.removeItem('reserveit_token')
  localStorage.removeItem('reserveit_auth')
  window.location.href = '/login'
}

async function request(path, { method='GET', body, token=getToken() }={}) {
  const headers = {}
  if (body !== undefined) headers['Content-Type']='application/json'
  if (token) headers.Authorization=`Bearer ${token}`
  const res=await fetch(`${BASE_URL}${path}`,{method,headers,body:body===undefined?undefined:JSON.stringify(body)})
  const type=res.headers.get('content-type') || ''
  const data=type.includes('application/json') ? await res.json() : null
  if(!res.ok) throw new Error(data?.message || `Request failed (${res.status})`)
  return data
}
const get=(p)=>request(p)
const post=(p,b)=>request(p,{method:'POST',body:b})
const put=(p,b)=>request(p,{method:'PUT',body:b})
const patch=(p,b)=>request(p,{method:'PATCH',body:b})
const del=(p)=>request(p,{method:'DELETE'})

export const authApi={
  login: async (credentials)=>{
    const data=await post('/api/auth/login',{...credentials,rememberMe:Boolean(credentials.rememberMe)})
    if(data?.token) localStorage.setItem('reserveit_token',data.token)
    localStorage.setItem('reserveit_auth',JSON.stringify(data))
    return data
  },
  register:(payload)=>post('/api/auth/register',payload)
}
export const clinicApi={
  list:(search='')=>get(`/api/clinics${search?`?search=${encodeURIComponent(search)}`:''}`),
  get:(id)=>get(`/api/clinics/${id}`),
  doctors:(id)=>get(`/api/clinics/${id}/doctors`),
  services:(id)=>get(`/api/clinics/${id}/services`),
  me:()=>get('/api/clinics/me'),
  update:(b)=>put('/api/clinics/me',b),
  myDoctors:()=>get('/api/clinics/me/doctors'),
  myServices:()=>get('/api/clinics/me/services'),
  createService:(b)=>post('/api/clinics/me/services',b),
}
export const doctorApi={
  get:(id)=>get(`/api/doctors/${id}`),
  slots:(id,date)=>get(`/api/doctors/${id}/availability/slots?date=${date}`),
  create:(b)=>post('/api/doctors',b),
  active:(id,active)=>patch(`/api/doctors/${id}/active`,{active}),
  me:()=>get('/api/doctors/me'),
  availability:()=>get('/api/doctors/me/availability'),
  addAvailability:(b)=>post('/api/doctors/me/availability',b),
  deleteAvailability:(id)=>del(`/api/doctors/me/availability/${id}`)
}
export const patientApi={
  me:()=>get('/api/patients/me'),
  update:(b)=>put('/api/patients/me',b),
}
export const appointmentApi={
  book:(b)=>post('/api/appointments',b),
  mine:()=>get('/api/appointments/me'),
  doctorMine:()=>get('/api/appointments/doctor/me'),
  clinicMine:()=>get('/api/appointments/clinic/me'),
  status:(id,b)=>patch(`/api/appointments/${id}/status`,b),
  reschedule:(id,b)=>patch(`/api/appointments/${id}/reschedule`,b),
  cancel:(id)=>patch(`/api/appointments/${id}/cancel`),
  reminder:(id)=>post(`/api/appointments/${id}/reminder`)
}
export const serviceApi={
  update:(id,b)=>put(`/api/services/${id}`,b),
  remove:(id)=>del(`/api/services/${id}`)
}
export const adminApi={
  users:()=>get('/api/admin/users'),
  userActive:(id,active)=>patch(`/api/admin/users/${id}/active`,{active}),
  clinics:()=>get('/api/admin/clinics'),
  clinicActive:(id,active)=>patch(`/api/admin/clinics/${id}/active`,{active})
}
