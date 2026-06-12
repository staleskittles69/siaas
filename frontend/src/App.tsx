import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'
import DashboardPage from './pages/DashboardPage'
import DashboardLayout from './components/layout/DashboardLayout'
import ProtectedRoute from './components/ProtectedRoute'

function PlaceholderPage({ title }: { title: string }) {
  return (
    <div className="flex items-center justify-center min-h-screen">
      <div className="text-center p-8 rounded-2xl border border-white/10 bg-white/5 backdrop-blur-sm">
        <h1 className="text-2xl font-bold text-white">{title}</h1>
        <p className="text-slate-400 mt-2 text-sm">Coming soon</p>
      </div>
    </div>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public */}
        <Route path="/login"    element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />

        {/* Student — all wrapped in DashboardLayout */}
        <Route element={<ProtectedRoute allowedRoles={['STUDENT']} />}>
          <Route element={<DashboardLayout />}>
            <Route path="/dashboard"       element={<DashboardPage />} />
            <Route path="/academics"       element={<PlaceholderPage title="Academics" />} />
            <Route path="/attendance"      element={<PlaceholderPage title="Attendance" />} />
            <Route path="/analytics"       element={<PlaceholderPage title="Analytics" />} />
            <Route path="/planner"         element={<PlaceholderPage title="CGPA Planner" />} />
            <Route path="/recommendations" element={<PlaceholderPage title="Recommendations" />} />
            <Route path="/reports"         element={<PlaceholderPage title="Reports" />} />
            <Route path="/notifications"   element={<PlaceholderPage title="Notifications" />} />
            <Route path="/profile"         element={<PlaceholderPage title="Profile" />} />
          </Route>
        </Route>

        {/* Faculty */}
        <Route element={<ProtectedRoute allowedRoles={['FACULTY']} />}>
          <Route path="/faculty/*" element={<PlaceholderPage title="Faculty Panel" />} />
        </Route>

        {/* Admin */}
        <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
          <Route path="/admin/*" element={<PlaceholderPage title="Admin Panel" />} />
        </Route>

        {/* Fallback */}
        <Route path="/" element={<Navigate to="/dashboard" replace />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </BrowserRouter>
  )
}
