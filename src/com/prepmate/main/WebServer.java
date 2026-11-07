package com.prepmate.main;

import com.sun.net.httpserver.HttpServer;

import com.sun.net.httpserver.HttpExchange;

import com.prepmate.service.AuthService;

import com.prepmate.model.User;

import java.io.IOException;

import java.net.InetSocketAddress;

import java.net.URLDecoder;

import java.nio.charset.StandardCharsets;

import java.nio.file.Files;

import java.nio.file.Path;

import java.nio.file.Paths;

import java.util.HashMap;

import java.util.Map;

import java.util.UUID;

import java.util.concurrent.ConcurrentHashMap;

import com.prepmate.dao.SubjectDAO;

import com.prepmate.service.QuizService;

import com.prepmate.dao.QuestionOptionDAO;

import com.prepmate.model.Question;

import com.prepmate.model.QuestionOption;

import com.prepmate.dao.TopicDAO;

import java.sql.SQLException;

import java.util.List;

import com.prepmate.dao.QuizDAO;

import com.prepmate.model.QuizAttempt;

import com.prepmate.model.AttemptAnswer;

import java.util.ArrayList;

import java.util.Collections;

public class WebServer {

    private static final AuthService authService = new AuthService();

    private static final QuizService quizService = new QuizService();

    private static final QuestionOptionDAO optionDAO = new QuestionOptionDAO();

    private static final SubjectDAO subjectDAO = new SubjectDAO();

    private static final TopicDAO topicDAO = new TopicDAO();

    private static final QuizDAO quizDAO = new QuizDAO();

    private record ActiveQuiz(

            int userId,

            int topicId,

            List<Question> questions

    ) {}

    private static final Map<String, ActiveQuiz> activeQuizzes =

            new ConcurrentHashMap<>();

    private static final Map<String, User> sessions =

            new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {

        HttpServer server = HttpServer.create(

                new InetSocketAddress(8080), 0);

        server.createContext("/", WebServer::handleRequest);

        server.start();

        System.out.println("PrepMate server started!");

        System.out.println("Open http://localhost:8080");

    }

    private static void handleRequest(HttpExchange exchange)

