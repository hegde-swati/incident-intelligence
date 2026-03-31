import React, { useEffect, useMemo, useState } from 'react';
import ServiceCard from '../components/ServiceCard';
import { ServiceSummary, fetchServices } from '../services/api';

const Dashboard: React.FC = () => {
  const [services, setServices] = useState<ServiceSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [query, setQuery] = useState('');

  useEffect(() => {
    let isMounted = true;

    const loadServices = async () => {
      try {
        const data = await fetchServices();
        if (isMounted) {
          setServices(data);
          setError(null);
        }
      } catch {
        if (isMounted) {
          setError('Could not load services. Check API availability.');
        }
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    loadServices();
    const timer = window.setInterval(loadServices, 5000);

    return () => {
      isMounted = false;
      window.clearInterval(timer);
    };
  }, []);

  const filtered = useMemo(
    () => services.filter((service) => service.name.toLowerCase().includes(query.toLowerCase())),
    [query, services]
  );

  return (
    <main className="min-h-screen bg-slate-950 px-6 py-8">
      <div className="mx-auto max-w-6xl">
        <header className="mb-6 flex flex-col justify-between gap-4 sm:flex-row sm:items-end">
          <div>
            <h1 className="text-3xl font-bold text-slate-100">Incident Intelligence Dashboard</h1>
            <p className="mt-1 text-slate-400">Monitor service health and investigate issues quickly.</p>
          </div>
          <input
            type="text"
            value={query}
            onChange={(event) => setQuery(event.target.value)}
            placeholder="Search services..."
            className="w-full rounded-lg border border-slate-700 bg-slate-900 px-4 py-2 text-sm text-slate-200 outline-none ring-indigo-400 focus:ring sm:w-64"
          />
        </header>

        {loading && (
          <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {Array.from({ length: 6 }).map((_, idx) => (
              <div key={idx} className="h-40 animate-pulse rounded-xl border border-slate-800 bg-slate-900" />
            ))}
          </div>
        )}

        {error && <p className="rounded-lg bg-rose-500/10 p-4 text-rose-300">{error}</p>}

        {!loading && !error && (
          <section className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {filtered.map((service) => (
              <ServiceCard key={service.id} service={service} />
            ))}
            {filtered.length === 0 && (
              <p className="col-span-full rounded-lg border border-slate-800 bg-slate-900 p-5 text-slate-400">
                No services matched your search.
              </p>
            )}
          </section>
        )}
      </div>
    </main>
  );
};

export default Dashboard;
