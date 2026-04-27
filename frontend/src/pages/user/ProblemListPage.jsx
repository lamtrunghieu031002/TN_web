import { useEffect, useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import * as problemService from "../../services/problemService";
import { useAuth } from "../../hooks/useAuth";
import { createApiClient } from "../../services/apiClient";
import { API_BASE } from "../../config/api";

const DIFFICULTIES = ['ALL', 'EASY', 'MEDIUM', 'HARD'];

const difficultyStyle = {
  EASY:   'text-green-600 bg-green-100',
  MEDIUM: 'text-yellow-600 bg-yellow-100',
  HARD:   'text-red-600 bg-red-100',
};

export function ProblemListPage() {
  const { auth } = useAuth();
  const navigate = useNavigate();

  // 1. Tạo API Client có Token (Cực kỳ quan trọng để không bị lỗi 401/client undefined)
  const client = useMemo(() => createApiClient(API_BASE, auth?.token), [auth?.token]);

  // 2. Các state quản lý dữ liệu
  const [problems, setProblems] = useState([]);
  const [topics, setTopics] = useState(['ALL']);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState({ type: '', text: '' });
  
  const [filters, setFilters] = useState({
    difficulty: 'ALL',
    topic: 'ALL'
  });

  // 3. Lấy danh sách Topic khi mount
  useEffect(() => {
    const fetchTopics = async () => {
      try {
        const res = await problemService.getTopics(client);
        // res trả về mảng trực tiếp từ client.request()
        setTopics(['ALL', ...(Array.isArray(res) ? res : [])]);
      } catch (err) {
        console.error("Lỗi tải topics:", err);
        setTopics(['ALL']);
      }
    };
    fetchTopics();
  }, [client]);

  const [myStatus, setMyStatus] = useState({});

  // 4. Lấy danh sách bài tập khi filter thay đổi
  useEffect(() => {
    const fetchProblems = async () => {
      setLoading(true);
      setMessage({ type: '', text: '' });
      try {
        const res = await problemService.getProblems(client, filters.difficulty, filters.topic);
        const actualData = Array.isArray(res) ? res : [];
        setProblems(actualData);

        // Lấy thêm trạng thái của bản thân nếu đã đăng nhập
        if (auth) {
          const statusRes = await client.request('/api/submissions/my-status', { useAuth: true });
          setMyStatus(statusRes || {});
        }

        if (actualData.length === 0) {
          setMessage({ type: 'info', text: 'Không tìm thấy bài tập nào phù hợp.' });
        }
      } catch (err) {
        console.error("Lỗi tải bài tập:", err);
        setProblems([]);
        setMessage({ type: 'error', text: 'Lỗi kết nối máy chủ hoặc hệ thống đang khởi động.' });
      } finally {
        setLoading(false);
      }
    };
    fetchProblems();
  }, [client, filters, auth]);

  return (
    <div className="mx-auto max-w-6xl px-4 py-8">
      {/* Header Section */}
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-slate-800">SQL Practice</h1>
        <p className="text-slate-500 mt-2">Luyện tập SQL qua các bài tập thực tế từ cơ bản đến nâng cao</p>
      </div>

      {/* Filter Card */}
      <div className="mb-6 rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
        <div className="flex flex-wrap items-center justify-between gap-4">
          <div className="flex flex-wrap gap-2">
            {DIFFICULTIES.map((d) => (
              <button
                key={d}
                onClick={() => setFilters(prev => ({ ...prev, difficulty: d }))}
                className={`px-5 py-2 rounded-full text-sm font-semibold transition-all border ${
                  filters.difficulty === d
                    ? 'bg-brand-red text-white border-brand-red shadow-md'
                    : 'bg-white text-slate-600 border-slate-200 hover:border-brand-red'
                }`}
              >
                {d}
              </button>
            ))}
          </div>

          <select
            value={filters.topic}
            onChange={(e) => setFilters(prev => ({ ...prev, topic: e.target.value }))}
            className="rounded-full border border-slate-200 bg-slate-50 px-4 py-2 text-sm font-medium text-slate-600 outline-none focus:border-brand-red focus:ring-1 focus:ring-brand-red"
          >
            {topics.map((t) => (
              <option key={t} value={t}>{t}</option>
            ))}
          </select>
        </div>
      </div>

      {/* Main Content Table */}
      <div className="rounded-2xl border border-slate-200 bg-white shadow-sm overflow-hidden">
        <div className="grid grid-cols-12 bg-slate-50 px-6 py-4 text-xs font-bold uppercase tracking-wider text-slate-500 border-b border-slate-200">
          <span className="col-span-1 text-center">#</span>
          <span className="col-span-1 text-center">Status</span>
          <span className="col-span-5">Tên bài tập</span>
          <span className="col-span-3">Chủ đề</span>
          <span className="col-span-2 text-center">Độ khó</span>
        </div>

        {loading ? (
          <div className="flex flex-col items-center py-20">
            <div className="h-8 w-8 animate-spin rounded-full border-4 border-slate-200 border-t-brand-red"></div>
            <p className="mt-4 text-slate-400 font-medium">Đang tải dữ liệu...</p>
          </div>
        ) : problems.length > 0 ? (
          <div className="divide-y divide-slate-100">
            {problems.map((p, i) => (
              <div
                key={p.id}
                onClick={() => navigate(`/problems/${p.id}`)}
                className="grid grid-cols-12 items-center px-6 py-4 hover:bg-slate-50 cursor-pointer transition-colors group"
              >
                <span className="col-span-1 text-center text-slate-400 font-medium">{i + 1}</span>
                <span className="col-span-1 text-center">
                  {myStatus[p.id] === 'ACCEPTED' ? (
                    <span className="text-green-500 text-lg">✅</span>
                  ) : myStatus[p.id] ? (
                    <span className="text-red-400 text-xs font-bold uppercase">WA</span>
                  ) : (
                    <span className="text-slate-200 text-lg">○</span>
                  )}
                </span>
                <span className="col-span-5 font-bold text-slate-700 group-hover:text-brand-red transition-colors">
                  {p.title}
                </span>
                <span className="col-span-3 text-slate-500 text-sm italic">{p.topic}</span>
                <div className="col-span-2 flex justify-center">
                  <span className={`rounded-full px-3 py-1 text-[10px] font-bold uppercase tracking-tighter ${difficultyStyle[p.difficulty] || 'bg-slate-100'}`}>
                    {p.difficulty}
                  </span>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="py-20 text-center">
             <p className="text-slate-400">{message.text || "Không có dữ liệu hiển thị."}</p>
          </div>
        )}
      </div>

      {/* Footer info */}
      <div className="mt-4 flex justify-between items-center text-slate-400 text-sm px-2">
        <span>Hiển thị {problems.length} kết quả</span>
        <span className="italic text-xs">Cập nhật: {new Date().toLocaleDateString('vi-VN')}</span>
      </div>
    </div>
  );
}