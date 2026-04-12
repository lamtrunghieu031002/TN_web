import { useMemo, useState } from "react";
import { forgotPassword } from "../services/authService";
import { createApiClient } from "../services/apiClient";
import { useNavigate } from "react-router-dom";
import { API_BASE } from "../config/api";

export function ForgotPassword() {
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const client = useMemo(() => createApiClient(API_BASE), []);
  const navigate = useNavigate();

  const handleSubmit = async () => {
    if (!email) {
      setError("Vui lòng nhập email");
      return;
    }
    
    setLoading(true);
    setError(""); // Reset lỗi trước khi gọi API

    try {
      await forgotPassword(client, email);
      
      // CHỦ CHỐT: Gửi xong là đi luôn sang trang nhập OTP
      // Truyền email theo state để trang ResetPassword không cần bắt người dùng nhập lại
      navigate('/reset-password', { state: { email: email } });
      
    } catch (error) {
      console.error("Lỗi gửi mail:", error);
      const message =
        error.response?.data?.message ||
        error.message ||
        "Không tìm thấy email hoặc lỗi hệ thống, vui lòng thử lại";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-1 items-center justify-between max-w-5xl mx-auto w-full px-8 py-16">
      {/* Left */}
      <div className="flex-1 pr-12">
        <h1 className="text-4xl font-bold mb-3">Quên mật khẩu</h1>
        <p className="text-gray-500 leading-relaxed">
          Nhập email của bạn để nhận mã xác thực (OTP)<br />
          để đặt lại mật khẩu mới.
        </p>
      </div>

      {/* Right card */}
      <div className="w-[460px] bg-white border border-gray-200 rounded-xl p-8 shadow-sm">
        <div className="mb-6">
          <label className="text-xs font-bold text-gray-400 uppercase">Địa chỉ Email</label>
          <input
            type="email"
            placeholder="example@gmail.com"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            className="w-full border border-gray-300 rounded-lg px-4 py-2.5 text-sm mt-1 outline-none focus:border-red-400"
          />
        </div>

        {error && <p className="text-red-600 text-sm mb-4 italic">{error}</p>}

        <button
          onClick={handleSubmit}
          disabled={loading}
          className="w-full bg-red-700 hover:bg-red-800 text-white py-2.5 rounded-lg font-medium text-sm transition-all"
        >
          {loading ? (
            <span className="flex items-center justify-center">
               Đang gửi mail...
            </span>
          ) : (
            "Tiếp tục"
          )}
        </button>

        <p className="text-center text-sm text-gray-500 mt-6">
          Nhớ mật khẩu rồi?{" "}
          <span
            onClick={() => navigate("/login")}
            className="text-red-700 cursor-pointer font-medium hover:underline"
          >
            Đăng nhập ngay
          </span>
        </p>
      </div>
    </div>
  );
}