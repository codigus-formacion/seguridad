package es.codeurjc.board.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import es.codeurjc.board.model.Question;
import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {
    
    @Autowired
    private QuestionService questionService;

    @PostConstruct
    public void init() {
        questionService.saveQuestion(new Question(
            "¿Cómo puedo crear una relación 1:N?",
            "Quiero hacer una relación entre Question y Answer, pero no sé cómo hacerlo. Este es mi código", 
            "private List<Answer> answers = new ArrayList<>();", 
            "java"));

        questionService.saveQuestion(new Question(
            "¿Cómo puedo usar streams en Java?",
            "Estoy tratando de filtrar una lista de objetos, pero no entiendo cómo funcionan los streams. ¿Alguien puede ayudarme?", 
            "List<String> filtered = list.stream().filter(s -> s.startsWith(\"A\")).collect(Collectors.toList());", 
            "java"));

        questionService.saveQuestion(new Question(
            "¿Cómo puedo manejar excepciones en Python?",
            "Quiero capturar errores específicos en mi código, pero no estoy seguro de cómo usar try-except correctamente.", 
            "try:\n    result = 10 / 0\nexcept ZeroDivisionError:\n    print(\"No se puede dividir por cero\")", 
            "python"));

        questionService.saveQuestion(new Question(
            "¿Cómo puedo manipular el DOM en JavaScript?",
            "Estoy intentando cambiar el texto de un elemento HTML, pero no estoy seguro de cómo hacerlo.", 
            "document.getElementById(\"myElement\").innerText = \"Nuevo texto\";", 
            "javascript"));

    }
}
