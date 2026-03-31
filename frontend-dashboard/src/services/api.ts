import axios from 'axios';
import { AIAnalysis, ServiceLog, ServiceMetric } from '../types';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
  timeout: 10000
});

export const fetchServices = async (): Promise<ServiceMetric[]> => {
  const response = await api.get<ServiceMetric[]>('/api/services');
  return response.data;
};

export const fetchServiceLogs = async (id: string): Promise<ServiceLog[]> => {
  const response = await api.get<ServiceLog[]>(`/api/services/${id}/logs`);
  return response.data;
};

export const analyzeLogs = async (serviceId: string, logs: ServiceLog[]): Promise<AIAnalysis> => {
  const response = await api.post<AIAnalysis>('/api/ai/analyze', { serviceId, logs });
  return response.data;
};

export default api;
