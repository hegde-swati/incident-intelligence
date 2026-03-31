import React, { useState } from 'react';
import { AIAnalysisResult, ServiceLog, analyzeLogs } from '../services/api';

interface AIAnalysisPanelProps {
  serviceId: string;
  logs: ServiceLog[];
}

const severityClass: Record<string, string> = {
  LOW: 'bg-emerald-500/20 text-emerald-300',
  MEDIUM: 'bg-amber-500/20 text-amber-300',
  HIGH: 'bg-orange-500/20 text-orange-300',
  CRITICAL: 'bg-rose-500/20 text-rose-300'
};

const AIAnalysisPanel: React.FC<AIAnalysisPanelProps> = ({ serviceId, logs }) => {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [analysis, setAnalysis] = useState<AIAnalysisResult | null>(null);

  const handleAnalyze = async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await analyzeLogs(serviceId, logs);
      setAnalysis(result);
    } catch {
      setError('Unable to analyze logs right now. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <aside className="rounded-xl border border-slate-800 bg-slate-900 p-5">
      <h3 className="text-lg font-semibold text-slate-100">AI Analysis</h3>
      <p className="mt-1 text-sm text-slate-400">Get root-cause hints and suggested remediation.</p>

      <button
        onClick={handleAnalyze}
        disabled={loading || logs.length === 0}
        className="mt-4 w-full rounded-lg bg-indigo-500 px-4 py-2 text-sm font-semibold text-white transition hover:bg-indigo-400 disabled:cursor-not-allowed disabled:bg-indigo-900"
      >
        {loading ? 'Analyzing...' : 'Analyze Logs with AI'}
      </button>

      {error && <p className="mt-4 rounded-lg bg-rose-500/10 p-3 text-sm text-rose-300">{error}</p>}

      {analysis && (
        <div className="mt-5 space-y-4 text-sm">
          <div>
            <p className="text-slate-400">Root Cause</p>
            <p className="mt-1 text-slate-100">{analysis.rootCause}</p>
          </div>

          <div>
            <p className="text-slate-400">Severity</p>
            <span
              className={`mt-1 inline-block rounded-full px-3 py-1 text-xs font-semibold ${
                severityClass[analysis.severity] || 'bg-slate-500/20 text-slate-300'
              }`}
            >
              {analysis.severity}
            </span>
          </div>

          <div>
            <p className="text-slate-400">Suggested Fix</p>
            <p className="mt-1 text-slate-100">{analysis.suggestedFix}</p>
          </div>
        </div>
      )}
    </aside>
  );
};

export default AIAnalysisPanel;
