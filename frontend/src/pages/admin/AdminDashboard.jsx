import { useEffect, useState } from 'react'
import {
  getAllProblems, createProblem, updateProblem, deleteProblem,
  getAllUsers, updateUserRole
} from '../../services/adminService'

const TABS = ['Quản lý bài tập', 'Quản lý user']
const DIFFICULTIES = ['EASY', 'MEDIUM', 'HARD']
const ROLES = ['STUDENT', 'TEACHER', 'ADMIN']

const emptyForm = {
  title: '', description: '', difficulty: 'EASY',
  topic: '', schemaSetupSql: '', solutionQuery: '',
  testCases: [{ extraSetupSql: '', expectedResultJson: '', hidden: false }]
}

export function AdminDashboard() {
  const [tab, setTab] = useState(0)
  const [problems, setProblems] = useState([])
  const [users, setUsers] = useState([])
  const [loading, setLoading] = useState(true)
  const [showForm, setShowForm] = useState(false)
  const [editingId, setEditingId] = useState(null)
  const [form, setForm] = useState(emptyForm)
  const [msg, setMsg] = useState('')

  useEffect(() => {
    if (tab === 0) fetchProblems()
    else fetchUsers()
  }, [tab])

  const fetchProblems = () => {
    setLoading(true)
    getAllProblems()
      .then(res => setProblems(res.data))
      .finally(() => setLoading(false))
  }

  const fetchUsers = () => {
    setLoading(true)
    getAllUsers()
      .then(res => setUsers(res.data))
      .finally(() => setLoading(false))
  }

  const handleSubmit = async () => {
    try {
      if (editingId) {
        await updateProblem(editingId, form)
        setMsg('✅ Cập nhật bài thành công!')
      } else {
        await createProblem(form)
        setMsg('✅ Tạo bài thành công!')
      }
      setShowForm(false)
      setForm(emptyForm)
      setEditingId(null)
      fetchProblems()
    } catch {
      setMsg('❌ Có lỗi xảy ra!')
    }
  }

  const handleEdit = (p) => {
    setForm({
      title: p.title,
      description: p.description,
      difficulty: p.difficulty,
      topic: p.topic,
      schemaSetupSql: p.schemaSetupSql,
      solutionQuery: p.solutionQuery,
      testCases: p.testCases?.length > 0 ? p.testCases : emptyForm.testCases
    })
    setEditingId(p.id)
    setShowForm(true)
  }

  const handleDelete = async (id) => {
    if (!confirm('Bạn có chắc muốn xóa bài này?')) return
    await deleteProblem(id)
    fetchProblems()
  }

  const handleRoleChange = async (userId, role) => {
    await updateUserRole(userId, [role])
    fetchUsers()
  }

  const updateTestCase = (i, field, value) => {
    const tcs = [...form.testCases]
    tcs[i] = { ...tcs[i], [field]: value }
    setForm({ ...form, testCases: tcs })
  }

  return (
    <main className="mx-auto max-w-6xl px-4 py-8">
      <h1 className="text-2xl font-bold text-slate-800 mb-6">⚙️ Admin Dashboard</h1>

      {/* Tabs */}
      <div className="flex gap-2 mb-6 border-b border-slate-200">
        {TABS.map((t, i) => (
          <button
            key={i}
            onClick={() => setTab(i)}
            className={`px-5 py-2 text-sm font-semibold border-b-2 transition ${
              tab === i
                ? 'border-red-600 text-red-600'
                : 'border-transparent text-slate-500 hover:text-slate-700'
            }`}
          >
            {t}
          </button>
        ))}
      </div>

      {/* ── TAB 0: PROBLEM ── */}
      {tab === 0 && (
        <div>
          <div className="flex justify-between items-center mb-4">
            <span className="text-slate-500 text-sm">{problems.length} bài tập</span>
            <button
              onClick={() => { setShowForm(true); setEditingId(null); setForm(emptyForm) }}
              className="bg-red-600 hover:bg-red-700 text-white px-4 py-2 rounded-lg text-sm font-semibold"
            >
              + Tạo bài mới
            </button>
          </div>

          {msg && (
            <div className="mb-4 px-4 py-2 bg-green-50 border border-green-200 rounded-lg text-green-700 text-sm">
              {msg}
            </div>
          )}

          {/* Form tạo/sửa bài */}
          {showForm && (
            <div className="mb-6 bg-white border border-slate-200 rounded-2xl p-6 shadow-sm">
              <h3 className="font-bold text-slate-700 mb-4">
                {editingId ? 'Chỉnh sửa bài' : 'Tạo bài mới'}
              </h3>
              <div className="grid grid-cols-2 gap-4">
                <div className="col-span-2">
                  <label className="text-xs font-semibold text-slate-500 uppercase mb-1 block">Tiêu đề</label>
                  <input
                    className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm"
                    value={form.title}
                    onChange={e => setForm({ ...form, title: e.target.value })}
                    placeholder="Tên bài tập"
                  />
                </div>
                <div className="col-span-2">
                  <label className="text-xs font-semibold text-slate-500 uppercase mb-1 block">Mô tả</label>
                  <textarea
                    className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm h-24"
                    value={form.description}
                    onChange={e => setForm({ ...form, description: e.target.value })}
                    placeholder="Mô tả đề bài"
                  />
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-500 uppercase mb-1 block">Độ khó</label>
                  <select
                    className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm"
                    value={form.difficulty}
                    onChange={e => setForm({ ...form, difficulty: e.target.value })}
                  >
                    {DIFFICULTIES.map(d => <option key={d}>{d}</option>)}
                  </select>
                </div>
                <div>
                  <label className="text-xs font-semibold text-slate-500 uppercase mb-1 block">Topic</label>
                  <input
                    className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm"
                    value={form.topic}
                    onChange={e => setForm({ ...form, topic: e.target.value })}
                    placeholder="GROUP BY, JOIN, SUBQUERY..."
                  />
                </div>
                <div className="col-span-2">
                  <label className="text-xs font-semibold text-slate-500 uppercase mb-1 block">Schema Setup SQL</label>
                  <textarea
                    className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm font-mono h-24 bg-slate-900 text-green-400"
                    value={form.schemaSetupSql}
                    onChange={e => setForm({ ...form, schemaSetupSql: e.target.value })}
                    placeholder="CREATE TABLE... INSERT INTO..."
                  />
                </div>
                <div className="col-span-2">
                  <label className="text-xs font-semibold text-slate-500 uppercase mb-1 block">Solution Query</label>
                  <textarea
                    className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm font-mono h-20 bg-slate-900 text-green-400"
                    value={form.solutionQuery}
                    onChange={e => setForm({ ...form, solutionQuery: e.target.value })}
                    placeholder="SELECT..."
                  />
                </div>

                {/* Test cases */}
                <div className="col-span-2">
                  <label className="text-xs font-semibold text-slate-500 uppercase mb-2 block">Test Cases</label>
                  {form.testCases.map((tc, i) => (
                    <div key={i} className="border border-slate-200 rounded-lg p-4 mb-3">
                      <p className="text-xs font-semibold text-slate-500 mb-2">Test case {i + 1}</p>
                      <textarea
                        className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm font-mono h-16 mb-2"
                        placeholder='Expected Result JSON: [{"col":"val"}]'
                        value={tc.expectedResultJson}
                        onChange={e => updateTestCase(i, 'expectedResultJson', e.target.value)}
                      />
                      <textarea
                        className="w-full border border-slate-200 rounded-lg px-3 py-2 text-sm font-mono h-16 mb-2"
                        placeholder="Extra Setup SQL (optional)"
                        value={tc.extraSetupSql}
                        onChange={e => updateTestCase(i, 'extraSetupSql', e.target.value)}
                      />
                      <label className="flex items-center gap-2 text-sm text-slate-600">
                        <input
                          type="checkbox"
                          checked={tc.hidden}
                          onChange={e => updateTestCase(i, 'hidden', e.target.checked)}
                        />
                        Hidden test case
                      </label>
                    </div>
                  ))}
                  <button
                    onClick={() => setForm({
                      ...form,
                      testCases: [...form.testCases, { extraSetupSql: '', expectedResultJson: '', hidden: false }]
                    })}
                    className="text-sm text-red-600 hover:underline"
                  >
                    + Thêm test case
                  </button>
                </div>
              </div>

              <div className="flex gap-3 mt-6">
                <button
                  onClick={handleSubmit}
                  className="bg-red-600 hover:bg-red-700 text-white px-6 py-2 rounded-lg text-sm font-semibold"
                >
                  {editingId ? 'Cập nhật' : 'Tạo bài'}
                </button>
                <button
                  onClick={() => { setShowForm(false); setMsg('') }}
                  className="bg-slate-100 hover:bg-slate-200 text-slate-700 px-6 py-2 rounded-lg text-sm font-semibold"
                >
                  Hủy
                </button>
              </div>
            </div>
          )}

          {/* Problem list */}
          {loading ? (
            <div className="text-center text-slate-400 py-10">Đang tải...</div>
          ) : (
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
              <div className="grid grid-cols-12 text-xs font-bold uppercase text-slate-400 px-6 py-3 bg-slate-50 border-b">
                <span className="col-span-1">ID</span>
                <span className="col-span-4">Tên bài</span>
                <span className="col-span-2">Topic</span>
                <span className="col-span-2">Độ khó</span>
                <span className="col-span-3">Thao tác</span>
              </div>
              {problems.length === 0 ? (
                <div className="text-center text-slate-400 py-10">Chưa có bài tập nào</div>
              ) : problems.map(p => (
                <div key={p.id} className="grid grid-cols-12 items-center px-6 py-3 border-b border-slate-100 hover:bg-slate-50">
                  <span className="col-span-1 text-slate-400 text-sm">{p.id}</span>
                  <span className="col-span-4 font-medium text-slate-700 text-sm">{p.title}</span>
                  <span className="col-span-2 text-slate-500 text-sm">{p.topic}</span>
                  <span className={`col-span-2 text-xs font-semibold ${
                    p.difficulty === 'EASY' ? 'text-green-600' :
                    p.difficulty === 'MEDIUM' ? 'text-yellow-600' : 'text-red-600'
                  }`}>{p.difficulty}</span>
                  <div className="col-span-3 flex gap-2">
                    <button
                      onClick={() => handleEdit(p)}
                      className="text-xs bg-blue-50 text-blue-600 px-3 py-1 rounded-lg hover:bg-blue-100"
                    >
                      Sửa
                    </button>
                    <button
                      onClick={() => handleDelete(p.id)}
                      className="text-xs bg-red-50 text-red-600 px-3 py-1 rounded-lg hover:bg-red-100"
                    >
                      Xóa
                    </button>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* ── TAB 1: USER ── */}
      {tab === 1 && (
        <div>
          {loading ? (
            <div className="text-center text-slate-400 py-10">Đang tải...</div>
          ) : (
            <div className="bg-white rounded-2xl border border-slate-200 shadow-sm overflow-hidden">
              <div className="grid grid-cols-12 text-xs font-bold uppercase text-slate-400 px-6 py-3 bg-slate-50 border-b">
                <span className="col-span-1">ID</span>
                <span className="col-span-3">Username</span>
                <span className="col-span-4">Email</span>
                <span className="col-span-4">Role</span>
              </div>
              {users.length === 0 ? (
                <div className="text-center text-slate-400 py-10">Chưa có user nào</div>
              ) : users.map(u => (
                <div key={u.id} className="grid grid-cols-12 items-center px-6 py-3 border-b border-slate-100 hover:bg-slate-50">
                  <span className="col-span-1 text-slate-400 text-sm">{u.id}</span>
                  <span className="col-span-3 font-medium text-slate-700 text-sm">{u.username}</span>
                  <span className="col-span-4 text-slate-500 text-sm">{u.email}</span>
                  <div className="col-span-4 flex gap-1 flex-wrap">
                    {ROLES.map(r => (
                      <button
                        key={r}
                        onClick={() => handleRoleChange(u.id, r)}
                        className={`text-xs px-2 py-1 rounded-full font-semibold transition ${
                          u.roles?.some(role => role.name === `ROLE_${r}`)
                            ? 'bg-red-600 text-white'
                            : 'bg-slate-100 text-slate-500 hover:bg-slate-200'
                        }`}
                      >
                        {r}
                      </button>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      )}
    </main>
  )
}