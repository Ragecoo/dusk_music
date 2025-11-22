import SearchBar from '@components/Search/SearchBar'
import AudioPlayer from '@components/AudioPlayer'
import {
	Route,
	Routes,
	BrowserRouter,
	Link,
	Navigate,
	useLocation,
} from 'react-router-dom'

import './Layout.css'
import HomePage from './pages/HomePage'
import PlaylistPage from './pages/PlaylistPage'
import ArtistPage from './pages/ArtistPage'
import NotFoundPage from './pages/NotFoundPage'
import SideList from './components/SideList'
import Search from './components/Search'
import LandingPage from './pages/LandingPage'
import LoginPage from './pages/LoginPage'
import RegisterPage from './pages/RegisterPage'

export default function Layout() {
	const location = useLocation()
	const simplePaths = ['/', '/login', '/register']
	const isSimple = simplePaths.includes(location.pathname)
	if (isSimple) {
		return (
			<div id='siteContainer' className='siteContainer'>
				<Routes>
					<Route path='/' element={<LandingPage />} />
					<Route path='/login' element={<LoginPage />} />
					<Route path='/register' element={<RegisterPage />} />
				</Routes>
			</div>
		)
	} else {
		return (
			<div id='siteContainer' className='siteContainer'>
				<main id='wrapper' className='wrapper'>
					<section className='siteSection siteSection_1 layoutBlock layoutBlock_search'>
						<Search></Search>
					</section>
					<section className='siteSection siteSection_2 layoutBlock layoutBlock_content'>
						<Routes>
							<Route path='/' element={<HomePage />} />
							<Route path='/playlist'>
								<Route index element={<Navigate to={'/'} />} />
								<Route path=':playlistId' element={<PlaylistPage />} />
							</Route>
							<Route path='/artist'>
								<Route index element={<Navigate to={'/'} />} />
								<Route path=':artistId' element={<ArtistPage />} />
							</Route>
							<Route path='*' element={<NotFoundPage />} />
						</Routes>
					</section>
					<section className='siteSection siteSection_3 layoutBlock layoutBlock_player'>
						<AudioPlayer />
					</section>
					<section className='siteSection siteSection_4 layoutBlock layoutBlock_list'>
						<SideList />
					</section>
				</main>
			</div>
		)
	}
}
