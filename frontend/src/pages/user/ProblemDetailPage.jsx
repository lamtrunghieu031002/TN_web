import { useEffect, useMemo, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { getProblemById, submitSolution } from '../../services/problemService';
import { useAuth } from '../../hooks/useAuth';
import { createApiClient } from '../../services/apiClient';
import { API_BASE } from '../../config/api';

export function ProblemDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { auth } = useAuth();
  const client = useMemo(() => createApiClient(API_BASE, auth?.token), [auth?.token]);

  const [problem, setProblem] = useState(null);
  const [userQuery, setUserQuery] = useState('');
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState(null);

  useEffect(() => {
    const fetchDetail = async () => {
      setLoading(true);
      try {
        const res = await getProblemById(client, id);
        // Fix: xử lý cả trường hợp res.data và res trực tiếp
        const problemData = res?.data || res;
        setProblem(problemData);
      } catch (err) {
        console.error("Lỗi tải chi tiết:", err);
      } finally {
        setLoading(false);
      }
    };
    if (id) fetchDetail();
  }, [client, id]);

  const handleSubmit = async () => {
    if (!userQuery.trim()) return;
    setSubmitting(true);
    setResult(null);
    try {
      const res = await submitSolution(client, Number(id), userQuery);
      // Fix: xử lý cả trường hợp res.data và res trực tiếp
      const resultData = res?.data || res;
      setResult(resultData);
    } catch (err) {
      setResult({
        status: 'ERROR',
        message: err.response?.data?.message || "Lỗi thực thi câu lệnh SQL."
      });
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return (
    <div className="p-10 text-center text-slate-500">Đang tải...</div>
  );

  if (!problem) return (
    <div className="p-10 text-center text-red-500 font-bold">Không tìm thấy bài tập!</div>
  );

  const isAccepted = result?.status === 'ACCEPTED';

  return (
    <div className="mx-auto max-w-7xl px-4 py-6">
      <button
        onClick={() => navigate('/problems')}
        className="mb-4 text-sm font-medium text-slate-500 hover:text-red-600"
      >
        ← Quay lại danh sách
      </button>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* CỘT TRÁI: ĐỀ BÀI */}
        <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm min-h-[500px]">
          <span className="text-xs font-bold uppercase text-red-600">{problem.topic}</span>
          <h1 className="text-2xl font-bold text-slate-800 mt-1 mb-4">{problem.title}</h1>

          <div className="mb-2 inline-block px-2 py-0.5 rounded-full text-xs font-semibold
            bg-green-100 text-green-700">
            {problem.difficulty}
          </div>

          <h3 className="text-sm font-bold text-slate-700 uppercase mt-4 mb-2">Mô tả:</h3>
          <p className="whitespace-pre-line text-slate-600">{problem.description}</p>
        </div>

        {/* CỘT PHẢI: EDITOR & KẾT QUẢ */}
        <div className="flex flex-col gap-6">
          {/* Editor */}
          <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
            <h3 className="mb-3 text-sm font-bold text-slate-700 uppercase">SQL Query Editor</h3>
            <textarea
              value={userQuery}
              onChange={(e) => setUserQuery(e.target.value)}
              placeholder="SELECT * FROM table_name..."
              className="h-56 w-full rounded-xl border border-slate-200 bg-slate-900 p-4 font-mono text-sm text-green-400 outline-none focus:ring-2 focus:ring-red-500"
            />
            <div className="mt-4 flex justify-end">
              <button
                onClick={handleSubmit}
                disabled={submitting}
                className="rounded-lg bg-red-600 px-8 py-2.5 text-sm font-bold text-white hover:bg-red-700 disabled:opacity-50"
              >
                {submitting ? 'Đang chấm bài...' : 'Nộp bài'}
              </button>
            </div>
          </div>

          {/* Kết quả */}
          {result && (
            <div className={`rounded-2xl border p-6 shadow-sm ${
              isAccepted
                ? 'border-green-300 bg-green-50'
                : 'border-red-300 bg-red-50'
            }`}>
              <div className="flex items-center gap-2 font-bold text-lg mb-2">
                <span>{isAccepted ? '✅' : '❌'}</span>
                <span className={isAccepted ? 'text-green-700' : 'text-red-700'}>
                  {isAccepted ? 'ACCEPTED!' : result.status}
                </span>
              </div>
              <p className="text-sm text-slate-600 mb-4">{result.message}</p>

              {/* Test case results */}
              {result.testCaseResults?.map((tc, i) => (
                <div key={i} className={`mb-2 rounded-lg px-4 py-2 text-sm ${
                  tc.passed ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                }`}>
                  <span className="font-semibold">Test case {tc.testCaseIndex}: </span>
                  {tc.passed ? '✅ Passed' : `❌ Failed${tc.errorMessage ? ` — ${tc.errorMessage}` : ''}`}
                </div>
              ))}

              {/* Solution query khi ACCEPTED */}
              {isAccepted && result.solutionQuery && (
                <div className="mt-4 rounded-lg bg-slate-800 p-4">
                  <p className="text-xs text-slate-400 mb-2 font-semibold uppercase">Solution mẫu:</p>
                  <code className="text-green-400 text-xs font-mono">{result.solutionQuery}</code>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}