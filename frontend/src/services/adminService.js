import axios from 'axios';
import { AUTH_STORAGE_KEY } from '../constants/storage';
import { API_BASE } from '../config/api';

const api = axios.create({
  baseURL: API_BASE,
});

api.interceptors.request.use((config) => {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY);
  if (raw) {
    const auth = JSON.parse(raw);
    if (auth?.token) {
      config.headers.Authorization = `Bearer ${auth.token}`;
    }
  }
  return config;
});

// Problem management (đúng endpoint backend: /api/problems)
export const getAllProblems = () => api.get('/api/problems');
export const createProblem = (data) => api.post('/api/problems', data);
export const updateProblem = (id, data) => api.put(`/api/problems/${id}`, data);
export const deleteProblem = (id) => api.delete(`/api/problems/${id}`);

// User management (đúng endpoint backend: /api/users)
export const getAllUsers = () => api.get('/api/users/all');
export const updateUserRole = (userId, roles) => api.put(`/api/users/${userId}/roles`, { roles });
