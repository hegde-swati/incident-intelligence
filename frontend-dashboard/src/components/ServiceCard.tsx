import { Link } from 'react-router-dom';
import { ServiceMetric, ServiceStatus } from '../types';

interface ServiceCardProps {
  service: ServiceMetric;
}

const statusStyles: Record<ServiceStatus, string> = {
  RUNNING: 'bg-emerald-500/20 text-emerald-300 border-emerald-500/30',
  ERROR: 'bg-red-500/20 text-red-300 border-red-500/30',
  STOPPED: 'bg-slate-500/20 text-slate-300 border-slate-500/30'
};

const ServiceCard = ({ service }: ServiceCardProps) => {
  return (
    <Link
      to={`/services/${service.id}`}
      className="block rounded-xl border border-slate-800 bg-slate-900/70 p-5 transition hover:border-cyan-500/60 hover:bg-slate-900"
    >
      <div className="mb-4 flex items-center justify-between gap-2">
        <h3 className="text-lg font-semibold text-white">{service.name}</h3>
        <span className={`rounded-full border px-3 py-1 text-xs font-medium ${statusStyles[service.status]}`}>
          {service.status}
        </span>
      </div>

      <div className="space-y-2 text-sm text-slate-300">
        <p>CPU: <span className="font-medium text-slate-100">{service.cpu}%</span></p>
        <p>Memory: <span className="font-medium text-slate-100">{service.memory} MB</span></p>
        <p>Disk: <span className="font-medium text-slate-100">{service.disk} MB</span></p>
      </div>

      <p className="mt-4 text-xs text-slate-400">
        Last updated: {new Date(service.lastUpdated).toLocaleString()}
      </p>
    </Link>
  );
};

export default ServiceCard;
