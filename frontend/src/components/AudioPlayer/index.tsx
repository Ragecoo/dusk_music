import { useApp } from '@src/AppContext'
import { useEffect, useRef, useState } from 'react'
import './AudioPlayer.css'

import { Star as Icon_Star, LucideGitCompareArrows } from 'lucide-react'
import { ListMusic as Icon_ListMusic } from 'lucide-react'
import { Play as Icon_Play } from 'lucide-react'
import { Ellipsis as Icon_Ellipsis } from 'lucide-react'
import { Volume2 as Icon_Volume2 } from 'lucide-react'
import { Repeat as Icon_Repeat } from 'lucide-react'
import { Repeat1 as Icon_Repeat1 } from 'lucide-react'
import { Shuffle as Icon_Shuffle } from 'lucide-react'
import { SkipForward as Icon_SkipForward } from 'lucide-react'
import { SkipBack as Icon_SkipBack } from 'lucide-react'
import { Pause as Icon_Pause } from 'lucide-react'

export default function AudioPlayer() {
	const {
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
	} = useApp()

	const audioRef = useRef<HTMLAudioElement | null>(null)
	const progressBarRef = useRef<HTMLDivElement | null>(null)
	const progressBarFillRef = useRef<HTMLDivElement | null>(null)
	const volumeBarRef = useRef<HTMLDivElement | null>(null)
	const volumeFillRef = useRef<HTMLDivElement | null>(null)

	const [isDraggingTime, setIsDraggingTime] = useState<boolean>()
	const [isDraggingVolume, setIsDraggingVolume] = useState(false)

	async function ref_togglePlay() {
		if (!audioRef.current) return
		if (isPlaying) {
			await ref_fadeVolume(0, 0.2)
			setIsPlaying(false)
		} else {
			setIsPlaying(true)
			await ref_fadeVolume(trackVolume, 0.2)
		}
	}
	function ref_skip(sec: number): void {
		if (!audioRef.current) return
		audioRef.current.currentTime += sec
		ref_updateTime()
	}
	function ref_updateTime(): void {
		if (!audioRef.current) return
		setTrackTime(audioRef.current.currentTime)
		if (!isDraggingTime)
			progressBarFillRef.current.style.width =
				(audioRef.current.currentTime / trackDuration) * 100 + '%'
	}
	async function ref_handleMute() {
		if (trackIsMuted) {
			setTrackIsMuted(false)
			await ref_fadeVolume(trackVolume, 0.2)
		} else {
			await ref_fadeVolume(0, 0.2)
			setTrackIsMuted(true)
		}
	}
	function handleChangeVolume(e: React.ChangeEvent<HTMLInputElement>): void {
		setTrackVolume(e.target.value)
	}
	function handleEnded(): void {
		setIsPlaying(false)
		alert('kekek')
	}
	function ref_handlePrev(): void {
		if (trackTime > 5 && isPlaying) {
			audioRef.current.currentTime = 0
			ref_updateTime()
			setTrackTime(0)
			setIsPlaying(true)
			return
		}
		alert('prev') // todo назад
	}
	function handleNext(): void {
		alert('next') // todo вперед
	}
	async function ref_fadeVolume(to: number, inSec: number): Promise<boolean> {
		if (!audioRef.current) return
		const from = audioRef.current.volume
		const diff = to - from
		const j = 30
		let i = 0
		return new Promise(resolve => {
			const interval = setInterval(() => {
				i++
				audioRef.current.volume = from + (diff * i) / j
				if (i >= j) {
					clearInterval(interval)
					audioRef.current.volume = to
					resolve(true)
				}
			}, (inSec * 1000) / j)
		})
	}
	function formatTime(sec: number) {
		if (isNaN(sec) || sec < 0) return '00:00'
		const m = String(Math.floor(sec / 60)).padStart(2, '0')
		const s = String(Math.floor(sec % 60)).padStart(2, '0')
		return `${m}:${s}`
	}

	function handleTimeBarMouseDown(e: React.MouseEvent<HTMLDivElement>) {
		setIsDraggingTime(true)
		updateTimeBarProgress(e)
		document.addEventListener('mousemove', handleTimeBarMouseMove)
		document.addEventListener('mouseup', handleTimeBarMouseUp)
	} // seek time
	function handleTimeBarMouseMove(e: MouseEvent) {
		updateTimeBarProgress(e)
	} // seek time
	function handleTimeBarMouseUp(e: MouseEvent) {
		const newTime = updateTimeBarProgress(e)
		setIsDraggingTime(false)
		document.removeEventListener('mousemove', handleTimeBarMouseMove)
		document.removeEventListener('mouseup', handleTimeBarMouseUp)
		if (audioRef.current && typeof newTime === 'number') {
			audioRef.current.currentTime = newTime
		}
	} // seek time
	function updateTimeBarProgress(e: MouseEvent | React.MouseEvent) {
		if (!progressBarRef.current || !audioRef.current) return
		const rect = progressBarRef.current.getBoundingClientRect()
		const x = Math.min(Math.max(e.clientX - rect.left, 0), rect.width)
		const percent = x / rect.width
		progressBarFillRef.current.style.width = percent * 100 + '%'
		return percent * trackDuration
	} // seek time
	function handleVolumeBarMouseDown(e: React.MouseEvent<HTMLDivElement>) {
		setIsDraggingVolume(true)
		updateVolumeBarProgress(e)
		document.addEventListener('mousemove', handleVolumeBarMouseMove)
		document.addEventListener('mouseup', handleVolumeBarMouseUp)
	} // seek volume
	function handleVolumeBarMouseMove(e: MouseEvent) {
		updateVolumeBarProgress(e)
	} // seek volume
	function handleVolumeBarMouseUp() {
		setIsDraggingVolume(false)
		document.removeEventListener('mousemove', handleVolumeBarMouseMove)
		document.removeEventListener('mouseup', handleVolumeBarMouseUp)
	} // seek volume
	function updateVolumeBarProgress(e: MouseEvent | React.MouseEvent) {
		if (!volumeBarRef.current || !audioRef.current || !volumeFillRef.current)
			return
		const rect = volumeBarRef.current.getBoundingClientRect()
		const y = Math.min(Math.max(e.clientY - rect.top, 0), rect.height)
		const percent = 1 - y / rect.height
		volumeFillRef.current.style.height = `${percent * 100}%`
		setTrackVolume(percent)
		audioRef.current.volume = percent
	} // seek volume

	useEffect(() => {
		if (audioRef.current) {
			console.log(audioRef.current)
			audioRef.current.src = '/audios/test.mp3'
			audioRef.current.onloadedmetadata = () => {
				setTrackDuration(audioRef.current.duration)
			}
		}
	}, [currentTrack])
	useEffect(() => {
		if (audioRef.current) {
			if (isPlaying) {
				audioRef.current.play().catch(e => {
					console.error('ошибка при play:', e)
				})
			} else {
				audioRef.current.pause()
			}
		}
	}, [isPlaying])
	useEffect(() => {
		if (!audioRef.current) return
		audioRef.current.muted = trackIsMuted
	}, [trackIsMuted])
	useEffect(() => {
		const keyboardKeydown = e => {
			switch (e.code) {
				case 'Space':
					e.preventDefault()
					ref_togglePlay()
					break
				case 'ArrowLeft':
					e.preventDefault()
					ref_skip(-5)
					console.log(-5)
					break
				case 'ArrowRight':
					e.preventDefault()
					ref_skip(5)
					console.log(5)
					break
				default:
					break
			}
		}
		window.addEventListener('keydown', keyboardKeydown)
		return () => {
			window.removeEventListener('keydown', keyboardKeydown)
		}
	}, [isPlaying])
	useEffect(() => {
		if (!audioRef.current) return
		audioRef.current.volume = trackVolume
		volumeFillRef.current.style.height = `${trackVolume * 100}%`
	}, [trackVolume])
	useEffect(() => {
		if (!audioRef.current) return
		const updateTime = () => {
			if (!isDraggingTime) setTrackTime(audioRef.current!.currentTime)
		}
		audioRef.current.addEventListener('timeupdate', updateTime)
		return () => audioRef.current?.removeEventListener('timeupdate', updateTime)
	}, [isDraggingTime])

	return (
		<div className='audioPlayer' id='audioPlayer'>
			<audio
				className='audioPlayerAudio'
				id='audio'
				ref={audioRef}
				onTimeUpdate={ref_updateTime}
				onEnded={handleEnded}
			/>
			<div className='audioPlayerPart audioPlayerPartLeft'>
				<img
					className='audioPlayerAlbum'
					src='https://placehold.co/72x72'
					alt='name'
				/>
				<div className='audioPlayerInfo'>
					<div className='audioPlayerTrackName'>title</div>
					<div className='audioPlayerTrackArtist'>author</div>
				</div>
			</div>
			<div className='audioPlayerPart audioPlayerPartMid'>
				<div className='audioPlayerControls'>
					<button
						className='audioPlayerInput inputPrev'
						id='inputPrev'
						onClick={ref_handlePrev}
					>
						<Icon_SkipBack />
					</button>
					<button
						className={`audioPlayerInput inputPlay ${isPlaying ? 'on' : 'off'}`}
						onClick={() => {
							ref_togglePlay()
						}}
					>
						{isPlaying ? <Icon_Pause /> : <Icon_Play />}
					</button>
					<button
						className='audioPlayerInput inputNext'
						id='inputNext'
						onClick={handleNext}
					>
						<Icon_SkipForward />
					</button>
				</div>
				<div className='audioPlayerPrbar'>
					<div className='prbarTime'>{formatTime(trackTime)}</div>
					<div className='prbar'>
						<div
							id='progressBarLabel'
							ref={progressBarRef}
							onMouseDown={handleTimeBarMouseDown}
						></div>
						<div className='audioPlayerProgress progressDiv' id='progressBar'>
							<div
								ref={progressBarFillRef}
								className='audioPlayerProgress progressDiv'
								id='progressBarFill'
							/>
						</div>
					</div>
					<div className='prbarDuration'>{formatTime(trackDuration)}</div>
				</div>
			</div>
			<div className='audioPlayerPart audioPlayerPartRight'>
				<div className='audioPlayerVolume'>
					<div
						className='volumeBarLabel'
						ref={volumeBarRef}
						onMouseDown={handleVolumeBarMouseDown}
					>
						<div className='volumeBar'>
							<div ref={volumeFillRef} className='volumeBarFill' />
						</div>
					</div>
					<button
						className='audioPlayerInput inputMute'
						id='inputMute'
						onClick={ref_handleMute}
					>
						<Icon_Volume2 />
					</button>
				</div>
			</div>
		</div>
	)
}
