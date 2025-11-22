import { useParams } from 'react-router-dom'
import './ArtistPage.css'

export default function ArtistPage() {
	const { artistId } = useParams()
	return <div className='artistPage content'>artist {artistId}</div>
}
