import './LandingPage.css'

export default function LandingPage() {
	return (
		<div className='landingPage landing'>
			<nav className='nav'>
				<div className='nav-container'>
					<div className='nav-content'>
						<div className='logo-container'>
							<img
								src='images/logo.svg'
								alt='Dusk Music'
								className='logo-img'
							/>
							<span className='logo-text'>dusk</span>
						</div>

						<div className='nav-menu'>
							<a href='#features' className='nav-link'>
								Возможности
							</a>
							<a href='#about' className='nav-link'>
								О нас
							</a>
							<button className='nav-button' onClick={() => {}}>
								Регистрация
							</button>
						</div>
					</div>
				</div>
			</nav>

			<section className='hero'>
				<div className='hero-container'>
					<div className='hero-grid'>
						<div className='hero-content fade-in-up'>
							<h1 className='hero-title'>
								Получайте удовольствие
								<br />
								<span className='hero-title-gradient'>от Музыки с dusk</span>
							</h1>
							<p className='hero-description'>
								Откройте мир чистого звука с dusk. Персонализированные
								плейлисты, эксклюзивные рекомендации и бесконечная музыка,
								которая становится частью вашей истории.
							</p>
							<button className='hero-button' onClick={() => {}}>
								Попробовать
							</button>
						</div>

						<div className='hero-image-container'>
							<img
								src='images/кайф.png'
								alt='Dusk Music Experience'
								className='hero-image float-3d'
							/>
						</div>
					</div>
				</div>
			</section>

			<section id='features' className='features'>
				<div className='features-container'>
					<h2 className='features-title fade-in-up'>Почему dusk?</h2>
					<div className='features-grid'>
						<div className='feature-card fade-in-up'>
							<div className='feature-icon-container'>
								<i data-feather='headphones' className='feature-icon'></i>
							</div>
							<h3 className='feature-title'>Беспрецедентное качество</h3>
							<p className='feature-description'>
								Наслаждайтесь музыкой в максимальном качестве с технологией
								потоковой передачи без потерь. Каждый инструмент, каждый голос —
								кристально чистый.
							</p>
						</div>
						<div className='feature-card fade-in-up'>
							<div className='feature-icon-container'>
								<i data-feather='zap' className='feature-icon'></i>
							</div>
							<h3 className='feature-title'>Умный подбор</h3>
							<p className='feature-description'>
								Наш ИИ изучает ваши вкусы и предлагает музыку, которая идеально
								соответствует вашему настроению и моменту. Открывайте новое
								каждый день.
							</p>
						</div>
						<div className='feature-card fade-in-up'>
							<div className='feature-icon-container'>
								<i data-feather='globe' className='feature-icon'></i>
							</div>
							<h3 className='feature-title'>Вся музыка мира</h3>
							<p className='feature-description'>
								Миллионы треков от легендарных исполнителей и rising stars. От
								классики до самых свежих релизов — всё в одном месте.
							</p>
						</div>
					</div>
				</div>
			</section>

			<section id='about' className='about'>
				<div className='about-container'>
					<div className='about-card fade-in-up'>
						<h2 className='about-title'>dusk — это больше чем музыка</h2>
						<p className='about-description'>
							Мы создали пространство, где музыка становится частью вашей жизни.
							Где каждый трек — это не просто звук, а эмоция, воспоминание,
							состояние души.
						</p>
						<p className='about-description'>
							Наша миссия — дать вам не просто доступ к музыке, а возможность
							переживать её заново каждый день. Откройте dusk и начните слушать
							по-новому.
						</p>
					</div>
				</div>
			</section>

			<footer className='footer'>
				<div className='footer-container'>
					<p className='footer-text'>© 2024 dusk. Переживайте музыку заново.</p>
				</div>
			</footer>
		</div>
	)
}
