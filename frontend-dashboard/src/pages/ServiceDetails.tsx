import React, { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import AIAnalysisPanel from '../components/AIAnalysisPanel';
import LogsViewer from '../components/LogsViewer';
import { ServiceLog, serviceLogStreamUrl } from '../services/api';

const MAX_LOGS = 500;

const ServiceDetails: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const [logs, setLogs] = useState<ServiceLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) {
      setError('Invalid service identifier.');
      setLoading(false);
      return;
    }

    setLogs([]);
    setLoading(true);
    setError(null);

    const stream = new EventSource(serviceLogStreamUrl(id));

    stream.addEventListener('log', (event) => {
      const nextLog = JSON.parse((event as MessageEvent).data) as ServiceLog;
      setLogs((current) => {
        const updated = [...current, nextLog];
        return updated.length > MAX_LOGS ? updated.slice(updated.length - MAX_LOGS) : updated;
      });
      setLoading(false);
    });

    stream.onerror = () => {
      setError('Live log stream disconnected. Please refresh and try again.');
      setLoading(false);
      stream.close();
    };

    return () => {
      stream.close();
    };
  }, [id]);

  return (
    <main className="min-h-screen bg-slate-950 px-6 py-8">
      <div className="mx-auto max-w-6xl">
        <Link to="/" className="mb-5 inline-block text-sm text-indigo-300 hover:text-indigo-200">
          ← Back to dashboard
        </Link>

        <h1 className="text-2xl font-bold text-slate-100">Service Details: {id}</h1>
        <p className="mt-1 text-slate-400">View live logs and run AI-assisted analysis.</p>

        {loading && <div className="mt-6 h-64 animate-pulse rounded-xl bg-slate-900" />}

        {error && <p className="mt-6 rounded-lg bg-rose-500/10 p-4 text-rose-300">{error}</p>}

        {!error && id && (
          <div className="mt-6 grid grid-cols-1 gap-6 lg:grid-cols-3">
            <section className="lg:col-span-2">
              <h2 className="mb-3 text-lg font-semibold text-slate-200">Service Logs</h2>
              <LogsViewer logs={logs} />
            </section>
            <AIAnalysisPanel serviceId={id} logs={logs} />
          </div>
        )}
      </div>
    </main>
  );
};

export default ServiceDetails;
