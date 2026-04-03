import { useState } from "react";

const API_BASE = "http://localhost:8080";

const styles = `
  @import url('https://fonts.googleapis.com/css2?family=JetBrains+Mono:wght@400;500&family=Syne:wght@400;500;600;700&display=swap');

  * { box-sizing: border-box; margin: 0; padding: 0; }

  body {
    font-family: 'Syne', sans-serif;
    background: #0a0a0f;
    color: #e8e6f0;
    min-height: 100vh;
  }

  .mono { font-family: 'JetBrains Mono', monospace; }

  .page { min-height: 100vh; display: flex; flex-direction: column; }

  /* ── Landing ── */
  .landing {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 2rem;
    position: relative;
    overflow: hidden;
  }

  .grid-bg {
    position: absolute; inset: 0;
    background-image:
      linear-gradient(rgba(99,71,255,0.07) 1px, transparent 1px),
      linear-gradient(90deg, rgba(99,71,255,0.07) 1px, transparent 1px);
    background-size: 48px 48px;
    pointer-events: none;
  }

  .glow-orb {
    position: absolute;
    width: 600px; height: 600px;
    border-radius: 50%;
    background: radial-gradient(circle, rgba(99,71,255,0.12) 0%, transparent 70%);
    top: 50%; left: 50%;
    transform: translate(-50%, -60%);
    pointer-events: none;
  }

  .landing-content { position: relative; z-index: 1; text-align: center; max-width: 680px; }

  .badge {
    display: inline-flex; align-items: center; gap: 6px;
    padding: 6px 14px;
    border: 1px solid rgba(99,71,255,0.4);
    border-radius: 100px;
    font-size: 12px; font-weight: 500; letter-spacing: 0.08em;
    color: #a89fff;
    margin-bottom: 2rem;
    background: rgba(99,71,255,0.08);
  }

  .badge-dot { width: 6px; height: 6px; border-radius: 50%; background: #7c5cfc; }

  .landing h1 {
    font-size: clamp(2.8rem, 7vw, 5rem);
    font-weight: 700;
    line-height: 1.05;
    letter-spacing: -0.03em;
    margin-bottom: 1.5rem;
    color: #f0eeff;
  }

  .landing h1 span { color: #7c5cfc; }

  .landing p {
    font-size: 1.1rem;
    color: #8884a0;
    line-height: 1.7;
    margin-bottom: 3rem;
    max-width: 520px;
    margin-left: auto; margin-right: auto;
  }

  .connect-card {
    background: rgba(255,255,255,0.03);
    border: 1px solid rgba(255,255,255,0.08);
    border-radius: 20px;
    padding: 2rem;
    width: 100%; max-width: 520px;
    margin: 0 auto;
    text-align: left;
  }

  .connect-card h2 {
    font-size: 1rem; font-weight: 600;
    color: #c8c0f0; margin-bottom: 1.25rem;
    letter-spacing: 0.02em;
  }

  .form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; }
  .form-full { grid-column: 1 / -1; }

  .form-group { display: flex; flex-direction: column; gap: 5px; }

  .form-group label {
    font-size: 11px; font-weight: 500;
    letter-spacing: 0.08em; text-transform: uppercase;
    color: #6b6880;
  }

  .form-group input {
    background: rgba(255,255,255,0.05);
    border: 1px solid rgba(255,255,255,0.1);
    border-radius: 10px;
    padding: 10px 13px;
    font-size: 14px; font-family: 'JetBrains Mono', monospace;
    color: #e8e6f0;
    outline: none;
    transition: border-color 0.2s;
  }

  .form-group input:focus { border-color: rgba(124,92,252,0.6); }
  .form-group input::placeholder { color: #3d3a50; }

  .btn-primary {
    width: 100%; margin-top: 1.25rem;
    padding: 13px;
    background: #7c5cfc;
    border: none; border-radius: 12px;
    font-family: 'Syne', sans-serif;
    font-size: 15px; font-weight: 600;
    color: #fff; cursor: pointer;
    transition: background 0.2s, transform 0.1s;
    display: flex; align-items: center; justify-content: center; gap: 8px;
  }

  .btn-primary:hover { background: #9070ff; }
  .btn-primary:active { transform: scale(0.98); }
  .btn-primary:disabled { background: #3d3560; color: #6b6880; cursor: not-allowed; }

  .error-msg {
    margin-top: 10px; padding: 10px 14px;
    background: rgba(226,75,74,0.1);
    border: 1px solid rgba(226,75,74,0.25);
    border-radius: 8px;
    font-size: 13px; color: #f09595;
  }

  .success-msg {
    margin-top: 10px; padding: 10px 14px;
    background: rgba(29,158,117,0.1);
    border: 1px solid rgba(29,158,117,0.25);
    border-radius: 8px;
    font-size: 13px; color: #5DCAA5;
  }

  /* ── Execute Page ── */
  .exec-page {
    min-height: 100vh;
    display: grid;
    grid-template-rows: auto 1fr;
    background: #0a0a0f;
  }

  .topbar {
    display: flex; align-items: center; justify-content: space-between;
    padding: 1rem 2rem;
    border-bottom: 1px solid rgba(255,255,255,0.06);
    background: rgba(255,255,255,0.02);
  }

  .topbar-brand {
    display: flex; align-items: center; gap: 10px;
    font-size: 15px; font-weight: 600; color: #e0dcff;
  }

  .topbar-brand .icon {
    width: 28px; height: 28px; border-radius: 8px;
    background: #7c5cfc;
    display: flex; align-items: center; justify-content: center;
    font-size: 14px;
  }

  .db-pill {
    display: flex; align-items: center; gap: 6px;
    padding: 5px 12px;
    background: rgba(29,158,117,0.1);
    border: 1px solid rgba(29,158,117,0.25);
    border-radius: 100px;
    font-size: 12px; color: #5DCAA5;
    font-family: 'JetBrains Mono', monospace;
  }

  .db-pill-dot { width: 6px; height: 6px; border-radius: 50%; background: #1D9E75; }

  .btn-disconnect {
    padding: 6px 14px;
    background: transparent;
    border: 1px solid rgba(255,255,255,0.1);
    border-radius: 8px;
    font-family: 'Syne', sans-serif;
    font-size: 13px; color: #6b6880;
    cursor: pointer; transition: all 0.2s;
  }
  .btn-disconnect:hover { border-color: rgba(226,75,74,0.4); color: #f09595; }

  .exec-body {
    display: grid;
    grid-template-columns: 380px 1fr;
    height: calc(100vh - 61px);
    overflow: hidden;
  }

  .left-panel {
    border-right: 1px solid rgba(255,255,255,0.06);
    padding: 1.5rem;
    display: flex; flex-direction: column; gap: 1.25rem;
    overflow-y: auto;
    background: rgba(255,255,255,0.01);
  }

  .panel-label {
    font-size: 11px; font-weight: 500;
    letter-spacing: 0.1em; text-transform: uppercase;
    color: #4a4760; margin-bottom: 6px;
  }

  .query-textarea {
    width: 100%;
    background: rgba(255,255,255,0.04);
    border: 1px solid rgba(255,255,255,0.09);
    border-radius: 12px;
    padding: 12px 14px;
    font-family: 'Syne', sans-serif;
    font-size: 15px; color: #e8e6f0;
    resize: none; outline: none;
    line-height: 1.6;
    transition: border-color 0.2s;
    min-height: 100px;
  }
  .query-textarea:focus { border-color: rgba(124,92,252,0.5); }
  .query-textarea::placeholder { color: #3d3a50; }

  .additional-input {
    width: 100%;
    background: rgba(255,255,255,0.04);
    border: 1px solid rgba(255,255,255,0.09);
    border-radius: 10px;
    padding: 10px 14px;
    font-family: 'Syne', sans-serif;
    font-size: 13px; color: #e8e6f0;
    outline: none; transition: border-color 0.2s;
  }
  .additional-input:focus { border-color: rgba(124,92,252,0.5); }
  .additional-input::placeholder { color: #3d3a50; }

  .btn-execute {
    width: 100%; padding: 13px;
    background: #7c5cfc;
    border: none; border-radius: 12px;
    font-family: 'Syne', sans-serif;
    font-size: 15px; font-weight: 600;
    color: #fff; cursor: pointer;
    transition: background 0.2s, transform 0.1s;
    display: flex; align-items: center; justify-content: center; gap: 8px;
  }
  .btn-execute:hover { background: #9070ff; }
  .btn-execute:active { transform: scale(0.98); }
  .btn-execute:disabled { background: #2d2a45; color: #4a4760; cursor: not-allowed; }

  .spinner {
    width: 16px; height: 16px;
    border: 2px solid rgba(255,255,255,0.2);
    border-top-color: #fff;
    border-radius: 50%;
    animation: spin 0.7s linear infinite;
  }
  @keyframes spin { to { transform: rotate(360deg); } }

  .right-panel {
    overflow-y: auto;
    padding: 1.5rem;
    display: flex; flex-direction: column; gap: 1.25rem;
  }

  .result-section { display: flex; flex-direction: column; gap: 8px; }

  .section-header {
    display: flex; align-items: center; gap: 8px;
    padding-bottom: 8px;
    border-bottom: 1px solid rgba(255,255,255,0.06);
  }

  .section-title {
    font-size: 12px; font-weight: 600;
    letter-spacing: 0.08em; text-transform: uppercase;
    color: #6b6880;
  }

  .section-badge {
    padding: 2px 8px;
    border-radius: 100px;
    font-size: 11px; font-weight: 500;
  }

  .badge-purple { background: rgba(124,92,252,0.15); color: #a89fff; }
  .badge-green  { background: rgba(29,158,117,0.15);  color: #5DCAA5; }
  .badge-amber  { background: rgba(186,117,23,0.15);  color: #FAC775; }

  .sql-block {
    background: rgba(255,255,255,0.03);
    border: 1px solid rgba(255,255,255,0.07);
    border-radius: 12px;
    padding: 14px 16px;
    font-family: 'JetBrains Mono', monospace;
    font-size: 13px; line-height: 1.7;
    color: #c8c0f0;
    white-space: pre-wrap; word-break: break-all;
  }

  .cost-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
  }

  .cost-card {
    background: rgba(255,255,255,0.03);
    border: 1px solid rgba(255,255,255,0.07);
    border-radius: 10px;
    padding: 12px 14px;
  }

  .cost-label {
    font-size: 11px; color: #4a4760;
    margin-bottom: 4px;
    letter-spacing: 0.05em; text-transform: uppercase;
  }
  .cost-value { font-family: 'JetBrains Mono', monospace; font-size: 18px; font-weight: 500; }
  .cost-value.purple { color: #9070ff; }
  .cost-value.green  { color: #5DCAA5; }
  .cost-value.amber  { color: #FAC775; }

  .improvement-row {
    display: flex; align-items: center; gap: 8px;
    padding: 8px 12px;
    background: rgba(29,158,117,0.06);
    border: 1px solid rgba(29,158,117,0.15);
    border-radius: 8px;
    font-size: 13px; color: #5DCAA5;
    font-family: 'JetBrains Mono', monospace;
  }

  .result-table-wrapper {
    background: rgba(255,255,255,0.02);
    border: 1px solid rgba(255,255,255,0.07);
    border-radius: 12px;
    overflow: auto;
    max-height: 400px;
  }

  .result-pre {
    font-family: 'JetBrains Mono', monospace;
    font-size: 12px; line-height: 1.6;
    color: #9d98b8;
    padding: 14px 16px;
    white-space: pre;
    margin: 0;
  }

  .explanation-box {
    background: rgba(255,255,255,0.03);
    border: 1px solid rgba(255,255,255,0.07);
    border-left: 3px solid #7c5cfc;
    border-radius: 0 12px 12px 0;
    padding: 14px 16px;
    font-size: 14px; line-height: 1.7;
    color: #a89fff;
    white-space: pre-wrap;
  }

  .empty-state {
    display: flex; flex-direction: column;
    align-items: center; justify-content: center;
    height: 100%; gap: 12px;
    color: #3d3a50;
  }

  .empty-icon {
    width: 56px; height: 56px;
    border: 2px solid rgba(255,255,255,0.05);
    border-radius: 16px;
    display: flex; align-items: center; justify-content: center;
    font-size: 24px;
  }

  .empty-state p { font-size: 14px; }

  .exec-error {
    padding: 14px 16px;
    background: rgba(226,75,74,0.08);
    border: 1px solid rgba(226,75,74,0.2);
    border-radius: 12px;
    font-size: 14px; color: #f09595; line-height: 1.6;
  }

  .divider { height: 1px; background: rgba(255,255,255,0.05); }
`;

