import { useParams } from 'react-router-dom'
import './PlaylistPage.css'

export default function PlaylistPage() {
	const { playlistId } = useParams()
	return <div className='playlistPage content'>playlist {playlistId}</div>
}
