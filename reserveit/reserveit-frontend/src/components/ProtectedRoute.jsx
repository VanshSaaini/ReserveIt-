import { Navigate } from 'react-router-dom'
import { getAuth, getToken } from '../api/client.js'
export default function ProtectedRoute({roles,children}){
  const auth=getAuth()
  if(!getToken() || !auth) return <Navigate to="/login" replace/>
  if(roles && !roles.includes(auth.role)) return <Navigate to={`/${String(auth.role||'PATIENT').toLowerCase().replace('_','-')}`} replace/>
  return children
}