// ── Cost Display Component ──────────────────────────────────────
function CostDisplay({ label, cost, colorClass }) {
  if (!cost) return null;

  const fmt = (val) => {
    if (cost.infinite) return "∞";
    if (val == null) return "—";
    return Number(val).toFixed(0);
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
      <p className="panel-label">{label}</p>
      <div className="cost-grid">
        <div className="cost-card">
          <div className="cost-label">Rows</div>
          <div className={`cost-value ${colorClass}`}>{fmt(cost.rows)}</div>
        </div>
        <div className="cost-card">
          <div className="cost-label">CPU</div>
          <div className={`cost-value ${colorClass}`}>{fmt(cost.cpu)}</div>
        </div>
        <div className="cost-card">
          <div className="cost-label">I/O</div>
          <div className={`cost-value ${colorClass}`}>{fmt(cost.io)}</div>
        </div>
      </div>
    </div>
  );
}

// ── Landing Page ───────────────────────────────────────────────
function LandingPage({ onConnected }) {
  const [form, setForm] = useState({
    host: "localhost", port: "5432",
    database: "", username: "", password: ""
  });
  const [loading, setLoading] = useState(false);
  const [error, setError]     = useState(null);
  const [success, setSuccess] = useState(null);

  const handleChange = (e) => setForm({ ...form, [e.target.name]: e.target.value });

  const handleConnect = async () => {
    setLoading(true); setError(null); setSuccess(null);
    try {
      const res  = await fetch(`${API_BASE}/db/connect`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });
      const data = await res.json();
      if (res.ok && data.response?.status === "SUCCESS") {
        setSuccess(`Connected to ${form.database} on ${form.host}`);
        setTimeout(() => onConnected(form), 800);
      } else {
        setError(data.response?.message || "Connection failed");
      }
    } catch (e) {
      setError("Could not reach the server. Is it running on port 8080?");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="landing">
      <div className="grid-bg" />
      <div className="glow-orb" />
      <div className="landing-content">
        <div className="badge">
          <div className="badge-dot" />
          Powered by Ollama · Apache Calcite · VolcanoPlanner
        </div>
        <h1>Natural language<br />to <span>optimized SQL</span></h1>
        <p>
          Connect your PostgreSQL database. Ask questions in plain English.
          Get validated, cost-optimized queries — automatically.
        </p>
        <div className="connect-card">
          <h2>Connect to database</h2>
          <div className="form-grid">
            <div className="form-group form-full">
              <label>Host</label>
              <input name="host" value={form.host} onChange={handleChange} placeholder="localhost" />
            </div>
            <div className="form-group">
              <label>Port</label>
              <input name="port" value={form.port} onChange={handleChange} placeholder="5432" />
            </div>
            <div className="form-group">
              <label>Database</label>
              <input name="database" value={form.database} onChange={handleChange} placeholder="mydb" />
            </div>
            <div className="form-group">
              <label>Username</label>
              <input name="username" value={form.username} onChange={handleChange} placeholder="postgres" />
            </div>
            <div className="form-group">
              <label>Password</label>
              <input name="password" type="password" value={form.password} onChange={handleChange} placeholder="••••••••" />
            </div>
          </div>
          {error   && <div className="error-msg">{error}</div>}
          {success && <div className="success-msg">{success}</div>}
          <button
            className="btn-primary"
            onClick={handleConnect}
            disabled={loading || !form.database || !form.username}
          >
            {loading ? <><div className="spinner" /> Connecting...</> : "Connect →"}
          </button>
        </div>
      </div>
    </div>
  );
}

