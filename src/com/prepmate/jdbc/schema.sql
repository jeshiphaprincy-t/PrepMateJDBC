
USE prepmate_db;

-- 1. Users
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Subjects
CREATE TABLE IF NOT EXISTS subjects (
    subject_id INT PRIMARY KEY AUTO_INCREMENT,
    subject_name VARCHAR(50) UNIQUE NOT NULL
);

-- 3. Topics
CREATE TABLE IF NOT EXISTS topics (
    topic_id INT PRIMARY KEY AUTO_INCREMENT,
    subject_id INT NOT NULL,
    topic_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (subject_id)
        REFERENCES subjects(subject_id),
    UNIQUE (subject_id, topic_name)
);

-- 4. Questions
CREATE TABLE IF NOT EXISTS questions (
    question_id INT PRIMARY KEY AUTO_INCREMENT,
    topic_id INT NOT NULL,
    difficulty ENUM('EASY','MEDIUM','HARD') NOT NULL,
    question_text TEXT NOT NULL,
    explanation TEXT,
    FOREIGN KEY (topic_id)
        REFERENCES topics(topic_id)
);

-- 5. Question options
CREATE TABLE IF NOT EXISTS question_options (
    option_id INT PRIMARY KEY AUTO_INCREMENT,
    question_id INT NOT NULL,
    option_text VARCHAR(500) NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (question_id)
        REFERENCES questions(question_id)
        ON DELETE CASCADE
);

-- 6. Quiz attempts
CREATE TABLE IF NOT EXISTS quiz_attempts (
    attempt_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    subject_id INT NOT NULL,
    total_questions INT NOT NULL,
    correct_answers INT DEFAULT 0,
    score DECIMAL(5,2) DEFAULT 0,
    attempted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

-- 7. Attempt answers
CREATE TABLE IF NOT EXISTS attempt_answers (
    answer_id INT PRIMARY KEY AUTO_INCREMENT,
    attempt_id INT NOT NULL,
    question_id INT NOT NULL,
    selected_option_id INT,
    is_correct BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (attempt_id)
        REFERENCES quiz_attempts(attempt_id)
        ON DELETE CASCADE,
    FOREIGN KEY (question_id)
        REFERENCES questions(question_id),
    FOREIGN KEY (selected_option_id)
        REFERENCES question_options(option_id)
);

-- Insert default subjects
INSERT IGNORE INTO subjects(subject_name)
VALUES ('Core Java'), ('SQL'), ('HTML'), ('CSS');

SHOW TABLES;
