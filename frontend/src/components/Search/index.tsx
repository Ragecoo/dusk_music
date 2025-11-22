import './Search.css'
import SearchBar from './SearchBar'
import SearchDropdown from './SearchDropdown'

export default function Search() {
	return (
		<div className='search' id='search'>
			<p>search</p>
			<SearchBar></SearchBar>
			<SearchDropdown></SearchDropdown>
		</div>
	)
}
