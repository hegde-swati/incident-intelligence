import React, { useEffect, useMemo, useRef } from 'react';
import { ServiceLog } from '../services/api';

interface LogsViewerProps {
  logs: ServiceLog[];
}

const levelColor: Record<string, string> = {
  INFO: 'text-cyan-300',
  WARN: 'text-amber-300',
  ERROR: 'text-rose-300'
};

const LogsViewer: React.FC<LogsViewerProps> = ({ logs }) => {
  const endRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [logs]);

  const empty = useMemo(() => logs.length === 0, [logs.length]);

  return (
    <div className="h-[440px] overflow-y-auto rounded-xl border border-slate-800 bg-slate-950 p-4 font-mono text-sm">
      {empty ? (
        <p className="text-slate-400">No logs available for this service.</p>
      ) : (
        <div className="space-y-2">
          {logs.map((log, idx) => (
            <div key={`${log.timestamp}-${idx}`} className="whitespace-pre-wrap text-slate-200">
              <span className="text-slate-500">[{new Date(log.timestamp).toLocaleTimeString()}]</span>{' '}
              <span className={levelColor[log.level] || 'text-slate-200'}>[{log.level}]</span>{' '}
              <span>{log.message}</span>
            </div>
          ))}
          <div ref={endRef} />
        </div>
      )}
    </div>
  );
};

export default LogsViewer;
