import axios from 'axios';

const api = axios.create({
  baseURL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8084',
  timeout: 10000
});

export type ServiceStatus = 'RUNNING' | 'STOPPED' | 'ERROR';

export interface ServiceSummary {
  id: string;
  name: string;
  status: ServiceStatus;
  cpu: number;
  memory: number;
  disk: number;
  lastUpdated: string;
}

export interface ServiceLog {
  timestamp: string;
  level: 'INFO' | 'WARN' | 'ERROR' | string;
  message: string;
}

export interface AIAnalysisResult {
  rootCause: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL' | string;
  suggestedFix: string;
}

export const fetchServices = async (): Promise<ServiceSummary[]> => {
  const { data } = await api.get<ServiceSummary[]>('/api/services');
  return data;
};

export const fetchServiceLogs = async (serviceId: string): Promise<ServiceLog[]> => {
  const { data } = await api.get<ServiceLog[]>(`/api/services/${serviceId}/logs`);
  return data;
};

export const analyzeLogs = async (
  serviceId: string,
  logs: ServiceLog[]
): Promise<AIAnalysisResult> => {
  const { data } = await api.post<AIAnalysisResult>('/api/ai/analyze', {
    serviceId,
    logs
  });

  return data;
};

export default api;
