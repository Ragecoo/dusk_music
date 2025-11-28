ALTER TABLE albums
    ALTER COLUMN  title TYPE varchar(255) ;

ALTER TABLE tracks
    ALTER COLUMN  title TYPE varchar(255) ;

INSERT INTO artists (artist_name, photo_url) VALUES
                                                 ('Deftones', 'covers/deftones_logo.jpg'),
                                                 ('Diplo', 'covers/trippie_red_logo.jpg'),
                                                 ('Trippie Redd','covers/trippie_red_logo.jpg'),
                                                 ('Kanye West','covers/kanye_logo.jpg'),
                                                 ('Jay-Z','covers/jayz_logo.jpg'),
                                                 ('Linkin Park','covers/Linkin_park_logo.jpg'),
                                                 ('Robert Miles','covers/rober_logo.jpg'),
                                                 ('The Police','covers/police_logo.jpg'),
                                                 ('Скриптонит','covers/scrip_logo.jpg');


INSERT INTO genres (id, title) VALUES
                                   (1, 'Rock'),
                                   (2, 'Hip-Hop'),
                                   (3, 'Nu-metal'),
                                   (4, 'Pop'),
                                   (5, 'Rap'),
                                   (6, 'House'),
                                   (7, 'Techno'),
                                   (8, 'Drum-n-bass'),
                                   (9, 'Metal'),
                                   (10, 'Alternative rock');





INSERT INTO albums (title, photo_url, release_date, artist_id)
VALUES
    ('Saturday Night Wrist', 'covers/night_wrist.jpg', '2006-10-31', (SELECT id FROM artists WHERE artist_name ILIKE 'Deftones')),
    ('California', 'covers/tripie_alb.jpg', '2018-03-23', (SELECT id FROM artists WHERE artist_name ILIKE 'Diplo')),
    ('Graduation', 'covers/graduation.jpg', '2007-09-11', (SELECT id FROM artists WHERE artist_name ILIKE 'Kanye West')),
    ('Watch The Throne', 'covers/throne.jpg', '2011-08-08', (SELECT id FROM artists WHERE artist_name ILIKE 'Jay-Z')),
    ('Meteora', 'covers/Meteora.jpg', '2003-03-25', (SELECT id FROM artists WHERE artist_name ILIKE 'Linkin Park')),
    ('Dreamland', 'covers/dreamland.jpg', '1996-06-07', (SELECT id FROM artists WHERE artist_name ILIKE 'Robert Miles')),
    ('Synchronicity', 'covers/police_alb.jpg', '1983-06-17', (SELECT id FROM artists WHERE artist_name ILIKE 'The Police')),
    ('Дом с нормальными явлениями', 'covers/dom_scrip.jpg', '2015-11-24', (SELECT id FROM artists WHERE artist_name ILIKE 'Скриптонит'));



-- V3__insert_demo_tracks.sql (пример имени для Flyway)

INSERT INTO tracks (
    artist_id,
    album_id,
    genre_id,
    title,
    duration,
    audio_url,
    cover_url,
    play_count,
    release_date
)
VALUES
-- Deftones – Beware
(
    (SELECT id FROM artists WHERE artist_name ILIKE 'Deftones'),
    (SELECT id FROM albums  WHERE title = 'Saturday Night Wrist'),
    1,
    'Deftones - Beware',
    360,                           -- 6:00
    'audio/Deftones_Beware.mp3',
    'covers/night_wrist.jpg',
    0,
    '2006-10-31 00:00:00'
),

-- Diplo feat. Trippie Redd – Wish
(
    (SELECT id FROM artists WHERE artist_name ILIKE 'Diplo'),
    (SELECT id FROM albums  WHERE title = 'California'),
    2,
    'Diplo feat. Trippie Redd - Wish',
    175,                           -- 2:55
    'audio/Diplo_Trippie_Redd_Wish.mp3',
    'covers/tripie_alb.jpg',
    0,
    '2018-03-23 00:00:00'
),

-- Kanye West feat. Dwele – Flashing Lights
(
    (SELECT id FROM artists WHERE artist_name ILIKE 'Kanye West'),
    (SELECT id FROM albums  WHERE title = 'Graduation'),
    2,
    'Kanye West feat. Dwele - Flashing Lights',
    237,                           -- 3:57
    'audio/Kanye_West feat. Dwele - Flash Lights.mp3',
    'covers/graduation.jpg',
    0,
    '2007-09-11 00:00:00'
),

-- JAY-Z & Kanye West feat. Mr Hudson – Why I Love You
(
    (SELECT id FROM artists WHERE artist_name ILIKE 'Jay-Z'),
    (SELECT id FROM albums  WHERE title = 'Watch The Throne'),
    2,
    'Jay-Z & Kanye West feat. Mr Hudson - Why I Love You',
    201,                           -- 3:21
    'audio/Kanye_West_Jay-Z_Feat_Mr_Hudson_Why_I_Love_You.mp3',
    'covers/throne.jpg',
    0,
    '2011-08-08 00:00:00'
),

-- Linkin Park – Numb
(
    (SELECT id FROM artists WHERE artist_name ILIKE 'Linkin Park'),
    (SELECT id FROM albums  WHERE title = 'Meteora'),
    3,
    'Linkin Park - Numb',
    186,                           -- как в твоём примере
    'audio/numb_track.mp3',
    'covers/Meteora.jpg',
    0,
    '2003-03-25 00:00:00'
),

-- Robert Miles – Children (Full Length)
(
    (SELECT id FROM artists WHERE artist_name ILIKE 'Robert Miles'),
    (SELECT id FROM albums  WHERE title = 'Dreamland'),
    7,
    'Robert Miles - Children (Full Length)',
    453,                           -- 7:33
    'audio/Robert_Miles_Children.mp3',
    'covers/dreamland.jpg',
    0,
    '1996-06-07 00:00:00'
),

-- The Police – Every Breath You Take
(
    (SELECT id FROM artists WHERE artist_name ILIKE 'The Police'),
    (SELECT id FROM albums  WHERE title = 'Synchronicity'),
    1,
    'The Police - Every Breath You Take',
    253,                           -- 4:13
    'audio/The_Police_Every_Breath_You_Take.mp3',
    'covers/police_alb.jpg',
    0,
    '1983-06-17 00:00:00'
),

-- Скриптонит – Положение
(
    (SELECT id FROM artists WHERE artist_name ILIKE 'Скриптонит'),
    (SELECT id FROM albums  WHERE title = 'Дом с нормальными явлениями'),
    2,
    'Скриптонит - Положение',
    283,                           -- 4:43
    'audio/Скриптонит - Положение.mp3',
    'covers/dom_scrip.jpg',
    0,
    '2015-11-24 00:00:00'
);


