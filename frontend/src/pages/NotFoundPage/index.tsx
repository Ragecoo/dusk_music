import './NotFoundPage.css'
import { useNavigate } from 'react-router-dom'

export default function NotFoundPage() {
	const navigate = useNavigate()
	function handleBack(): void {
		window.history.state && window.history.state.idx > 0
			? navigate(-1)
			: navigate('/')
	}

	return (
		<div className='notFoundPage content'>
			notFound <button onClick={handleBack}></button>
		</div>
	)
}
