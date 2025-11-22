// Функционал восстановления пароля
class ForgotPassword {
    constructor() {
        this.modal = null;
        this.init();
    }

    init() {
        this.createModal();
        this.setupEventListeners();
    }

    createModal() {
        const modalHTML = `
            <div class="modal-overlay" id="forgotPasswordModal">
                <div class="modal-content">
                    <button class="close-modal">&times;</button>
                    <h2 class="modal-title">Восстановление пароля</h2>
                    <p class="modal-description">
                        Введите email, указанный при регистрации, и мы вышлем вам ссылку для восстановления пароля.
                    </p>
                    <form class="modal-form" id="forgotPasswordForm">
                        <div class="form-group">
                            <label for="recoveryEmail">Электронная почта</label>
                            <input type="email" id="recoveryEmail" placeholder="Введите ваш email" required />
                        </div>
                        <div class="modal-buttons">
                            <button type="submit" class="modal-btn primary">Отправить ссылку</button>
                            <button type="button" class="modal-btn secondary" id="cancelRecovery">Отмена</button>
                        </div>
                        <div class="success-message" id="successMessage">
                            Ссылка для восстановления отправлена на ваш email!
                        </div>
                    </form>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHTML);
        this.modal = document.getElementById('forgotPasswordModal');
    }

    setupEventListeners() {
        // Открытие модального окна
        document.querySelector('.forgot-password').addEventListener('click', (e) => {
            e.preventDefault();
            this.openModal();
        });

        // Закрытие модального окна
        this.modal.querySelector('.close-modal').addEventListener('click', () => {
            this.closeModal();
        });

        this.modal.querySelector('#cancelRecovery').addEventListener('click', () => {
            this.closeModal();
        });

        // Клик вне модального окна
        this.modal.addEventListener('click', (e) => {
            if (e.target === this.modal) {
                this.closeModal();
            }
        });

        // Отправка формы
        document.getElementById('forgotPasswordForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.handlePasswordRecovery();
        });

        // ESC для закрытия
        document.addEventListener('keydown', (e) => {
            if (e.key === 'Escape' && this.modal.classList.contains('active')) {
                this.closeModal();
            }
        });
    }

    openModal() {
        this.modal.classList.add('active');
        document.getElementById('recoveryEmail').focus();
    }

    closeModal() {
        this.modal.classList.remove('active');
        this.resetForm();
    }

    resetForm() {
        document.getElementById('forgotPasswordForm').reset();
        document.getElementById('successMessage').classList.remove('show');
    }

    handlePasswordRecovery() {
        const email = document.getElementById('recoveryEmail').value;
        const submitBtn = this.modal.querySelector('.modal-btn.primary');
        const successMessage = document.getElementById('successMessage');

        // Валидация email
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!email || !emailRegex.test(email)) {
            this.showError('Пожалуйста, введите корректный email адрес');
            return;
        }

        // Показываем загрузку
        const originalText = submitBtn.textContent;
        submitBtn.innerHTML = '<div class="loading-spinner"></div>Отправка...';
        submitBtn.disabled = true;

        // Имитация отправки запроса
        setTimeout(() => {
            // Успешная отправка
            submitBtn.style.display = 'none';
            this.modal.querySelector('.modal-buttons').style.display = 'none';
            successMessage.classList.add('show');

            // Автоматическое закрытие через 3 секунды
            setTimeout(() => {
                this.closeModal();
            }, 3000);
        }, 1500);
    }

    showError(message) {
        // Можно добавить красивый вывод ошибок
        alert(message); // Временное решение
    }
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', () => {
    new ForgotPassword();
});