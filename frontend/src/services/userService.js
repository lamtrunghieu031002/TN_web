export function getUserProfile(client, username) {
  return client.request(`/api/users/${username}`, { useAuth: true })
}

export function updateProfile(client, username, payload) {
  return client.request(`/api/users/${username}/profile`, {
    method: 'PUT',
    body: payload,
    useAuth: true,
  })
}

export function changePassword(client, payload) {
  return client.request('/api/users/password', {
    method: 'PUT',
    body: payload,
    useAuth: true,
  })
}

export function getAllUsers(client) {
  return client.request('/api/users', { useAuth: true })
}