// ── Execute Page ───────────────────────────────────────────────
function ExecutePage({ dbInfo, onDisconnect }) {
  const [userMessage,      setUserMessage]      = useState("");
  const [additionalInputs, setAdditionalInputs] = useState("");
  const [loading,          setLoading]           = useState(false);
  const [result,           setResult]            = useState(null);
  const [error,            setError]             = useState(null);

  const handleExecute = async () => {
    if (!userMessage.trim()) return;
    setLoading(true); setError(null); setResult(null);
    try {
      const res  = await fetch(`${API_BASE}/execute`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ userMessage, additionalInputs }),
      });
      const data = await res.json();
      if (res.ok) {
        setResult(data.response || data); // ✅ unwrap nested response
      } else {
        setError(data.response?.message || JSON.stringify(data));
      }
    } catch (e) {
      setError("Request failed. Check that the server is running.");
    } finally {
      setLoading(false);
    }
  };

  const handleDisconnect = async () => {
    await fetch(`${API_BASE}/db/close`).catch(() => {});
    onDisconnect();
  };

  // ✅ Only show savings when both costs are finite
  const bothFinite = result?.originalCost?.infinite === false && result?.optimizedCost?.infinite === false;
  const rowsSaved  = bothFinite ? (result.originalCost.rows - result.optimizedCost.rows).toFixed(0) : null;
  const cpuSaved   = bothFinite ? (result.originalCost.cpu  - result.optimizedCost.cpu).toFixed(0)  : null;

  return (
    <div className="exec-page">
      {/* Top Bar */}
      <div className="topbar">
        <div className="topbar-brand">
          <div className="icon">⚡</div>
          SQL Optimizer
        </div>
        <div className="db-pill">
          <div className="db-pill-dot" />
          {dbInfo.database}@{dbInfo.host}:{dbInfo.port}
        </div>
        <button className="btn-disconnect" onClick={handleDisconnect}>Disconnect</button>
      </div>

      <div className="exec-body">
        {/* ── Left Panel ── */}
        <div className="left-panel">
          <div>
            <p className="panel-label">Your question</p>
            <textarea
              className="query-textarea"
              rows={4}
              placeholder="e.g. Show me all students who scored above 80 in math"
              value={userMessage}
              onChange={(e) => setUserMessage(e.target.value)}
            />
          </div>

          <div>
            <p className="panel-label">Additional context (optional)</p>
            <input
              className="additional-input"
              placeholder="e.g. Sort by score descending, limit 10"
              value={additionalInputs}
              onChange={(e) => setAdditionalInputs(e.target.value)}
            />
          </div>

          <button
            className="btn-execute"
            onClick={handleExecute}
            disabled={loading || !userMessage.trim()}
          >
            {loading
              ? <><div className="spinner" /> Running...</>
              : "Execute ↗"
            }
          </button>

          {/* Cost panels — only shown after a result */}
          {result && (
            <>
              <div className="divider" />
              <CostDisplay label="Original cost"  cost={result.originalCost}  colorClass="purple" />
              <CostDisplay label="Optimized cost" cost={result.optimizedCost} colorClass="green"  />
              {rowsSaved && cpuSaved && (
                <div>
                  <p className="panel-label">Savings</p>
                  <div className="improvement-row">
                    ↓ {rowsSaved} rows · {cpuSaved} cpu
                  </div>
                </div>
              )}
            </>
          )}
        </div>

        {/* ── Right Panel ── */}
        <div className="right-panel">
          {/* Empty state */}
          {!result && !error && !loading && (
            <div className="empty-state">
              <div className="empty-icon">✦</div>
              <p>Ask a question to get started</p>
            </div>
          )}

          {/* Loading state */}
          {loading && (
            <div className="empty-state">
              <div className="spinner" style={{ width: 32, height: 32, borderWidth: 3 }} />
              <p>Generating · Validating · Optimizing...</p>
            </div>
          )}

          {/* Error state */}
          {error && (
            <div className="exec-error">
              <strong>Error: </strong>{error}
            </div>
          )}

          {/* Results */}
          {result && (
            <>
              {result.aiGeneratedQuery && (
                <div className="result-section">
                  <div className="section-header">
                    <span className="section-title">AI Generated Query</span>
                    <span className="section-badge badge-purple">Validated</span>
                  </div>
                  <div className="sql-block">{result.aiGeneratedQuery}</div>
                </div>
              )}

              {result.optimizedSqlQuery && (
                <div className="result-section">
                  <div className="section-header">
                    <span className="section-title">Optimized Query</span>
                    <span className="section-badge badge-green">VolcanoPlanner</span>
                  </div>
                  <div className="sql-block">{result.optimizedSqlQuery}</div>
                </div>
              )}

              {result.whatIsOptimized && (
                <div className="result-section">
                  <div className="section-header">
                    <span className="section-title">What Changed</span>
                    <span className="section-badge badge-amber">LLM Analysis</span>
                  </div>
                  <div className="explanation-box">{result.whatIsOptimized}</div>
                </div>
              )}

              {result.resultSet && (
                <div className="result-section">
                  <div className="section-header">
                    <span className="section-title">Result</span>
                  </div>
                  <div className="result-table-wrapper">
                    <pre className="result-pre">{result.resultSet}</pre>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </div>
    </div>
  );
}

// ── Root ───────────────────────────────────────────────────────
export default function App() {
  const [page,   setPage]   = useState("landing");
  const [dbInfo, setDbInfo] = useState(null);

  return (
    <>
      <style>{styles}</style>
      {page === "landing" ? (
        <LandingPage
          onConnected={(info) => { setDbInfo(info); setPage("execute"); }}
        />
      ) : (
        <ExecutePage
          dbInfo={dbInfo}
          onDisconnect={() => { setDbInfo(null); setPage("landing"); }}
        />
      )}
    </>
  );
}