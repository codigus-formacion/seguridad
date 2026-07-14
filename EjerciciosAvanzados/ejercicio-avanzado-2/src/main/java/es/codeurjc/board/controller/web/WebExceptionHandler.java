package es.codeurjc.board.controller.web;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice(basePackages = "es.codeurjc.board.controller.web")
public class WebExceptionHandler {

	@ExceptionHandler(NoSuchElementException.class)
	public ModelAndView handleNotFound(NoSuchElementException ex, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("status", HttpStatus.NOT_FOUND.value());
		mav.addObject("error", "Not Found");
		mav.addObject("message", "The requested resource was not found");
		mav.setStatus(HttpStatus.NOT_FOUND);
		
		// Add CSRF token if available
		String token = (String) request.getAttribute("_csrf.token");
		if (token != null) {
			mav.addObject("token", token);
		}
		
		// Add user info if available
		String username = request.getRemoteUser();
		if (username != null) {
			mav.addObject("loggedUser", true);
			mav.addObject("username", username);
		}
		
		return mav;
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ModelAndView handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("status", ex.getStatusCode().value());
		mav.addObject("error", ex.getReason() != null ? ex.getReason() : ex.getStatusCode().toString());
		mav.addObject("message", ex.getMessage());
		mav.setStatus(ex.getStatusCode());
		
		// Add CSRF token if available
		String token = (String) request.getAttribute("_csrf.token");
		if (token != null) {
			mav.addObject("token", token);
		}
		
		// Add user info if available
		String username = request.getRemoteUser();
		if (username != null) {
			mav.addObject("loggedUser", true);
			mav.addObject("username", username);
		}
		
		return mav;
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ModelAndView handleBadRequest(IllegalArgumentException ex, HttpServletRequest request) {
		ModelAndView mav = new ModelAndView("error");
		mav.addObject("status", HttpStatus.BAD_REQUEST.value());
		mav.addObject("error", "Bad Request");
		mav.addObject("message", ex.getMessage());
		mav.setStatus(HttpStatus.BAD_REQUEST);
		
		// Add CSRF token if available
		String token = (String) request.getAttribute("_csrf.token");
		if (token != null) {
			mav.addObject("token", token);
		}
		
		// Add user info if available
		String username = request.getRemoteUser();
		if (username != null) {
			mav.addObject("loggedUser", true);
			mav.addObject("username", username);
		}
		
		return mav;
	}
}
