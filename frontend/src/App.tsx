import { Navigate, Route, Routes } from 'react-router-dom'
import RequireAuth from './auth/RequireAuth'
import Layout from './components/Layout'
import CamasPage from './pages/CamasPage'
import LoginPage from './pages/LoginPage'
import PacienteDetallePage from './pages/PacienteDetallePage'
import PacientesPage from './pages/PacientesPage'
import PerfilPage from './pages/PerfilPage'
import UsuariosPage from './pages/UsuariosPage'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<LoginPage />} />

      {/* Todo lo que está adentro de este bloque exige sesión iniciada y comparte el Layout (menú superior) */}
      <Route
        element={
          <RequireAuth>
            <Layout />
          </RequireAuth>
        }
      >
        <Route path="/camas" element={<CamasPage />} />
        <Route path="/perfil" element={<PerfilPage />} />
        <Route
          path="/pacientes"
          element={
            <RequireAuth roles={['ADMINISTRADOR', 'NUTRICIONISTA']}>
              <PacientesPage />
            </RequireAuth>
          }
        />
        <Route
          path="/pacientes/:id"
          element={
            <RequireAuth roles={['ADMINISTRADOR', 'NUTRICIONISTA']}>
              <PacienteDetallePage />
            </RequireAuth>
          }
        />
        <Route
          path="/usuarios"
          element={
            <RequireAuth roles={['ADMINISTRADOR']}>
              <UsuariosPage />
            </RequireAuth>
          }
        />
      </Route>

      <Route path="*" element={<Navigate to="/camas" replace />} />
    </Routes>
  )
}
