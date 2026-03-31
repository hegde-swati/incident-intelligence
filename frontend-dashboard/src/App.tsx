import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';
import Dashboard from './pages/Dashboard';
import ServiceDetails from './pages/ServiceDetails';

const App: React.FC = () => (
  <Routes>
    <Route path="/" element={<Dashboard />} />
    <Route path="/services/:id" element={<ServiceDetails />} />
    <Route path="*" element={<Navigate to="/" replace />} />
  </Routes>
);

export default App;
