import { Link } from 'react-router-dom'

const examSets = [
  { id: 'db-basic', name: 'Cơ sở dữ liệu', level: 'Cơ bản', questions: 40, duration: 45 },
  { id: 'java-mid', name: 'Lập trình Java', level: 'Trung bình', questions: 50, duration: 60 },
  { id: 'net-adv', name: 'Mạng máy tính', level: 'Nâng cao', questions: 40, duration: 45 },
]

export function ExamPage() {
  return (
    <section className="mx-auto max-w-6xl px-4 py-14">
      <h1 className="text-center text-4xl font-extrabold text-slate-800">Đề thi</h1>
      <p className="mt-3 text-center text-slate-500">
        Chọn đề thi theo môn học, thời lượng và độ khó.
      </p>

      <div className="mt-10 grid gap-6 md:grid-cols-3">
        {examSets.map((exam) => (
          <article
            key={exam.id}
            className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm transition hover:-translate-y-1 hover:shadow-md"
          >
            <h2 className="text-xl font-bold text-brand-red">{exam.name}</h2>
            <p className="mt-2 text-slate-600">Mức độ: {exam.level}</p>
            <p className="mt-1 text-slate-600">Số câu: {exam.questions}</p>
            <p className="mt-1 text-slate-600">Thời gian: {exam.duration} phút</p>
            <Link
              to={`/exams/${exam.id}`}
              className="mt-4 inline-flex rounded-md bg-brand-red px-4 py-2 text-sm font-semibold text-white hover:bg-brand-redDark"
            >
              Bắt đầu thi
            </Link>
          </article>
        ))}
      </div>
    </section>
  )
}

