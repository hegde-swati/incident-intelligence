import { useEffect, useMemo, useState } from 'react';
import ServiceCard from '../components/ServiceCard';
import { fetchServices } from '../services/api';
import { ServiceMetric } from '../types';

const Dashboard = () => {
  const [services, setServices] = useState<ServiceMetric[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [search, setSearch] = useState('');

  useEffect(() => {
    let active = true;

    const load = async () => {
      try {
        setError(null);
        const data = await fetchServices();
        if (active) {
          setServices(data);
        }
      } catch (err) {
        if (active) {
          setError('Unable to load services right now. Please try again.');
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    load();
    const intervalId = window.setInterval(load, 5000);

    return () => {
      active = false;
      window.clearInterval(intervalId);
    };
  }, []);

  const filteredServices = useMemo(
    () => services.filter((service) => service.name.toLowerCase().includes(search.toLowerCase())),
    [services, search]
  );

  return (
    <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
      <header className="mb-6 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h1 className="text-3xl font-bold text-white">Incident Intelligence Dashboard</h1>
          <p className="mt-1 text-sm text-slate-400">Live service health and infrastructure metrics.</p>
        </div>
        <input
          type="text"
          value={search}
          onChange={(event) => setSearch(event.target.value)}
          placeholder="Search services..."
          className="w-full rounded-lg border border-slate-700 bg-slate-900 px-3 py-2 text-sm text-slate-100 placeholder:text-slate-500 focus:border-cyan-500 focus:outline-none sm:w-72"
        />
      </header>

      {loading ? (
        <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {Array.from({ length: 6 }).map((_, index) => (
            <div key={index} className="h-44 animate-pulse rounded-xl border border-slate-800 bg-slate-900/60" />
          ))}
        </div>
      ) : error ? (
        <p className="rounded-lg border border-red-500/30 bg-red-500/10 p-4 text-red-300">{error}</p>
      ) : filteredServices.length === 0 ? (
        <p className="rounded-lg border border-slate-800 bg-slate-900/60 p-4 text-slate-300">No services match your search.</p>
      ) : (
        <section className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
          {filteredServices.map((service) => (
            <ServiceCard key={service.id} service={service} />
          ))}
        </section>
      )}
    </main>
  );
};

export default Dashboard;
