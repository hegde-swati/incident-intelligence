import { useEffect, useMemo, useRef } from 'react';
import { ServiceLog } from '../types';

interface LogsViewerProps {
  logs: ServiceLog[];
}

const levelColorMap: Record<string, string> = {
  INFO: 'text-cyan-300',
  WARN: 'text-amber-300',
  ERROR: 'text-red-300'
};

const LogsViewer = ({ logs }: LogsViewerProps) => {
  const terminalRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const container = terminalRef.current;
    if (container) {
      container.scrollTop = container.scrollHeight;
    }
  }, [logs]);

  const normalizedLogs = useMemo(() => logs ?? [], [logs]);

  return (
    <div
      ref={terminalRef}
      className="h-[28rem] overflow-y-auto rounded-xl border border-slate-800 bg-black/50 p-4 font-mono text-sm"
    >
      {normalizedLogs.length === 0 ? (
        <p className="text-slate-500">No logs available for this service.</p>
      ) : (
        <div className="space-y-2">
          {normalizedLogs.map((log, index) => (
            <div key={`${log.timestamp}-${index}`} className="leading-relaxed">
              <span className="mr-3 text-slate-500">[{new Date(log.timestamp).toLocaleTimeString()}]</span>
              <span className={`mr-2 font-semibold ${levelColorMap[log.level] || 'text-slate-200'}`}>
                {log.level}
              </span>
              <span className="text-slate-200">{log.message}</span>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default LogsViewer;
