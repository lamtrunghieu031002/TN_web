export function login(client, payload) {
  return client.request('/api/auth/login', {
    method: 'POST',
    body: payload,
  })
}

export function register(client, form) {
  return client.request('/api/auth/register', {
    method: 'POST',
    body: {
      username: form.username,
      email: form.email,
      password: form.password,
      roles: [form.role],
    },
  })
}

export function forgotPassword(client, payload) {
  return client.request('/api/auth/forgot-password', {
    method: 'POST',
    body: { email: payload },
  })
}

export function resetPassword(client, payload) {
  return client.request('/api/auth/reset-password', {
    method: 'POST',
    body: payload,
  })
}
