import { createContext, useContext, useState } from 'react'
import Queue from '@src/types'

const AppContext = createContext(null)

export const AppProvider = ({ children }) => {
	const [queue, setQueue] = useState<Queue>([])
	const [currentTrack, setCurrentTrack] = useState(null)
	const [isPlaying, setIsPlaying] = useState<boolean>(false)
	const [trackTime, setTrackTime] = useState<number>(0)
	const [trackDuration, setTrackDuration] = useState<number>(0)
	const [trackVolume, setTrackVolume] = useState<number>(1)
	const [trackIsMuted, setTrackIsMuted] = useState<boolean>(false)
	const [curBpm, setCurBpm] = useState<number>(0)

	return (
		<AppContext.Provider
			value={{
				queue,
				setQueue,
				isPlaying,
				setIsPlaying,
				currentTrack,
				setCurrentTrack,
				trackTime,
				setTrackTime,
				trackDuration,
				setTrackDuration,
				trackVolume,
				setTrackVolume,
				curBpm,
				setCurBpm,
				trackIsMuted,
				setTrackIsMuted,
			}}
		>
			{children}
		</AppContext.Provider>
	)
}

export const useApp = () => useContext(AppContext)
