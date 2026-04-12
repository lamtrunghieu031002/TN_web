const stats = [
  { icon: '📝', value: '120+', label: 'Đề thi thử' },
  { icon: '⏱️', value: '45', label: 'Phút / đề' },
  { icon: '🎯', value: '3 cấp', label: 'Mức độ' },
]

export function HomePage() {
  return (
    <section className="bg-gradient-to-b from-red-50 via-red-50/70 to-white">
      <div className="mx-auto max-w-6xl px-4 pb-16 pt-16 text-center">
        <h1 className="text-5xl font-extrabold text-brand-red md:text-6xl">PTIT Exam</h1>
        <p className="mx-auto mt-6 max-w-2xl text-lg text-slate-600">
          Luyện đề và thi thử trắc nghiệm có hẹn giờ. Đăng nhập để bắt đầu làm bài và
          lưu kết quả.
        </p>

        <div className="mx-auto mt-10 grid max-w-2xl grid-cols-1 gap-4 sm:grid-cols-3">
          {stats.map((item) => (
            <div
              key={item.label}
              className="rounded-xl bg-white p-4 shadow-sm ring-1 ring-slate-200"
            >
              <div className="text-2xl">{item.icon}</div>
              <div className="mt-2 text-3xl font-bold text-brand-red">{item.value}</div>
              <div className="text-sm text-slate-600">{item.label}</div>
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}

