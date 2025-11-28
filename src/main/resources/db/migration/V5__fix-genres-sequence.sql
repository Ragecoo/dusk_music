-- Исправление sequence для genres после ручной вставки с указанием ID
-- Это нужно, чтобы следующие автоинкременты работали корректно

SELECT setval('genres_id_seq', (SELECT MAX(id) FROM genres), true);

