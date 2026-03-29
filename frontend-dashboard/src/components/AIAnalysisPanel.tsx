import { AIAnalysis } from '../types';

interface AIAnalysisPanelProps {
  onAnalyze: () => Promise<void>;
  analysis: AIAnalysis | null;
  isAnalyzing: boolean;
  error: string | null;
}

const severityStyle = (severity: string) => {
  switch (severity.toUpperCase()) {
    case 'CRITICAL':
      return 'bg-fuchsia-500/20 text-fuchsia-300 border-fuchsia-400/30';
    case 'HIGH':
      return 'bg-red-500/20 text-red-300 border-red-400/30';
    case 'MEDIUM':
      return 'bg-amber-500/20 text-amber-300 border-amber-400/30';
    default:
      return 'bg-emerald-500/20 text-emerald-300 border-emerald-400/30';
  }
};

const AIAnalysisPanel = ({ onAnalyze, analysis, isAnalyzing, error }: AIAnalysisPanelProps) => {
  return (
    <aside className="rounded-xl border border-slate-800 bg-slate-900/80 p-5">
      <div className="mb-4 flex items-center justify-between">
        <h2 className="text-lg font-semibold text-white">AI Log Analysis</h2>
      </div>

      <button
        type="button"
        onClick={onAnalyze}
        disabled={isAnalyzing}
        className="mb-5 w-full rounded-lg bg-cyan-500 px-4 py-2 font-semibold text-slate-900 transition hover:bg-cyan-400 disabled:cursor-not-allowed disabled:bg-cyan-700/40 disabled:text-slate-300"
      >
        {isAnalyzing ? 'Analyzing…' : 'Analyze Logs with AI'}
      </button>

      {error && <p className="mb-4 rounded-md border border-red-500/30 bg-red-500/10 p-3 text-sm text-red-300">{error}</p>}

      {!analysis ? (
        <p className="text-sm text-slate-400">Run analysis to view root cause and suggested remediation.</p>
      ) : (
        <div className="space-y-4 text-sm text-slate-200">
          <section>
            <p className="mb-1 text-xs uppercase tracking-wide text-slate-400">Root Cause</p>
            <p className="rounded-md bg-slate-950 p-3">{analysis.rootCause}</p>
          </section>
          <section>
            <p className="mb-1 text-xs uppercase tracking-wide text-slate-400">Severity</p>
            <span className={`inline-block rounded-full border px-3 py-1 text-xs font-semibold ${severityStyle(analysis.severity)}`}>
              {analysis.severity}
            </span>
          </section>
          <section>
            <p className="mb-1 text-xs uppercase tracking-wide text-slate-400">Suggested Fix</p>
            <p className="rounded-md bg-slate-950 p-3">{analysis.suggestedFix}</p>
          </section>
        </div>
      )}
    </aside>
  );
};

export default AIAnalysisPanel;
