INSERT INTO users (username, password, role)
SELECT 'admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (username, password, role)
SELECT 'alice', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'WRITER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'alice');

INSERT INTO users (username, password, role)
SELECT 'bob', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'WRITER'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'bob');

INSERT INTO posts (title, body, author, tag, votes)
SELECT 'Deployed to production on a Friday',
       'Never again. It was 5pm, the client was pressuring us, and I thought "it is just a small CSS change". Two hours later the entire checkout flow was broken and I was sweating through my shirt trying to roll back a database migration that should not have been in that branch in the first place.',
       'alice',
       'devops',
       42
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Deployed to production on a Friday');

INSERT INTO posts (title, body, author, tag, votes)
SELECT 'Found a TODO comment from 2015',
       'It said: TODO: fix this hack before launch. The launch was in 2015. The system had been running in production for 8 years on this hack. The original developer was long gone. I left the comment in place and added my own below it: Still here, still working, not touching it.',
       'bob',
       'backend',
       87
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Found a TODO comment from 2015');

INSERT INTO posts (title, body, author, tag, votes)
SELECT 'Client said the app was too fast',
       'We optimised the dashboard load time from 8 seconds to 400ms. The client called us the next day complaining that it felt cheap and unfinished. They were used to waiting. We added an artificial 1.5 second loading spinner. They loved it.',
       'alice',
       'frontend',
       134
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Client said the app was too fast');

INSERT INTO posts (title, body, author, tag, votes)
SELECT 'The database that could not be turned off',
       'We tried to migrate a legacy Oracle database that had been running since 2003. Nobody knew the root password. The original DBA had retired. The server was physical hardware under someone''s desk in a different country. The business said it processes 10000 transactions a day and cannot go down. We worked around it for 6 months.',
       'bob',
       'database',
       201
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'The database that could not be turned off');

INSERT INTO posts (title, body, author, tag, votes)
SELECT 'Manager deleted production thinking it was staging',
       'Our manager had SSH access for emergencies. One Friday afternoon he ran rm -rf on what he thought was the staging server. Backups were 3 days old. We spent the weekend reconstructing data from logs, user reports, and one developer who had not pushed their local copy.',
       'alice',
       'devops',
       312
WHERE NOT EXISTS (SELECT 1 FROM posts WHERE title = 'Manager deleted production thinking it was staging');

INSERT INTO comments (post_id, author, body)
SELECT p.id, 'bob', 'I felt this in my soul. Never on Fridays.'
FROM posts p
WHERE p.title = 'Deployed to production on a Friday'
  AND NOT EXISTS (SELECT 1
                  FROM comments c
                  WHERE c.post_id = p.id
                    AND c.author = 'bob');

INSERT INTO comments (post_id, author, body)
SELECT p.id, 'alice', 'The rollback was worse than the deployment'
FROM posts p
WHERE p.title = 'Deployed to production on a Friday'
  AND NOT EXISTS (SELECT 1
                  FROM comments c
                  WHERE c.post_id = p.id
                    AND c.author = 'alice');