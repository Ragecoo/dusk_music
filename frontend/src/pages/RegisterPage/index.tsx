import './RegisterPage.css'
import { useState } from 'react'
import authService from '../../api/authService'

export default function RegisterPage() {
	const [formData, setFormData] = useState({
		username: '',
		email: '',
		password: '',
	})
	const [isLoading, setIsLoading] = useState(false)
	const [errors, setErrors] = useState<{
		username?: string
		email?: string
		password?: string
	}>({})

	const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
		setFormData({
			...formData,
			[e.target.id]: e.target.value,
		})
		// Очищаем ошибки при вводе
		if (errors[e.target.id as keyof typeof errors]) {
			setErrors({
				...errors,
				[e.target.id]: undefined,
			})
		}
	}

	const validateForm = () => {
		const newErrors: { username?: string; email?: string; password?: string } =
			{}

		if (!formData.username || formData.username.length < 3) {
			newErrors.username = 'Имя пользователя должно быть не менее 3 символов'
		}

		const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
		if (!formData.email || !emailRegex.test(formData.email)) {
			newErrors.email = 'Пожалуйста, введите корректный email'
		}

		if (!formData.password || formData.password.length < 8) {
			newErrors.password = 'Пароль должен быть не менее 8 символов'
		}

		setErrors(newErrors)
		return Object.keys(newErrors).length === 0
	}

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault()

		if (!validateForm()) return

		setIsLoading(true)

		try {
			await authService.register(formData)
			alert('Регистрация успешна!')
			// Перенаправление на главную страницу
			window.location.href = '/'
		} catch (error: any) {
			// Обработка ошибок от сервера
			if (
				error.message.includes('Username') ||
				error.message.includes('username')
			) {
				setErrors({ username: 'Это имя пользователя уже занято' })
			} else if (
				error.message.includes('Email') ||
				error.message.includes('email')
			) {
				setErrors({ email: 'Этот email уже используется' })
			} else {
				alert(`Ошибка регистрации: ${error.message}`)
			}
		} finally {
			setIsLoading(false)
		}
	}

	return (
		<div className='registerPage content'>
			<canvas id='bgCanvas'></canvas>
			<section className='register-section'>
				<div className='register-container glass-morphism fade-in'>
					<h1 className='register-title'>Создать аккаунт</h1>
					<form
						id='registerForm'
						className='register-form'
						onSubmit={handleSubmit}
					>
						<div className='form-group'>
							<label htmlFor='username'>Имя пользователя</label>
							<input
								type='text'
								id='username'
								placeholder='Введите имя'
								required
								minLength={3}
								maxLength={50}
								value={formData.username}
								onChange={handleChange}
								disabled={isLoading}
								className={errors.username ? 'error' : ''}
							/>
							{errors.username && (
								<div className='error-message'>{errors.username}</div>
							)}
						</div>

						<div className='form-group'>
							<label htmlFor='email'>Электронная почта</label>
							<input
								type='email'
								id='email'
								placeholder='Введите email'
								required
								maxLength={100}
								value={formData.email}
								onChange={handleChange}
								disabled={isLoading}
								className={errors.email ? 'error' : ''}
							/>
							{errors.email && (
								<div className='error-message'>{errors.email}</div>
							)}
						</div>

						<div className='form-group'>
							<label htmlFor='password'>Пароль</label>
							<input
								type='password'
								id='password'
								placeholder='Введите пароль'
								required
								minLength={8}
								maxLength={64}
								value={formData.password}
								onChange={handleChange}
								disabled={isLoading}
								className={errors.password ? 'error' : ''}
							/>
							{errors.password && (
								<div className='error-message'>{errors.password}</div>
							)}
						</div>

						<button type='submit' className='register-btn' disabled={isLoading}>
							{isLoading ? 'Регистрация...' : 'Зарегистрироваться'}
						</button>
					</form>

					<p className='login-text'>
						Уже есть аккаунт?{' '}
						<a href='/login' className='login-link'>
							Войти
						</a>
					</p>
				</div>
			</section>
		</div>
	)
}
