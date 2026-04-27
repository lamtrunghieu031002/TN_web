import { useMemo, useState } from "react";
import { resetPassword } from "../services/authService"; // Đảm bảo hàm này đã được định nghĩa trong authService
import { createApiClient } from "../services/apiClient";
import { useNavigate, useLocation } from "react-router-dom";
import { API_BASE } from "../config/api";

export function ResetPassword() {
  const navigate = useNavigate();
  const location = useLocation();
  
  // Lấy email từ state được truyền từ trang ForgotPassword sang
  const email = location.state?.email || "";

  const [otp, setOtp] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const client = useMemo(() => createApiClient(API_BASE), []);

  const handleSubmit = async () => {
    // 1. Validation cơ bản
    if (!otp || !newPassword || !confirmPassword) {
      setError("Vui lòng nhập đầy đủ thông tin");
      return;
    }
    if (newPassword !== confirmPassword) {
      setError("Mật khẩu xác nhận không khớp");
      return;
    }
    if (newPassword.length < 6) {
      setError("Mật khẩu phải có ít nhất 6 ký tự");
      return;
    }

    setLoading(true);
    setError("");

    try {
      // 2. Gọi API reset password
      await resetPassword(client, {
        email: email,
        otp: otp,
        newPassword: newPassword,
      });

      setSuccess(true);
      // Sau 3 giây tự động chuyển về trang đăng nhập
      setTimeout(() => navigate("/login"), 3000);
      
    } catch (error) {
      console.error("Lỗi Reset Password:", error);
      const message =
        error.response?.data?.message ||
        error.message ||
        "Mã OTP không đúng hoặc đã hết hạn";
      setError(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-1 items-center justify-between max-w-5xl mx-auto w-full px-8 py-16">
      {/* Left - Giữ nguyên layout của bạn */}
      <div className="flex-1 pr-12">
        <h1 className="text-4xl font-bold mb-3">Đặt lại mật khẩu</h1>
        <p className="text-gray-500 leading-relaxed">
          Mã xác thực đã được gửi đến email: <br />
          <span className="font-semibold text-gray-800">{email}</span>
        </p>
      </div>

      {/* Right card */}
      <div className="w-[460px] bg-white border border-gray-200 rounded-xl p-8 shadow-sm">
        {!success ? (
          <div className="space-y-4">
            <div>
              <label className="text-xs font-bold text-gray-400 uppercase">Mã OTP</label>
              <input
                type="text"
                placeholder="Nhập 6 số OTP"
                value={otp}
                onChange={(e) => setOtp(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-4 py-2.5 text-sm mt-1 outline-none focus:border-red-400"
              />
            </div>

            <div>
              <label className="text-xs font-bold text-gray-400 uppercase">Mật khẩu mới</label>
              <input
                type="password"
                placeholder="••••••••"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-4 py-2.5 text-sm mt-1 outline-none focus:border-red-400"
              />
            </div>

            <div>
              <label className="text-xs font-bold text-gray-400 uppercase">Xác nhận mật khẩu</label>
              <input
                type="password"
                placeholder="••••••••"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="w-full border border-gray-300 rounded-lg px-4 py-2.5 text-sm mt-1 outline-none focus:border-red-400"
              />
            </div>

            {error && <p className="text-red-600 text-sm">{error}</p>}

            <button
              onClick={handleSubmit}
              disabled={loading}
              className="w-full bg-red-700 hover:bg-red-800 text-white py-2.5 rounded-lg font-medium text-sm mt-2 transition-colors"
            >
              {loading ? "Đang xử lý..." : "Cập nhật mật khẩu"}
            </button>
          </div>
        ) : (
          <div className="text-center">
            <div className="w-14 h-14 rounded-full bg-green-100 flex items-center justify-center mx-auto mb-4">
              ✅
            </div>
            <p className="font-medium mb-2">Thành công!</p>
            <p className="text-sm text-gray-500 mb-4 leading-relaxed">
              Mật khẩu của bạn đã được thay đổi. <br /> Đang chuyển hướng về trang đăng nhập...
            </p>
          </div>
        )}
      </div>
    </div>
  );
}