import { useState } from 'react'
import './LoginPage.css'
import authService from '../../api/authService'

export default function LoginPage() {
	const [email, setEmail] = useState('')
	const [password, setPassword] = useState('')

	const [errors, setErrors] = useState<{ email?: string; password?: string }>(
		{}
	)
	const [loading, setLoading] = useState(false)
	const [success, setSuccess] = useState(false)

	function validate() {
		const newErrors: any = {}

		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

		if (!email) newErrors.email = 'Пожалуйста, введите email'
		else if (!emailRegex.test(email))
			newErrors.email = 'Пожалуйста, введите корректный email'

		if (!password) newErrors.password = 'Пожалуйста, введите пароль'

		setErrors(newErrors)

		return Object.keys(newErrors).length === 0
	}

	async function handleSubmit(e: React.FormEvent) {
		e.preventDefault()

		if (!validate()) return

		setLoading(true)
		setErrors({})

		try {
			await authService.login({
				usernameOrEmail: email,
				password: password,
			})

			setSuccess(true)
			setLoading(false)

			// Перенаправляем на главную страницу после успешного входа
			setTimeout(() => {
				window.location.href = '/'
			}, 1500)
		} catch (error: any) {
			setLoading(false)
			// Обрабатываем ошибки от сервера
			if (
				error.message.includes('Invalid credentials') ||
				error.message.includes('401')
			) {
				setErrors({ password: 'Неверный email или пароль' })
			} else {
				setErrors({ password: 'Ошибка при входе. Попробуйте снова.' })
			}
		}
	}

	return (
		<div className='loginPage content'>
			<section className='login-section'>
				<div className='login-container glass-morphism fade-in'>
					<h1 className='login-title'>Вход в аккаунт</h1>

					<form className='login-form' onSubmit={handleSubmit}>
						<div className='form-group'>
							<label>Email</label>
							<input
								type='text'
								value={email}
								onChange={e => setEmail(e.target.value)}
								className={errors.email ? 'error' : ''}
								placeholder='Введите email'
								disabled={loading || success}
							/>
							{errors.email && (
								<div className='error-message'>{errors.email}</div>
							)}
						</div>

						<div className='form-group'>
							<label>Пароль</label>
							<input
								type='password'
								value={password}
								onChange={e => setPassword(e.target.value)}
								className={errors.password ? 'error' : ''}
								placeholder='Введите пароль'
								disabled={loading || success}
							/>
							{errors.password && (
								<div className='error-message'>{errors.password}</div>
							)}
						</div>

						<button
							type='submit'
							className='login-btn'
							disabled={loading || success}
						>
							{loading ? (
								<div className='loading-spinner' />
							) : success ? (
								'✓ Успешный вход!'
							) : (
								'Войти'
							)}
						</button>
					</form>

					<p className='register-text'>
						Нет аккаунта?{' '}
						<a href='/register' className='register-link'>
							Зарегистрироваться
						</a>
					</p>
				</div>
			</section>
		</div>
	)
}
