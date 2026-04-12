export function createApiClient(apiBase, token) {
  const request = async (path, { method = 'GET', body, useAuth = false } = {}) => {
    const headers = { 'Content-Type': 'application/json' }

    if (useAuth && token) {
      headers.Authorization = `Bearer ${token}`
    }

    const response = await fetch(`${apiBase}${path}`, {
      method,
      headers,
      body: body ? JSON.stringify(body) : undefined,
    })

    const text = await response.text()
    let parsed = text
    try {
      parsed = text ? JSON.parse(text) : null
    } catch {
      // Keep plain text if backend returns raw message string
    }

    if (!response.ok) {
      const errorMessage =
        (typeof parsed === 'object' && parsed?.message) ||
        (typeof parsed === 'string' && parsed) ||
        `HTTP ${response.status}`
      throw new Error(errorMessage)
    }

    return parsed
  }

  return { request }
}
