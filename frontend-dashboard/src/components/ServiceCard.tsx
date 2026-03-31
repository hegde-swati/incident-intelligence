import React from 'react';
import { Link } from 'react-router-dom';
import { ServiceSummary } from '../services/api';

interface ServiceCardProps {
  service: ServiceSummary;
}

const statusColor: Record<string, string> = {
  RUNNING: 'bg-emerald-500/15 text-emerald-400 border-emerald-500/20',
  ERROR: 'bg-rose-500/15 text-rose-400 border-rose-500/20',
  STOPPED: 'bg-slate-500/15 text-slate-300 border-slate-500/20'
};

const ServiceCard: React.FC<ServiceCardProps> = ({ service }) => {
  const badgeClass = statusColor[service.status] || statusColor.STOPPED;

  return (
    <Link
      to={`/services/${service.id}`}
      className="block rounded-xl border border-slate-800 bg-slate-900 p-5 shadow-lg transition hover:-translate-y-1 hover:border-slate-700"
    >
      <div className="mb-4 flex items-start justify-between gap-2">
        <h2 className="text-lg font-semibold text-slate-100">{service.name}</h2>
        <span className={`rounded-full border px-3 py-1 text-xs font-medium ${badgeClass}`}>
          {service.status}
        </span>
      </div>

      <div className="space-y-2 text-sm text-slate-300">
        <div className="flex justify-between">
          <span>CPU Usage</span>
          <span className="font-medium text-slate-100">{service.cpu}%</span>
        </div>
        <div className="flex justify-between">
          <span>Memory</span>
          <span className="font-medium text-slate-100">{service.memory} MB</span>
        </div>
        <div className="flex justify-between">
          <span>Disk</span>
          <span className="font-medium text-slate-100">{service.disk} MB</span>
        </div>
        <div className="pt-1 text-xs text-slate-400">
          Updated: {new Date(service.lastUpdated).toLocaleString()}
        </div>
      </div>
    </Link>
  );
};

export default ServiceCard;
