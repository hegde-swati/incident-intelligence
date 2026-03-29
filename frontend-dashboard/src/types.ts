export type ServiceStatus = 'RUNNING' | 'STOPPED' | 'ERROR';

export interface ServiceMetric {
  id: string;
  name: string;
  status: ServiceStatus;
  cpu: number;
  memory: number;
  disk: number;
  lastUpdated: string;
}

export type LogLevel = 'INFO' | 'WARN' | 'ERROR';

export interface ServiceLog {
  timestamp: string;
  level: LogLevel;
  message: string;
}

export interface AIAnalysis {
  rootCause: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL' | string;
  suggestedFix: string;
}