            throws IOException {

        String path = exchange.getRequestURI().getPath();

        String method = exchange.getRequestMethod();

        // REGISTRATION

        if (path.equals("/register")) {

            if (!method.equals("POST")) {

                sendText(exchange, 405, "Method Not Allowed");

                return;

            }

            Map<String, String> data = readForm(exchange);

            String name = data.get("name");

            String email = data.get("email");

            String password = data.get("password");

            String result = authService.register(

                    name, email, password);

            if (result.equals("Registration successful!")) {

                sendHtml(exchange, 200,

                        "<h2>Registration successful!</h2>" +

                        "<p>Your account has been created.</p>" +

                        "<a href='/login.html'>Login here</a>");

            } else {

                sendHtml(exchange, 400,

                        "<h2>Registration failed</h2>" +

                        "<p>" + escapeHtml(result) + "</p>" +

                        "<a href='/register.html'>Try again</a>");

            }

            return;

        }

        // LOGIN

        if (path.equals("/login")) {

            if (!method.equals("POST")) {

                sendText(exchange, 405, "Method Not Allowed");

                return;

            }

            Map<String, String> data = readForm(exchange);

            String email = data.get("email");

            String password = data.get("password");

            User user = authService.login(email, password);

            if (user != null) {

                String token = UUID.randomUUID().toString();

                sessions.put(token, user);

                exchange.getResponseHeaders().add(

                        "Set-Cookie",

                        "PREPMATE_SESSION=" + token +

                        "; Path=/; HttpOnly; SameSite=Lax; Max-Age=3600"

                );

                redirect(exchange, "/dashboard.html");

            } else {

                sendHtml(exchange, 401,

                        "<h2>Invalid email or password!</h2>" +

                        "<a href='/login.html'>Try again</a>");

            }

            return;

        }

        // LOGOUT

        if (path.equals("/logout")) {

            String token = getSessionToken(exchange);

            if (token != null) {

                sessions.remove(token);

            }

            exchange.getResponseHeaders().add(

                    "Set-Cookie",

                    "PREPMATE_SESSION=; Path=/; HttpOnly; " +

                    "SameSite=Lax; Max-Age=0"

            );

            redirect(exchange, "/login.html");

            return;

        }

     // DISPLAY TOPICS FOR SELECTED SUBJECT

     if (path.equals("/topics")) {

         if (getSessionUser(exchange) == null) {

             redirect(exchange, "/login.html");

             return;

         }

         String subjectName = exchange.getRequestURI()

                 .getQuery();

         if (subjectName == null || !subjectName.startsWith("subject=")) {

             sendText(exchange, 400, "Subject not selected");

             return;

         }

         subjectName = URLDecoder.decode(

                 subjectName.substring("subject=".length()),

                 StandardCharsets.UTF_8

         );

         try {

             int subjectId = -1;

             for (var subject : subjectDAO.getAllSubjects()) {

                 if (subject.getSubjectName().equalsIgnoreCase(subjectName)) {

                     subjectId = subject.getSubjectId();

                     break;

                 }

             }

             if (subjectId == -1) {

                 sendText(exchange, 404, "Subject not found");

                 return;

             }

             var topics = topicDAO.getTopicsBySubject(subjectId);

             StringBuilder html = new StringBuilder();

             html.append("<!DOCTYPE html><html><head>");

             html.append("<title>PrepMate - Topics</title>");

             html.append("<link rel='stylesheet' href='/css/style.css'>");
            html.append("<script src='/js/navbar.js' defer></script>");

             html.append("</head><body>");

             html.append("<div class='subjects-container'>");

             html.append("<h1>").append(escapeHtml(subjectName))

                 .append(" Topics</h1>");

             html.append("<div class='subject-grid'>");

             for (var topic : topics) {

            	    html.append("<a class='subject-card' href='/difficulty?topicId=")

            	        .append(topic.getTopicId())

            	        .append("'>");

            	    html.append("<h2>")

            	        .append(escapeHtml(topic.getTopicName()))

            	        .append("</h2>");

            	    html.append("<span>Start Practising →</span>");

            	    html.append("</a>");

            	}

             if (topics.isEmpty()) {

                 html.append("<p>No topics available for this subject.</p>");

             }

             html.append("</div>");

             html.append("<a href='/subjects.html' class='back-link'>");

             html.append("← Back to Subjects</a>");

             html.append("</div></body></html>");

             sendHtml(exchange, 200, html.toString());

         } catch (SQLException e) {

             e.printStackTrace();

             sendText(exchange, 500, "Unable to load topics");

         }

         return;

     }

  // DIFFICULTY SELECTION

  if (path.equals("/difficulty")) {

      if (getSessionUser(exchange) == null) {

          redirect(exchange, "/login.html");

          return;

      }

      String query = exchange.getRequestURI().getQuery();

      if (query == null || !query.startsWith("topicId=")) {

          sendText(exchange, 400, "Topic not selected");

          return;

      }

      try {

          int topicId = Integer.parseInt(

                  query.substring("topicId=".length())

          );

          StringBuilder html = new StringBuilder();

          html.append("<!DOCTYPE html><html><head>");

          html.append("<title>Choose Difficulty - PrepMate</title>");

          html.append("<link rel='stylesheet' href='/css/style.css'>");
            html.append("<script src='/js/navbar.js' defer></script>");

          html.append("</head><body>");

          html.append("<div class='subjects-container'>");

          html.append("<h1>Choose Difficulty 🎯</h1>");

          html.append("<p>Select your preferred difficulty level.</p>");

          html.append("<div class='subject-grid'>");

          String[] levels = {"EASY", "MEDIUM", "HARD"};

          for (String level : levels) {

              html.append("<a class='subject-card' href='/quiz?topicId=")

                  .append(topicId)

                  .append("&difficulty=")

                  .append(level)

                  .append("'>");

              html.append("<h2>").append(level).append("</h2>");

              html.append("<span>Start Quiz →</span>");

              html.append("</a>");

          }

          html.append("</div>");

          html.append("<a href='/subjects.html' class='back-link'>");

          html.append("← Back to Subjects</a>");

          html.append("</div></body></html>");

          sendHtml(exchange, 200, html.toString());

      } catch (NumberFormatException e) {

          sendText(exchange, 400, "Invalid topic ID");

      }

      return;

  }

//================================

//START QUIZ

//================================

if (path.equals("/quiz")) {

   User user = getSessionUser(exchange);

   if (user == null) {

       redirect(exchange, "/login.html");

       return;

   }

   if (!method.equals("GET")) {

       sendText(exchange, 405, "Method Not Allowed");

       return;

   }

   Map<String, String> params = new HashMap<>();

   String query = exchange.getRequestURI().getRawQuery();

   if (query != null) {

       for (String pair : query.split("&")) {

           String[] parts = pair.split("=", 2);

           params.put(

               URLDecoder.decode(parts[0], StandardCharsets.UTF_8),

               parts.length > 1

                   ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8)

                   : ""

           );

       }

   }

   try {

       int topicId = Integer.parseInt(

               params.getOrDefault("topicId", "")

       );

       String difficulty = params.getOrDefault(

               "difficulty", ""

       ).toUpperCase();

       if (!List.of("EASY", "MEDIUM", "HARD")

               .contains(difficulty)) {

           sendText(exchange, 400, "Invalid difficulty");

           return;

       }

       if (!new com.prepmate.dao.QuestionDAO()

               .topicExists(topicId)) {

           sendText(exchange, 404, "Topic not found");

           return;

       }

       List<Question> questions = new ArrayList<>(

               quizService.getQuizQuestions(

                       topicId, difficulty)

       );

       // Keep the quiz limited to five questions.

       Collections.shuffle(questions);

       if (questions.size() > 5) {

           questions = new ArrayList<>(

                   questions.subList(0, 5)

           );

       }

       if (questions.isEmpty()) {

           sendHtml(exchange, 200,

                   "<h2>No questions available yet.</h2>"

                   + "<a href='/subjects.html'>"

                   + "Choose another subject</a>");

           return;

       }

       String quizToken = UUID.randomUUID().toString();

       activeQuizzes.put(

               quizToken,

               new ActiveQuiz(

                       user.getUserId(),

                       topicId,

                       List.copyOf(questions)

               )

       );

       StringBuilder html = new StringBuilder();

       html.append("<!DOCTYPE html><html><head>");

       html.append("<meta charset='UTF-8'>");

       html.append("<title>PrepMate Quiz</title>");

       html.append("<link rel='stylesheet' ");

       html.append("href='/css/style.css'>");
      html.append("<script src='/js/navbar.js' defer></script>");

       html.append("</head><body>");

       html.append("<div class='subjects-container'>");

       html.append("<h1>PrepMate Quiz</h1>");

       html.append("<p>Difficulty: ")

           .append(escapeHtml(difficulty))

           .append("</p>");

       html.append("<form action='/submit-quiz' ");

       html.append("method='POST'>");

       html.append("<input type='hidden' name='quizToken' value='")

           .append(quizToken).append("'>");

       int number = 1;

       for (Question question : questions) {

           html.append("<div class='quiz-question'>");

           html.append("<h3>")

               .append(number++)

               .append(". ")

               .append(escapeHtml(

                       question.getQuestionText()))

               .append("</h3>");

           List<QuestionOption> options =

                   optionDAO.getOptionsByQuestion(

                           question.getQuestionId());

           for (QuestionOption option : options) {

               html.append("<label class='quiz-option'>");

               html.append("<input type='radio' name='q")

                   .append(question.getQuestionId())

                   .append("' value='")

                   .append(option.getOptionId())

                   .append("'> ");

               html.append(escapeHtml(

                       option.getOptionLabel()))

                   .append(". ");

               html.append(escapeHtml(

                       option.getOptionText()));

               html.append("</label>");

           }

           html.append("</div>");

       }

       html.append("<button class='quiz-submit' ");

       html.append("type='submit'>");

       html.append("Submit Quiz</button>");

       html.append("</form>");

       html.append("</div></body></html>");

       sendHtml(exchange, 200, html.toString());

   } catch (NumberFormatException e) {

       sendText(exchange, 400, "Invalid topic ID");

   } catch (SQLException e) {

       e.printStackTrace();

       sendText(exchange, 500, "Unable to load quiz");

   }

   return;

}

//================================

//SUBMIT QUIZ AND SHOW RESULT

//================================

if (path.equals("/submit-quiz")) {

   User user = getSessionUser(exchange);

   if (user == null) {

       redirect(exchange, "/login.html");

       return;

   }

   if (!method.equals("POST")) {

       sendText(exchange, 405, "Method Not Allowed");

       return;

   }

   Map<String, String> data = readForm(exchange);

   String quizToken = data.get("quizToken");

   ActiveQuiz quiz = activeQuizzes.get(quizToken);

   if (quiz == null ||

           quiz.userId() != user.getUserId()) {

       sendText(exchange, 400,

               "Quiz expired or already submitted");

       return;

   }

   // Prevent the same quiz from being submitted twice.

   if (!activeQuizzes.remove(quizToken, quiz)) {

       sendText(exchange, 400,

               "Quiz already submitted");

       return;

   }

   try {

       List<Question> questions = quiz.questions();

       QuizAttempt attempt = new QuizAttempt(

               user.getUserId(),

               quiz.topicId(),

               questions.size()

       );

       int attemptId = quizDAO.createAttempt(attempt);

       if (attemptId <= 0) {

           sendText(exchange, 500,

                   "Unable to create quiz attempt");

           return;

       }

       int correctCount = 0;

       StringBuilder review = new StringBuilder();

       for (Question question : questions) {

           int questionId = question.getQuestionId();

           String selected = data.get("q" + questionId);

           int selectedOptionId = 0;

           try {

               if (selected != null) {

                   selectedOptionId =

                           Integer.parseInt(selected);

               }

           } catch (NumberFormatException ignored) {

               selectedOptionId = 0;

           }

           List<QuestionOption> options =

                   optionDAO.getOptionsByQuestion(questionId);

           boolean correct = false;

           String selectedLabel = "Not answered";

           String correctLabel = "Not available";

           int validSelectedId = 0;

           for (QuestionOption option : options) {

               if (option.isCorrect()) {

                   correctLabel =

                           option.getOptionLabel() + ". "

                           + option.getOptionText();

               }

               if (option.getOptionId() ==

                       selectedOptionId) {

                   validSelectedId = selectedOptionId;

                   selectedLabel =

                           option.getOptionLabel() + ". "

                           + option.getOptionText();

                   correct = option.isCorrect();

               }

           }

           if (correct) {

               correctCount++;

           }

           AttemptAnswer answer = new AttemptAnswer(

                   attemptId,

                   questionId,

                   validSelectedId > 0

                           ? selectedLabel : null,

                   correct

           );

           answer.setSelectedOptionId(validSelectedId);

           quizDAO.saveAnswer(answer);

           review.append("<div class='quiz-question'>");

           review.append("<h3>")

               .append(escapeHtml(

                       question.getQuestionText()))

               .append("</h3>");

           review.append("<p>Your answer: ")

               .append(escapeHtml(selectedLabel))

               .append("</p>");

           review.append("<p>Correct answer: ")

               .append(escapeHtml(correctLabel))

               .append("</p>");

           review.append("<p><strong>")

               .append(correct ? "Correct!" : "Incorrect")

               .append("</strong></p>");

           if (question.getExplanation() != null) {

               review.append("<p>Explanation: ")

                   .append(escapeHtml(

                           question.getExplanation()))

                   .append("</p>");

           }

           review.append("</div>");

       }

       double percentage =

               correctCount * 100.0 / questions.size();

       quizDAO.completeAttempt(

               attemptId, correctCount, percentage

       );

       StringBuilder html = new StringBuilder();

       html.append("<!DOCTYPE html><html><head>");

       html.append("<meta charset='UTF-8'>");

       html.append("<title>Quiz Result - PrepMate</title>");

       html.append("<link rel='stylesheet' ");

       html.append("href='/css/style.css'>");
      html.append("<script src='/js/navbar.js' defer></script>");

       html.append("</head><body>");

       html.append("<div class='subjects-container'>");

       html.append("<h1>Quiz Completed!</h1>");

       html.append("<div class='quiz-question'>");

       html.append("<h2>Your Score: ")

           .append(correctCount)

           .append(" / ")

           .append(questions.size())

           .append("</h2>");

       html.append("<h2>")

           .append(String.format(

                   java.util.Locale.US,

                   "%.1f%%", percentage))

           .append("</h2>");

       html.append("</div>");

       html.append("<h2>Answer Review</h2>");

       html.append(review);

       html.append("<div class='result-actions'>");

       html.append("<a href='/subjects.html' class='btn-primary'>");
       html.append("Practise Again →</a>");

       html.append("<a href='/dashboard.html' class='btn-secondary'>");
       html.append("← Back to Dashboard</a>");

       html.append("</div>");

       html.append("</div></body></html>");

       sendHtml(exchange, 200, html.toString());

   } catch (SQLException e) {

       e.printStackTrace();

       sendText(exchange, 500,

               "Database error while saving quiz");

   }

   return;

}

//QUIZ HISTORY

if (path.equals("/history")) {

 User user = getSessionUser(exchange);

 if (user == null) {

     redirect(exchange, "/login.html");

     return;

 }

 if (!method.equals("GET")) {

     sendText(exchange, 405, "Method Not Allowed");

     return;

 }

 try {

     List<QuizAttempt> attempts =

             quizDAO.getAttemptsByUser(user.getUserId());

     StringBuilder html = new StringBuilder();

     html.append("<!DOCTYPE html><html><head>");

     html.append("<meta charset='UTF-8'>");

     html.append("<title>Quiz History - PrepMate</title>");

     html.append("<link rel='stylesheet' href='/css/style.css'>");
            html.append("<script src='/js/navbar.js' defer></script>");

     html.append("</head><body>");

     html.append("<div class='subjects-container'>");

     html.append("<h1>My Quiz History 📚</h1>");

     if (attempts.isEmpty()) {

         html.append("<p>You haven't attempted any quizzes yet.</p>");

     } else {

         for (QuizAttempt attempt : attempts) {

             html.append("<div class='quiz-question'>");

             html.append("<h2>Topic ID: ")

                 .append(attempt.getTopicId())

                 .append("</h2>");

             html.append("<p>Score: ")

                 .append(attempt.getCorrectAnswers())

                 .append(" / ")

                 .append(attempt.getTotalQuestions())

                 .append("</p>");

             html.append("<p>Percentage: ")

                 .append(String.format(

                         java.util.Locale.US,

                         "%.1f%%",

                         attempt.getScore()))

                 .append("</p>");

             html.append("<p>Date: ")

                 .append(attempt.getStartedAt() != null

                         ? escapeHtml(attempt.getStartedAt().toString())

                         : "N/A")

                 .append("</p>");

             html.append("</div>");

         }

     }

     html.append("<a href='/dashboard.html' class='back-link'>");

     html.append("← Back to Dashboard</a>");

     html.append("</div></body></html>");

     sendHtml(exchange, 200, html.toString());

 } catch (SQLException e) {

     e.printStackTrace();

     sendText(exchange, 500, "Unable to load quiz history");

 }

 return;

}

//PERFORMANCE DASHBOARD
if (path.equals("/performance")) {

 User user = getSessionUser(exchange);

 if (user == null) {
     redirect(exchange, "/login.html");
     return;
 }

 if (!method.equals("GET")) {
     sendText(exchange, 405, "Method Not Allowed");
     return;
 }

 try {
     int userId = user.getUserId();

     int totalAttempts = quizDAO.getTotalAttempts(userId);
     int totalAnswered = quizDAO.getTotalQuestionsAnswered(userId);

     Map<String, Double> stats =
             quizDAO.getScoreStatistics(userId);

     Map<String, Double> subjectPerformance =
             quizDAO.getSubjectWisePerformance(userId);

     Map<String, Double> weakTopics =
             quizDAO.getWeakTopics(userId);

     String recommendedTopic =
             quizDAO.getRecommendedTopic(userId);

     double average = stats.getOrDefault("average", 0.0);
     double highest = stats.getOrDefault("highest", 0.0);

     StringBuilder html = new StringBuilder();

     html.append("<!DOCTYPE html><html><head>");
     html.append("<meta charset='UTF-8'>");
     html.append("<meta name='viewport' content='width=device-width, initial-scale=1'>");
     html.append("<title>Performance - PrepMate</title>");
     html.append("<link rel='stylesheet' href='/css/style.css'>");
     html.append("<script src='/js/navbar.js' defer></script>");
     html.append("</head><body>");

     html.append("<div class='subjects-container'>");
     html.append("<h1>My Performance 📊</h1>");
     html.append("<p>Track your progress and find areas to improve.</p>");

     // SUMMARY CARDS
     html.append("<div class='performance-grid'>");

     html.append("<div class='performance-card'>");
     html.append("<h3>Total Attempts</h3>");
     html.append("<h2>").append(totalAttempts).append("</h2>");
     html.append("</div>");

     html.append("<div class='performance-card'>");
     html.append("<h3>Questions Answered</h3>");
     html.append("<h2>").append(totalAnswered).append("</h2>");
     html.append("</div>");

     html.append("<div class='performance-card'>");
     html.append("<h3>Average Score</h3>");
     html.append("<h2>").append(String.format(
             java.util.Locale.US, "%.1f%%", average)).append("</h2>");
     html.append("</div>");

     html.append("<div class='performance-card'>");
     html.append("<h3>Highest Score</h3>");
     html.append("<h2>").append(String.format(
             java.util.Locale.US, "%.1f%%", highest)).append("</h2>");
     html.append("</div>");

     html.append("</div>");

     // SUBJECT-WISE PERFORMANCE
     html.append("<div class='performance-section'>");
     html.append("<h2>Subject-wise Performance</h2>");

     if (subjectPerformance.isEmpty()) {
         html.append("<p>Attempt a quiz to see your subject performance.</p>");
     } else {
         for (Map.Entry<String, Double> entry :
                 subjectPerformance.entrySet()) {

             double score = entry.getValue();

             html.append("<div class='performance-row'>");
             html.append("<strong>")
                 .append(escapeHtml(entry.getKey()))
                 .append("</strong>");

             html.append("<div class='progress-track'>");
             html.append("<div class='progress-fill' style='width:")
                 .append(Math.max(0, Math.min(100, score)))
                 .append("%'></div>");
             html.append("</div>");

             html.append("<span>")
                 .append(String.format(
                         java.util.Locale.US, "%.1f%%", score))
                 .append("</span>");

             html.append("</div>");
         }
     }

     html.append("</div>");

     // WEAK TOPICS
     html.append("<div class='performance-section'>");
     html.append("<h2>Topics to Improve 💡</h2>");

     if (weakTopics.isEmpty()) {
         html.append("<p>No weak topics found yet. Keep practising!</p>");
     } else {
         for (Map.Entry<String, Double> entry :
                 weakTopics.entrySet()) {

             html.append("<div class='weak-topic'>");
             html.append("<strong>")
                 .append(escapeHtml(entry.getKey()))
                 .append("</strong>");

             html.append("<span>")
                 .append(String.format(
                         java.util.Locale.US,
                         "%.1f%% average", entry.getValue()))
                 .append("</span>");

             html.append("</div>");
         }
     }

     html.append("</div>");

     // RECOMMENDATION
     html.append("<div class='recommendation-card'>");
     html.append("<h2>Recommended Next Practice 🎯</h2>");
     html.append("<p>")
         .append(escapeHtml(recommendedTopic))
         .append("</p>");
     html.append("<a href='/subjects.html'>Start Practising →</a>");
     html.append("</div>");

     html.append("</div></body></html>");

     sendHtml(exchange, 200, html.toString());

 } catch (SQLException e) {
     e.printStackTrace();
     sendText(exchange, 500, "Unable to load performance dashboard");
 }

 return;
}

