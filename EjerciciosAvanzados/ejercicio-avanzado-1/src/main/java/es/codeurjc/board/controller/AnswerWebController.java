package es.codeurjc.board.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import es.codeurjc.board.model.Answer;
import es.codeurjc.board.model.Question;
import es.codeurjc.board.service.AnswerService;
import es.codeurjc.board.service.QuestionService;

@Controller
@RequestMapping("/answers")
public class AnswerWebController {

    private final AnswerService answerService;
    private final QuestionService questionService;

    public AnswerWebController(AnswerService answerService, QuestionService questionService) {
        this.answerService = answerService;
        this.questionService = questionService;
    }

    @GetMapping("/new/{questionId}")
    public String newAnswerForm(@PathVariable Long questionId, Model model) {
        Optional<Question> question = questionService.getQuestionById(questionId);
        if (question.isPresent()) {
            model.addAttribute("question", question.get());
            return "answer/new";
        } else {
            return "error";
        }
    }

    @PostMapping("/new/{questionId}")
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

    @GetMapping("/delete/{id}")
    public String deleteAnswer(@PathVariable Long id) {
        Optional<Answer> answer = answerService.getAnswerById(id);
        if (answer.isPresent()) {
            Long questionId = answer.get().getQuestion().getId();
            answerService.deleteAnswer(id);
            return "redirect:/questions/view/" + questionId;
        } else {
            return "error";
        }
    }
}