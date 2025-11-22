const API_BASE_URL = 'http://localhost:8080/api/v1/auth'

class AuthService {
	// Логин
	async login(credentials: { usernameOrEmail: string; password: string }) {
		const response = await fetch(`${API_BASE_URL}/login`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(credentials),
		})

		if (!response.ok) {
			const error = await response.text()
			throw new Error(error)
		}

		const tokens = await response.json()
		this.setTokens(tokens)
		return tokens
	}

	// Регистрация
	async register(userData: {
		username: string
		email: string
		password: string
	}) {
		const response = await fetch(`${API_BASE_URL}/register`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify(userData),
		})

		if (!response.ok) {
			const error = await response.text()
			throw new Error(error)
		}

		const tokens = await response.json()
		this.setTokens(tokens)
		return tokens
	}

	// Обновление токена
	async refreshToken() {
		const refreshToken = localStorage.getItem('refreshToken')

		if (!refreshToken) {
			throw new Error('No refresh token available')
		}

		const response = await fetch(`${API_BASE_URL}/refresh`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
			},
			body: JSON.stringify({ refreshToken }),
		})

		if (!response.ok) {
			throw new Error('Refresh token invalid')
		}

		const tokens = await response.json()
		this.setTokens(tokens)
		return tokens.accessToken
	}

	// Сохранение токенов в localStorage
	setTokens(tokens: { accessToken: string; refreshToken: string }) {
		localStorage.setItem('accessToken', tokens.accessToken)
		localStorage.setItem('refreshToken', tokens.refreshToken)
	}

	// Выход
	logout() {
		localStorage.removeItem('accessToken')
		localStorage.removeItem('refreshToken')
	}

	// Проверка авторизации
	isAuthenticated() {
		return !!localStorage.getItem('accessToken')
	}

	// Получение токенов
	getTokens() {
		return {
			accessToken: localStorage.getItem('accessToken'),
			refreshToken: localStorage.getItem('refreshToken'),
		}
	}

	// Универсальный fetch с автоматическим обновлением токена
	async authFetch(url: string, options: RequestInit = {}) {
		let accessToken = localStorage.getItem('accessToken')

		const config = {
			...options,
			headers: {
				'Content-Type': 'application/json',
				Authorization: accessToken ? `Bearer ${accessToken}` : '',
				...options.headers,
			},
		}

		let response = await fetch(url, config)

		// Если токен просрочен, пытаемся обновить
		if (response.status === 401 && accessToken) {
			try {
				const newAccessToken = await this.refreshToken()

				// Повторяем исходный запрос с новым токеном
				config.headers.Authorization = `Bearer ${newAccessToken}`
				response = await fetch(url, config)
			} catch (error) {
				this.logout()
				throw error
			}
		}

		return response
	}
}

export default new AuthService()
