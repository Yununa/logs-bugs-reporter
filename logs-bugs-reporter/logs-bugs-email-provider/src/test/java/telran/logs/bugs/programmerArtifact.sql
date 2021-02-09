DELETE FROM artifacts;
DELETE FROM programmers;
INSERT INTO programmers (id, name, email) VALUES (123, 'Moshe', 'moshe@gmail.com');
INSERT INTO artifacts (artifact_id, programmer_id) VALUES ('artifact1', 123);