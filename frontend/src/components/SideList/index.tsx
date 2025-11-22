import { useNavigate } from 'react-router-dom'
import './SideList.css'

export default function SideList() {
	const navigate = useNavigate()
	function handleToPlaylist(playlistId): void {
		navigate(`/playlist/${playlistId}`)
	}
	function handleToArtist(artistId): void {
		navigate(`/artist/${artistId}`)
	}
	return (
		<div className='sideList' id='sideList'>
			<p>sidelist</p>
			<button
				onClick={() => {
					handleToPlaylist('kek')
				}}
			>
				playlist kek
			</button>
			<button
				onClick={() => {
					handleToArtist('lol')
				}}
			>
				artist lol
			</button>
		</div>
	)
}