        // PROTECT DASHBOARD

        if (path.equals("/dashboard.html")) {

            if (getSessionUser(exchange) == null) {

                redirect(exchange, "/login.html");

                return;

            }

        }

        // SERVE HTML AND CSS FILES

        serveStaticFile(exchange, path);

    }

    private static Map<String, String> readForm(

            HttpExchange exchange) throws IOException {

        byte[] body = exchange.getRequestBody().readAllBytes();

        String form = new String(body, StandardCharsets.UTF_8);

        Map<String, String> data = new HashMap<>();

        for (String pair : form.split("&")) {

            String[] parts = pair.split("=", 2);

            String key = URLDecoder.decode(

                    parts[0], StandardCharsets.UTF_8);

            String value = parts.length > 1

                    ? URLDecoder.decode(

                            parts[1], StandardCharsets.UTF_8)

                    : "";

            data.put(key, value);

        }

        return data;

    }

    private static String getSessionToken(HttpExchange exchange) {

        String cookie = exchange.getRequestHeaders()

                .getFirst("Cookie");

        if (cookie == null) {

            return null;

        }

        for (String item : cookie.split(";")) {

            String part = item.trim();

            if (part.startsWith("PREPMATE_SESSION=")) {

                return part.substring(

                        "PREPMATE_SESSION=".length());

            }

        }

        return null;

    }

    private static User getSessionUser(HttpExchange exchange) {

        String token = getSessionToken(exchange);

        if (token == null) {

            return null;

        }

        return sessions.get(token);

    }

    private static void serveStaticFile(

            HttpExchange exchange, String requestedPath)

            throws IOException {

        if (requestedPath.equals("/")) {

            requestedPath = "/index.html";

        }

        Path webFolder = Paths.get("web")

                .toAbsolutePath().normalize();

        Path file = webFolder.resolve(

                requestedPath.substring(1)).normalize();

        if (!file.startsWith(webFolder) ||

                !Files.isRegularFile(file)) {

            sendText(exchange, 404, "404 - Page Not Found");

            return;

        }

        String contentType = "text/html; charset=UTF-8";

        if (requestedPath.endsWith(".css")) {

            contentType = "text/css; charset=UTF-8";

        } else if (requestedPath.endsWith(".js")) {

            contentType = "application/javascript; charset=UTF-8";

        }

        byte[] response = Files.readAllBytes(file);

        exchange.getResponseHeaders().set(

                "Content-Type", contentType);

        exchange.sendResponseHeaders(200, response.length);

        try (var output = exchange.getResponseBody()) {

            output.write(response);

        }

    }

    private static void redirect(

            HttpExchange exchange, String location)

            throws IOException {

        exchange.getResponseHeaders().set("Location", location);

        exchange.sendResponseHeaders(303, -1);

        exchange.close();

    }

    private static void sendText(

            HttpExchange exchange, int status, String message)

            throws IOException {

        byte[] response = message.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(

                "Content-Type", "text/plain; charset=UTF-8");

        exchange.sendResponseHeaders(status, response.length);

        try (var output = exchange.getResponseBody()) {

            output.write(response);

        }

    }

    private static void sendHtml(

            HttpExchange exchange, int status, String html)

            throws IOException {

        byte[] response = html.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(

                "Content-Type", "text/html; charset=UTF-8");

        exchange.sendResponseHeaders(status, response.length);

        try (var output = exchange.getResponseBody()) {

            output.write(response);

        }

    }

    private static String escapeHtml(String text) {

        if (text == null) return "";

        return text.replace("&", "&amp;")

                   .replace("<", "&lt;")

                   .replace(">", "&gt;")

                   .replace("\"", "&quot;")

                   .replace("'", "&#39;");

    }

}
