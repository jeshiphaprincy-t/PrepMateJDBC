# PrepMate AI 🎯

**Your Personal Interview Preparation Partner**

PrepMate AI is a Java-based interview preparation platform that helps users practise technical questions, evaluate their performance, and identify areas for improvement.

## ✨ Features

- **User Registration & Login** – Secure user authentication.
- **Subject Selection** – Practise Java, SQL, HTML, and CSS.
- **Topic-Based Practice** – Choose specific topics to improve your knowledge.
- **Difficulty Levels** – Easy, Medium, and Hard questions.
- **Interactive Quizzes** – Answer multiple-choice questions.
- **Instant Results** – View scores and correct answers with explanations.
- **Quiz History** – Track previous quiz attempts.
- **Performance Dashboard** – View average scores, highest scores, subject performance, and weak topics.

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java | Backend development |
| JDBC | Database connectivity |
| MySQL | Database |
| HTML | Web page structure |
| CSS | Styling |
| JavaScript | Frontend interactions |
| Eclipse IDE | Development environment |

## 🗂️ Project Structure

```text
PrepMateJDBC/
├── src/
│   └── com/prepmate/
│       ├── connection/
│       ├── model/
│       ├── util/
│       ├── dao/
│       ├── service/
│       └── main/
├── web/
│   ├── css/
│   ├── js/
│   ├── index.html
│   ├── login.html
│   ├── register.html
│   ├── dashboard.html
│   └── subjects.html
├── .gitignore
└── README.md
```

## ⚙️ Setup and Installation

1. Clone this repository.
2. Import the project into Eclipse.
3. Install JDK and MySQL.
4. Create a MySQL database named `prepmate_db`.
5. Import the database schema and question data.
6. Add MySQL Connector/J to the project's build path.
7. Configure the following environment variables:

   - `DB_URL` – `jdbc:mysql://localhost:3306/prepmate_db`
   - `DB_USER` – Your MySQL username.
   - `DB_PASSWORD` – Your MySQL password.

8. Run `WebServer.java`.
9. Open `http://localhost:8080` in your browser.

## 🔐 Security

- Passwords are hashed before being stored.
- Database credentials are configured through environment variables.
- Session-based authentication is used to protect pages.

## 🚀 Future Enhancements

- More technical subjects and question banks.
- Additional performance analytics.
- Timed mock interviews.
- Improved mobile responsiveness.

## 👩‍💻 Author

**Java Developer | Aspiring Software Developer**

---

*PrepMate AI – Prepare smarter. Practice better. Crack your interviews.*
