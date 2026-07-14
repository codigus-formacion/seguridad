package es.codeurjc.board.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import es.codeurjc.board.model.Answer;
import es.codeurjc.board.model.Question;
import es.codeurjc.board.service.AnswerService;
import es.codeurjc.board.service.QuestionService;

@Controller
public class QuestionWebController {

    private final QuestionService questionService;
    private final AnswerService answerService;

    public QuestionWebController(QuestionService questionService, AnswerService answerService) {
        this.questionService = questionService;
        this.answerService = answerService;
    }

    @GetMapping("/")
    public String listQuestions(Model model) {
        List<Question> questions = questionService.getAllQuestions();
        model.addAttribute("questions", questions);
        return "question/list";
    }

    @GetMapping("/questions/new")
    public String newQuestionForm(Model model) {
        model.addAttribute("question", new Question());
        return "question/new";
    }

    @PostMapping("/questions/new")
    public String saveQuestion(@ModelAttribute Question question) {
        questionService.saveQuestion(question);
        return "redirect:/";
    }

    @GetMapping("/questions/view/{id}")
    public String viewQuestion(@PathVariable Long id, Model model) {
        Optional<Question> op = questionService.getQuestionById(id);
        if (op.isPresent()) {
            Question question = op.get();
            question.setCode("<code class=\"language-"+question.getLanguage()+"\">"+question.getCode()+"</code>");
            model.addAttribute("question", question);
            return "question/view";
        } else {
            model.addAttribute("message", "Pregunta no encontrada");
            return "error";
        }
    }

    @GetMapping("/questions/edit/{id}")
    public String editQuestionForm(@PathVariable Long id, Model model) {
        Optional<Question> question = questionService.getQuestionById(id);
        if (question.isPresent()) {
            model.addAttribute("question", question.get());
            return "question/edit";
        } else {
            return "error";
        }
    }

    @PostMapping("/questions/edit")
    public String updateQuestion(@ModelAttribute Question question) {
        questionService.saveQuestion(question);
        return "redirect:/";
    }

    @GetMapping("/questions/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return "redirect:/";
    }

    // Answer handling

    @PostMapping("/questions/{questionId}/answers/new")
    public String saveAnswer(@PathVariable Long questionId, @ModelAttribute Answer answer) {
        Optional<Question> question = questionService.getQuestionById(questionId);
        if (question.isPresent()) {
            answer.setQuestion(question.get());
            answerService.saveAnswer(answer);
            return "redirect:/questions/view/" + questionId;
        } else {
            return "error";
        }
    }

    @PostMapping("/questions/{questionId}/answers/{id}/delete")
    public String deleteAnswer(@PathVariable Long questionId, @PathVariable Long id) {
        Optional<Answer> answer = answerService.getAnswerById(id);
        if (answer.isPresent()) {
            answerService.deleteAnswer(id);
            return "redirect:/questions/view/" + questionId;
        } else {
            return "error";
        }
    }
}