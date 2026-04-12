import { useMemo, useState } from 'react';
import { API_BASE } from '../config/api';
import { createApiClient } from '../services/apiClient';
import * as userService from '../services/userService';
import { useAuth } from '../hooks/useAuth';

export function ChangePassword() {
  const { auth } = useAuth();
  
  // Khởi tạo client có đính kèm token
  const client = useMemo(() => createApiClient(API_BASE, auth?.token), [auth?.token]);

  // Các state quản lý form và thông báo
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });
  const [pwForm, setPwForm] = useState({ 
    oldPassword: '', 
    newPassword: '', 
    confirmPassword: '' 
  });

  const handleSubmit = async (event) => {
    event.preventDefault(); // Ngăn trình duyệt load lại trang
    setMessage({ type: '', text: '' });

    // 1. Kiểm tra đầu vào
    if (!pwForm.oldPassword || !pwForm.newPassword || !pwForm.confirmPassword) {
      setMessage({ type: 'error', text: 'Vui lòng nhập đầy đủ thông tin!' });
      return;
    }
    if (pwForm.newPassword !== pwForm.confirmPassword) {
      setMessage({ type: 'error', text: 'Mật khẩu xác nhận không khớp!' });
      return;
    }

    // 2. Gọi API
    setLoading(true);
    try {
      if (!auth?.token) {
        throw new Error('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.');
      }

      await userService.changePassword(client, {
        oldPassword: pwForm.oldPassword,
        newPassword: pwForm.newPassword
      });

      // 3. Xử lý khi thành công
      setMessage({ type: 'success', text: 'Đổi mật khẩu thành công!' });
      setPwForm({ oldPassword: '', newPassword: '', confirmPassword: '' }); // Xóa trắng form

    } catch (error) {
      // Xử lý khi lỗi (sai mật khẩu cũ)
      setMessage({ type: 'error', text: `Lỗi: ${error.message || 'Không thể đổi mật khẩu'}` });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
      <h2 className="text-xl font-bold text-brand-red">Đổi mật khẩu</h2>
      
      <form onSubmit={handleSubmit} className="mt-4 space-y-3">
        <input
          type="password"
          className="w-full rounded-md border border-slate-300 px-3 py-2 focus:border-brand-red focus:outline-none"
          placeholder="Mật khẩu cũ"
          value={pwForm.oldPassword}
          onChange={(e) => setPwForm((p) => ({ ...p, oldPassword: e.target.value }))}
        />
        
        <input
          type="password"
          className="w-full rounded-md border border-slate-300 px-3 py-2 focus:border-brand-red focus:outline-none"
          placeholder="Mật khẩu mới"
          value={pwForm.newPassword}
          onChange={(e) => setPwForm((p) => ({ ...p, newPassword: e.target.value }))}
        />
        
        <input
          type="password"
          className="w-full rounded-md border border-slate-300 px-3 py-2 focus:border-brand-red focus:outline-none"
          placeholder="Xác nhận mật khẩu mới"
          value={pwForm.confirmPassword}
          onChange={(e) => setPwForm((p) => ({ ...p, confirmPassword: e.target.value }))}
        />
        
        <button
          type="submit"
          disabled={loading}
          className="w-full rounded-md bg-brand-red px-4 py-2 text-sm font-semibold text-white hover:bg-brand-redDark disabled:opacity-60"
        >
          {loading ? 'Đang xử lý...' : 'Xác nhận đổi mật khẩu'}
        </button>
      </form>

      {/* Hiển thị thông báo */}
      {message.text && (
        <p className={`mt-4 rounded-md p-3 text-sm font-medium border ${
          message.type === 'error' 
            ? 'bg-red-50 text-red-600 border-red-100' 
            : 'bg-green-50 text-green-600 border-green-100'
        }`}>
          {message.text}
        </p>
      )}
    </div>
  );
}