import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import AIAnalysisPanel from '../components/AIAnalysisPanel';
import LogsViewer from '../components/LogsViewer';
import { analyzeLogs, fetchServiceLogs } from '../services/api';
import { AIAnalysis, ServiceLog } from '../types';

const ServiceDetails = () => {
  const { id } = useParams();
  const serviceId = id ?? '';

  const [logs, setLogs] = useState<ServiceLog[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [analysis, setAnalysis] = useState<AIAnalysis | null>(null);
  const [analyzing, setAnalyzing] = useState(false);
  const [analysisError, setAnalysisError] = useState<string | null>(null);

  useEffect(() => {
    if (!serviceId) {
      setLoading(false);
      setError('Missing service identifier in URL.');
      return;
    }

    let active = true;

    const loadLogs = async () => {
      try {
        const data = await fetchServiceLogs(serviceId);
        if (active) {
          setError(null);
          setLogs(data);
        }
      } catch (err) {
        if (active) {
          setError('Unable to load logs for this service.');
        }
      } finally {
        if (active) {
          setLoading(false);
        }
      }
    };

    loadLogs();

    return () => {
      active = false;
    };
  }, [serviceId]);

  const handleAnalyze = async () => {
    if (!serviceId) return;

    try {
      setAnalyzing(true);
      setAnalysisError(null);
      const result = await analyzeLogs(serviceId, logs);
      setAnalysis(result);
    } catch (err) {
      setAnalysisError('AI analysis failed. Please retry in a moment.');
    } finally {
      setAnalyzing(false);
    }
  };

  return (
    <main className="mx-auto max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
      <div className="mb-6 flex items-center justify-between gap-4">
        <div>
          <Link to="/" className="text-sm text-cyan-400 hover:text-cyan-300">
            ← Back to dashboard
          </Link>
          <h1 className="mt-2 text-2xl font-bold text-white">Service Logs: {serviceId || 'Unknown'}</h1>
        </div>
      </div>

      {loading ? (
        <div className="h-80 animate-pulse rounded-xl border border-slate-800 bg-slate-900/60" />
      ) : error ? (
        <p className="rounded-lg border border-red-500/30 bg-red-500/10 p-4 text-red-300">{error}</p>
      ) : (
        <div className="grid gap-6 lg:grid-cols-[2fr,1fr]">
          <section>
            <h2 className="mb-3 text-lg font-semibold text-white">Runtime Logs</h2>
            <LogsViewer logs={logs} />
          </section>

          <AIAnalysisPanel onAnalyze={handleAnalyze} analysis={analysis} isAnalyzing={analyzing} error={analysisError} />
        </div>
      )}
    </main>
  );
};

export default ServiceDetails;
